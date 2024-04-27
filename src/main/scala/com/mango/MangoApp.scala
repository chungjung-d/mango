package com.mango

import zio._
import zio.json._
import zio.Console._
import zio.http.codec.PathCodec._
import zio.http._
import java.io.IOException

object MangoApp extends ZIOAppDefault {

  private val app = (literal("api") / Router.routes).toHttpApp

  override val run =
    Server.serve(app).provide(Dependecies.services)
}
