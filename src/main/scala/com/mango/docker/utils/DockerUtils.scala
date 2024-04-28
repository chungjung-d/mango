package com.mango.docker.utils

import zio._
import com.mango.docker.model.DockerImage
import com.github.dockerjava.api.model.Container
import com.github.dockerjava.api.command.InspectContainerResponse

import com.mango.docker.model.DockerContainer
import com.github.dockerjava.api.model.Image

trait DockerUtils {
  def toDockerContainer(container: Container): Task[DockerContainer]
  def toDockerContainer(container: InspectContainerResponse): Task[DockerContainer]
  def toDockerImage(image: Image): Task[DockerImage]
}

case class DockerUtilsImpl() extends DockerUtils {

  def toDockerContainer(container: InspectContainerResponse): Task[DockerContainer] = ZIO.attempt {

    val name = Option(container.getName())

    DockerContainer(container.getId, container.getState.getStatus, name)
  }

  def toDockerContainer(container: Container): Task[DockerContainer] = ZIO.attempt {

    val name = Option(container.getNames()).flatMap(_.headOption)

    DockerContainer(container.getId, container.getStatus, name)
  }

  def toDockerImage(image: Image): Task[DockerImage] = ZIO.attempt {

    val tags = Option(image.getRepoTags()).map(_.toList).getOrElse(List.empty)
    val name = tags.headOption.flatMap(_.split(":").headOption)

    DockerImage(image.getId, name, tags)

  }

}

object DockerUtils {
  val layer: ZLayer[Any, Nothing, DockerUtils] =
    ZLayer.succeed(DockerUtilsImpl())
}
