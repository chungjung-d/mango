import sbt._

object Dependencies {

  val zioVersion            = "2.1-RC1"
  val zioHttpVersion        = "3.0.0-RC6"
  val zioInteropJavaVersion = "1.1.0.0-RC6"
  val zioJsonVersion        = "0.6.2"

  val dockerJavaVersion     = "3.2.8"
  val dockerJavaSlfjVersion = "2.0.13"

  val circeVersion = "0.14.6"
  val munitVersion = "0.7.29" // MUnit 버전 추가

  val zio = Seq(
    "dev.zio" %% "zio"              % zioVersion,
    "dev.zio" %% "zio-macros"       % zioVersion,
    "dev.zio" %% "zio-http"         % zioHttpVersion,
    "dev.zio" %% "zio-interop-java" % zioInteropJavaVersion,
    "dev.zio" %% "zio-json"         % zioJsonVersion
  )

  val docker = Seq(
    "com.github.docker-java" % "docker-java-core"                  % dockerJavaVersion,
    "com.github.docker-java" % "docker-java-transport-httpclient5" % dockerJavaVersion,
    "org.slf4j"              % "slf4j-api"                         % dockerJavaSlfjVersion
  )

  val circe = Seq(
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-literal" % circeVersion
  )

  val munit = "org.scalameta" %% "munit" % munitVersion % Test // MUnit 의존성 정의

  val dependecies: Seq[ModuleID] = zio ++ circe ++ docker :+ munit
}
