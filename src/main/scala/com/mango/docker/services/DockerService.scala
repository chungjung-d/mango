package com.mango.docker.services

import zio._
import zio.macros.accessible
import scala.jdk.CollectionConverters._
import com.github.dockerjava.api.DockerClient
import com.mango.docker.model._
import com.mango.docker.grpc
import com.mango.docker.utils.DockerUtils
import com.mango.grpc.ListImagesRes

@accessible
trait DockerService {
  def listContainers: Task[List[DockerContainer]]
  def listImages: Task[List[DockerImage]]

  def createContainer(imageId: String, containerName: String): Task[DockerContainer]

}

case class DockerServiceImpl(client: DockerClient, utils: DockerUtils) extends DockerService {
  def listContainers: Task[List[DockerContainer]] =
    for {
      containers <- ZIO.attemptBlocking(
        client
          .listContainersCmd()
          .withShowAll(true)
          .exec()
          .asScala
          .toList,
      )
      dockerContainers <- ZIO.foreach(containers)(utils.toDockerContainer)
    } yield dockerContainers

  def listImages: Task[List[DockerImage]] =
    for {
      images       <- ZIO.attemptBlocking(client.listImagesCmd().exec().asScala.toList)
      dockerImages <- ZIO.foreach(images)(utils.toDockerImage)
    } yield dockerImages

  def createContainer(imageId: String, containerName: String): Task[DockerContainer] = {
    val createContainerCmd = client.createContainerCmd(imageId).withName(containerName)
    for {
      containerId     <- ZIO.attemptBlocking(createContainerCmd.exec().getId)
      container       <- ZIO.attemptBlocking(client.inspectContainerCmd(containerId).exec())
      dockerContainer <- utils.toDockerContainer(container)
    } yield dockerContainer
  }

}

object DockerService {
  val layer: ZLayer[DockerClient with DockerUtils, Nothing, DockerService] =
    ZLayer.fromFunction((client: DockerClient, utils: DockerUtils) =>
      DockerServiceImpl(client, utils),
    )

  // val test: ZLayer[Any, Nothing, DockerService] = ZLayer.succeed(
  //     new DockerService {
  //             def listContainers: Task[List[String]] =
  //                 ZIO.succeed(List.empty)
  //     }
  // )
}
