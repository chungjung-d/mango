package com

import com.mango.docker.Dependecies
import zio.http._

package object mango {
  object Dependecies {
    val services = docker.Dependecies.dockerService ++ Server.default
  }

  object Router {
    val routes = docker.Router.routes
  }
}
