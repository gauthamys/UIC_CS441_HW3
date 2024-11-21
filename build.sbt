name := "hw3"
organization := "com.example"

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.13.15"

libraryDependencies += guice
libraryDependencies += "org.scalatestplus.play" %% "scalatestplus-play" % "7.0.1" % Test

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.4.3",
  "com.knuddels" % "jtokkit" % "1.1.0"
  ,
  "org.deeplearning4j" % "deeplearning4j-core" % "1.0.0-M2.1",
  "org.deeplearning4j" % "deeplearning4j-zoo" % "1.0.0-M2.1",
  "org.deeplearning4j" % "deeplearning4j-modelimport" % "1.0.0-M2.1",
  "io.github.ollama4j" % "ollama4j" % "1.0.79",
  "com.softwaremill.sttp.client3" %% "core" % "3.9.7",
  "io.circe" %% "circe-core" % "0.15.0-M1",
  "io.circe" %% "circe-generic" % "0.15.0-M1",
  "io.circe" %% "circe-parser" % "0.15.0-M1",

  "org.nd4j" % "nd4j-native" % "1.0.0-M2.1",
  "org.nd4j" % "nd4j-native-platform" % "1.0.0-M2.1",
)
dependencyOverrides += "com.fasterxml.jackson.core" % "jackson-databind" % "2.14.2"
