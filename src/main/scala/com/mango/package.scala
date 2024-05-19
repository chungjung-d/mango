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

    val mangoGrpcServerLayer =
      ServerLayer.fromServiceList(
        io.grpc.ServerBuilder
          .forPort(9090)
          .addService(ProtoReflectionService.newInstance()),
        serviceList,
      )

    val dockerLifeCycleSchedulerLayer = ZLayer.make[DockerService with DockerLifetimeService](
      docker.Dependecies.dockerClient,
      docker.services.DockerService.layer,
      docker.utils.DockerUtils.layer,
      docker.services.DockerLifetimeService.layer,
    )

  }

  object Zio {

    val mangoGrpcZio =
      ZLayer
        .make[Server](
          Dependecies.mangoGrpcServerLayer,
          docker.Dependecies.dockerClient,
          docker.services.DockerService.layer,
          docker.grpc.DockerGrpcService.layer,
          docker.utils.DockerUtils.layer,
        )
        .launch

    val dockerLifeCycleSchedulerZio: Task[Long] =
      DockerLifetimeService
        .getContainersToKill()
        .flatMap(ZIO.foreachDiscard(_)(DockerService.stopContainer(_).ignore))
        .repeat(Schedule.spaced(5.seconds))
        .provideLayer(Dependecies.dockerLifeCycleSchedulerLayer)

  }

}
