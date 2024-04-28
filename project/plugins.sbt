resolvers ++= Resolver.sonatypeOssRepos("snapshots")

addSbtPlugin("com.thesamet" % "sbt-protoc" % "1.0.6")

val zioGrpcVersion = "0.6.1"

libraryDependencies ++= Seq(
  "com.thesamet.scalapb.zio-grpc" %% "zio-grpc-codegen" % zioGrpcVersion,
  "com.thesamet.scalapb"          %% "compilerplugin"   % "0.11.10",
)
