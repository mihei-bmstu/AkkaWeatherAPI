ThisBuild / version := "0.1.0"

ThisBuild / scalaVersion := "2.13.8"

val akkaVersion = "2.6.19"
val akkaHttpVersion = "10.2.9"

lazy val root = (project in file("."))
  .settings(
    name := "SimpleAkkaExample",
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
      "com.typesafe.akka" %% "akka-stream" % akkaVersion,
      "com.typesafe.akka" %% "akka-http" % akkaHttpVersion
    )
  )
