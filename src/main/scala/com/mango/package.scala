package com

import com.mango.docker.Dependecies
import scalapb.zio_grpc.ServerLayer
import scalapb.zio_grpc.Server
import scalapb.zio_grpc.{ServiceList, ServerMain}
import com.mango.docker.services.DockerService
import com.mango.grpc.ZioDocker
import zio._
import io.grpc.protobuf.services.ProtoReflectionService
import com.mango.docker.services.DockerLifetimeService

package object mango {

  object Dependecies {

    val serviceList = ServiceList.addFromEnvironment[ZioDocker.RCDocker]

    val serverLayer =
      ServerLayer.fromServiceList(
        io.grpc.ServerBuilder
          .forPort(9090)
          .addService(ProtoReflectionService.newInstance()),
        serviceList,
      )

    val mangoGrpcApp: ZLayer[Any, Throwable, Server] =
      ZLayer.make[Server](
        serverLayer,
        docker.Dependecies.dockerClient,
        docker.services.DockerService.layer,
        docker.grpc.DockerGrpcService.layer,
        docker.utils.DockerUtils.layer,
      )

    val dockerLifeCycleSchedulerLayer =
      (docker.Dependecies.dockerService ++ docker.Dependecies.dockerLifetimeService)

  }

}
