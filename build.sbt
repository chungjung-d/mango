import Dependencies._
import scala.sys.process._

ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0-SNAPSHOT"
ThisBuild / organization     := "com.mango"
ThisBuild / organizationName := "Mango"

scalacOptions += "-Ymacro-annotations"

lazy val root = (project in file("."))
  .settings(
    name := "com.mango",
    libraryDependencies ++= dependecies,
  )

resolvers ++= Seq(
  "Maven Central" at "https://repo1.maven.org/maven2/",
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases",
  "Typesafe repo" at "https://repo.typesafe.com/typesafe/releases/",
  "sbt Plugin Releases" at "https://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/",
)

PB.targets in Compile := Seq(
  scalapb.gen(grpc = true)          -> (sourceManaged in Compile).value / "scalapb",
  scalapb.zio_grpc.ZioCodeGenerator -> (sourceManaged in Compile).value / "scalapb",
)

//NOTE: - Pre run script

val runPreSbt = taskKey[Unit]("Runs a pre-sbt script")

runPreSbt := {
  val log = streams.value.log
  log.info("running pre-sbt sh")

  val results = "sh ./pre-sbt.sh".!
  if (results != 0) {
    sys.error("pre-sbt failed")
  }
}

// Set the Pre Sbt script execute before the compile
((Compile / compile)) := ((Compile / compile) dependsOn runPreSbt).value

//NOTE: - Post run script (Triggerd when after sbt run task is completed)

val runPostRunSbt = taskKey[Unit]("Runs a post-sbt-run script")

runPostRunSbt := {
  val log = streams.value.log
  log.info("running post-sbt-run sh")

  val results = "sh ./post-sbt-run.sh".!
  if (results != 0) {
    sys.error("post-sbt-run failed")
  }
}

// Set the Post Sbt script execute after the run task
Compile / run := Def
  .sequential(
    (Compile / run).toTask(""),
    runPostRunSbt,
  )
  .value
