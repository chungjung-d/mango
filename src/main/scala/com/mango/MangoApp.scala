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

  def run =
    (Zio.mangoGrpcZio &> Zio.dockerLifeCycleSchedulerZio).exitCode

}
