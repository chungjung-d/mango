package com.mango

import zio._
import zio.json._
import zio.Console._
import java.io.IOException
import scalapb.zio_grpc.ServerMain
import scalapb.zio_grpc.Server
import com.mango.docker.services.DockerLifetimeService
import com.mango.docker.services.DockerService

object Mangoapp extends ZIOAppDefault {

  val dockerLifeCycleScheduler: Task[Long] =
    DockerLifetimeService
      .getContainersToKill()
      .flatMap(ZIO.foreachDiscard(_)(DockerService.stopContainer(_).ignore))
      .repeat(Schedule.spaced(5.seconds))
      .provideLayer(Dependecies.dockerLifeCycleSchedulerLayer)

  def run =
    (Dependecies.mangoGrpcApp.launch &> dockerLifeCycleScheduler).exitCode

}
