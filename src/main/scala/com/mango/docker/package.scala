package com.mango

import zio._
import com.github.dockerjava.api.DockerClient
import com.github.dockerjava.core.DefaultDockerClientConfig
import com.github.dockerjava.transport.DockerHttpClient
import com.github.dockerjava.core.DockerClientImpl
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient
import zio.http._
import zio.http.codec.PathCodec._
import zio.json._
import com.mango.docker.services.DockerService
import com.github.dockerjava.api.model.Container

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
      }
    )

    val dockerService: ZLayer[Any, Throwable, DockerService] = dockerClient >>> DockerService.layer

  }

  object Router {
    val dockerContainerListRouter: Route[DockerService, Nothing] =
      Method.GET / "container" / "list" -> handler { (req: Request) =>
        (
          DockerService.listContainers
            .mapBoth(
              e => Response.internalServerError(e.getMessage()),
              containers => Response.json(containers.toJson.toString)
            )
            .merge
        )
      }

    val dockerImageListRouter: Route[DockerService, Nothing] =
      Method.GET / "image" / "list" -> handler { (_: Request) =>
        (
          DockerService.listImages
            .mapBoth(
              e => Response.internalServerError(e.getMessage()),
              images => Response.json(images.toJson.toString)
            )
            .merge
        )
      }

    val routes = literal("docker") / Routes(
      dockerContainerListRouter,
      dockerImageListRouter
    )
  }

}
