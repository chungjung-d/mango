package com.mango.docker.services

import zio._
import zio.macros.accessible
import scala.jdk.CollectionConverters._
import com.github.dockerjava.api.DockerClient
import com.mango.docker.model._

@accessible
trait DockerService {
  def listContainers: Task[List[DockerContainer]]
  def listImages: Task[List[DockerImage]]
}

case class DockerServiceImpl(client: DockerClient) extends DockerService {
  def listContainers: Task[List[DockerContainer]] = ZIO.attemptBlocking {
    client.listContainersCmd().exec().asScala.toList.map { containter =>
      val name = containter.getNames().headOption

      DockerContainer(containter.getId, name)
    }
  }

  def listImages: Task[List[DockerImage]] = ZIO.attemptBlocking {
    client.listImagesCmd().exec().asScala.toList.map { image =>
      val tags = image.getRepoTags.toList
      val name = tags.headOption.flatMap(_.split(":").headOption)

      DockerImage(image.getId, name, tags)
    }
  }
}

object DockerService {
  val layer: ZLayer[DockerClient, Nothing, DockerService] =
    ZLayer.fromFunction((client: DockerClient) => DockerServiceImpl(client))

  // val test: ZLayer[Any, Nothing, DockerService] = ZLayer.succeed(
  //     new DockerService {
  //             def listContainers: Task[List[String]] =
  //                 ZIO.succeed(List.empty)
  //     }
  // )
}
