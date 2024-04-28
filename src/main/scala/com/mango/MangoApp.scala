package com.mango

import zio._
import zio.json._
import zio.Console._
import java.io.IOException
import scalapb.zio_grpc.ServerMain
import scalapb.zio_grpc.Server

object MangoApp extends ZIOAppDefault {

  def run =
    Dependecies.mangoApp.launch.exitCode


}
