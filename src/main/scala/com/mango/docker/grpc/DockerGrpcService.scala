package com.mango.docker.grpc

import zio._

import com.mango.grpc._
import com.mango.docker.services.DockerService
import scalapb.zio_grpc.RequestContext

case class DockerGrpcService(dockerService: DockerService) extends ZioDocker.RCDocker {

  def listContainers(request: Empty, context: RequestContext): IO[io.grpc.StatusException, ListContainersRes] =
    for {
      containers <- dockerService.listContainers.mapError(_ =>
        new io.grpc.StatusException(io.grpc.Status.INTERNAL),
      )
      containerRes = containers.map { container =>
        ListContainersRes.DockerContainer(container.id, container.status ,container.name)
      }
    } yield ListContainersRes(containerRes)


    def listImages(request: Empty, context: RequestContext): IO[io.grpc.StatusException, ListImagesRes] =
    for {
      images <- dockerService.listImages.mapError(_ =>
        new io.grpc.StatusException(io.grpc.Status.INTERNAL),
      )
      imageRes = images.map { image =>
        ListImagesRes.DockerImage(image.id, image.name, image.tags)
      }
    } yield ListImagesRes(imageRes)

    def createContainer(request: CreateContainerReq, context: RequestContext): IO[io.grpc.StatusException, CreateContainerRes] =
    for {
      container <- dockerService.createContainer(request.imageId, request.containerName).mapError(_ =>
        new io.grpc.StatusException(io.grpc.Status.INTERNAL),
      )
    } yield CreateContainerRes(container.id, container.status ,container.name)

}

object DockerGrpcService {
  val layer: ZLayer[DockerService, Nothing, DockerGrpcService] =
    ZLayer.fromFunction(DockerGrpcService(_))
}
