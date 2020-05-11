name := "scala-ratelimiter"

version := "0.1"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http"   % "10.1.11",
  "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.11",
  "com.typesafe.akka" %% "akka-stream" % "2.6.4",
  "joda-time" % "joda-time" % "2.9.3"
)

scalaVersion := "2.13.1"
