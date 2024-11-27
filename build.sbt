name := "hw3"
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

import sbtprotoc.ProtocPlugin.autoImport.*

scalaVersion := "2.13.15"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.3",
  "com.knuddels" % "jtokkit" % "1.1.0",

  "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-M2.1",
  "org.deeplearning4j" % "deeplearning4j-zoo" % "1.0.0-M2.1",
  "org.deeplearning4j" % "deeplearning4j-modelimport" % "1.0.0-M2.1",
  "io.github.ollama4j" % "ollama4j" % "1.0.79",

  "com.softwaremill.sttp.client3" %% "core" % "3.9.7",
  "io.circe" %% "circe-core" % "0.15.0-M1",
  "io.circe" %% "circe-generic" % "0.15.0-M1",
  "io.circe" %% "circe-parser" % "0.15.0-M1",

  "io.grpc" % "grpc-netty" % "1.65.1",
  "io.grpc" % "grpc-stub" % "1.64.0",
  "com.thesamet.scalapb" %% "scalapb-runtime-grpc" % "0.11.17",
  "com.typesafe.akka" %% "akka-stream" % "2.6.20",

  "org.nd4j" % "nd4j-native" % "1.0.0-M2.1",
  "org.nd4j" % "nd4j-native-platform" % "1.0.0-M2.1",
)
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.2"

Compile / PB.targets := Seq(
  scalapb.gen() -> (Compile / sourceManaged).value / "scalapb"
)

Compile / PB.protoSources += file("app/protobuf")