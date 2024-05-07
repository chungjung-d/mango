package com.mango.docker.services

import zio.macros.accessible
import zio._
import java.time.LocalDateTime
import java.time.OffsetTime
import java.time.OffsetDateTime

@accessible
trait DockerLifetimeService {

  def setContainerLifespan(containerId: String, lifespan: Duration): UIO[Unit]
  def renewAliveTime(containerId: String): Task[Unit]
  def getContainersToKill(): UIO[List[String]]
}

case class DockerLifetimeServiceImpl(
    lifespanByContainerId: Ref[Map[String, Duration]],
    aliveTimeByContainerId: Ref[Map[String, OffsetDateTime]],
) extends DockerLifetimeService {

  def setContainerLifespan(containerId: String, lifespan: Duration): UIO[Unit] =
    lifespanByContainerId.update(_.updated(containerId, lifespan))

  def renewAliveTime(containerId: String): Task[Unit] =
    for {
      now <- Clock.currentDateTime
      lifespan <- lifespanByContainerId.get
        .map(_.get(containerId))
        .someOrFail(new Exception("Container not registered"))
      _ <- aliveTimeByContainerId.update(_.updated(containerId, now.plus(lifespan)))
    } yield {}

  def getContainersToKill(): UIO[List[String]] =
    for {
      now        <- Clock.currentDateTime
      aliveTimes <- aliveTimeByContainerId.get
      containersToKill = aliveTimes.filter { case (_, aliveTime) =>
        aliveTime.isBefore(now)
      }.keys.toList
    } yield containersToKill

}

object DockerLifetimeService {

  val layer: ULayer[DockerLifetimeServiceImpl] = ZLayer.fromZIO(
    for {
      lifespanByContainerId  <- Ref.make(Map.empty[String, Duration])
      aliveTimeByContainerId <- Ref.make(Map.empty[String, OffsetDateTime])
    } yield DockerLifetimeServiceImpl(lifespanByContainerId, aliveTimeByContainerId),
  )
}
