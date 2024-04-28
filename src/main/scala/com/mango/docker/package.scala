package com.mango

import zio._
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.transport.DockerHttpClient
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import zio.json._
import com.mango.docker.services.DockerService
import com.github.dockerjava.api.model.Container
import com.mango.docker.grpc.DockerGrpcService

package object docker {

  object Dependecies {

    val dockerClient: ZLayer[Any, Throwable, DockerClient] = ZLayer.fromZIO(
      ZIO.attempt {
        val config = DefaultDockerClientConfig.createDefaultConfigBuilder().build()

        val httpClient = new ApacheDockerHttpClient.Builder()
          .dockerHost(config.getDockerHost())
          .sslConfig(config.getSSLConfig())
          .maxConnections(100)
          .build()

        DockerClientImpl.getInstance(config, httpClient)
      },
    )
  }
}
