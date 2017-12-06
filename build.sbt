name := "road-to-akka"

version := "1.0"

scalaVersion := "2.12.2"

val akkaVersion = "2.5.7"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-stream-testkit" % akkaVersion % Test
)