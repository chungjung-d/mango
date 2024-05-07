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
import com.mango.docker.services.DockerLifetimeService
import com.mango.docker.utils.DockerUtils

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

    val dockerService =
      ZLayer.make[DockerService](dockerClient, DockerService.layer, DockerUtils.layer)

    val dockerLifetimeService = ZLayer.make[DockerLifetimeService](DockerLifetimeService.layer)

    // case class DockerLifeCycleSchedule private ()

    // val dockerLifeCycleSchedule
    //     : ZLayer[DockerLifetimeService with DockerService, Nothing, DockerLifeCycleSchedule] =
    //   ZLayer.fromZIO {
    //     DockerLifetimeService
    //       .getContainersToKill()
    //       .flatMap(ZIO.foreachDiscard(_)(DockerService.stopContainer(_).ignore))
    //       .repeat(Schedule.spaced(5.minutes))
    //       .forkDaemon
    //       .as(DockerLifeCycleSchedule())
    //   }
  }
}
