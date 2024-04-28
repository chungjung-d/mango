import sbt._

object Dependencies {

  val zioVersion            = "2.1-RC1"
  val zioHttpVersion        = "3.0.0-RC6"
  val zioInteropJavaVersion = "1.1.0.0-RC6"
  val zioJsonVersion        = "0.6.2"

  val dockerJavaVersion = "3.2.8"

  val logbackVersion = "1.5.3"
  val slfjVersion    = "2.0.13"

  val circeVersion = "0.14.6"
  val munitVersion = "0.7.29" // MUnit 버전 추가

  val grpcVersion = "1.63.0"

  val zio = Seq(
    "dev.zio" %% "zio"        % zioVersion,
    "dev.zio" %% "zio-macros" % zioVersion,
    // "dev.zio" %% "zio-http"         % zioHttpVersion,
    "dev.zio" %% "zio-interop-java" % zioInteropJavaVersion,
    "dev.zio" %% "zio-json"         % zioJsonVersion,
  )

  val docker = Seq(
    "com.github.docker-java" % "docker-java-core"                  % dockerJavaVersion,
    "com.github.docker-java" % "docker-java-transport-httpclient5" % dockerJavaVersion,
  )

  

  val log = Seq(
    "org.slf4j"      % "slf4j-api"       % slfjVersion,
    "ch.qos.logback" % "logback-classic" % logbackVersion,
  )

  val circe = Seq(
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-literal" % circeVersion,
  )

  val grpc = Seq(
    "io.grpc"               % "grpc-netty"    % grpcVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % scalapb.compiler.Version.scalapbVersion,
    "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  )

  val munit = "org.scalameta" %% "munit" % munitVersion % Test // MUnit 의존성 정의

  val dependecies: Seq[ModuleID] = zio ++ circe ++ docker ++ grpc ++ log :+ munit
}
