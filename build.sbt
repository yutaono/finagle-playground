name := "finagle-playground"

version := "1.0"

scalaVersion := "2.12.7"

lazy val finagleVersion = "18.11.0"
lazy val circeVersion = "0.10.1"
lazy val twitterServerVersion = "18.11.0"

resolvers += "Twitter's Repository" at "https://maven.twttr.com/"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % finagleVersion,
  "com.twitter" %% "twitter-server" % twitterServerVersion,
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
//  "io.zipkin.finagle" % "zipkin-finagle-http" % "2.0.7",
  "joda-time" % "joda-time" % "2.9.4",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)

