package com.mango.docker.model

import zio.json._
case class DockerImage(id: String, name: Option[String], tags: List[String])

object DockerImage {

  implicit val decoder: JsonDecoder[DockerImage] = DeriveJsonDecoder.gen[DockerImage]
  implicit val encoder: JsonEncoder[DockerImage] = DeriveJsonEncoder.gen[DockerImage]
}

