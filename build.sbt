import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerBaseImage

ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.13.8"

val akkaVersion = "2.6.19"
val akkaHttpVersion = "10.2.9"

lazy val root = (project in file("."))
  .enablePlugins(JavaServerAppPackaging, DockerPlugin)
  .settings(
    name := "SimpleAkkaExample",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
      "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
      "org.slf4j" % "slf4j-api" % "2.0.0",
      "org.slf4j" % "slf4j-simple" % "2.0.0",
      "com.lihaoyi" %% "upickle" % "2.0.0",
      "org.scalactic" %% "scalactic" % "3.2.13",
      "org.scalatest" %% "scalatest" % "3.2.13" % "test"
    ),
    dockerBaseImage := "openjdk",
    dockerExposedPorts := Seq(8081),
    Docker / packageName := "akkaweatherapi"
  )
