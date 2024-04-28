package com.mango.docker.model

import zio.json._

case class DockerContainer(id: String, status: String ,name: Option[String])

object DockerContainer {

  implicit val decoder: JsonDecoder[DockerContainer] = DeriveJsonDecoder.gen[DockerContainer]
  implicit val encoder: JsonEncoder[DockerContainer] = DeriveJsonEncoder.gen[DockerContainer]
}
