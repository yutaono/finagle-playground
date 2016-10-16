name := "finagle-playground"

version := "1.0"

scalaVersion := "2.11.8"

lazy val finagleVersion = "6.35.0"
lazy val circeVersion = "0.5.0-M3"

resolvers += "Twitter's Repository" at "https://maven.twttr.com/"

libraryDependencies ++= Seq(
  "com.twitter" %% "finagle-http" % finagleVersion,
  "com.twitter" %% "finagle-zipkin" % finagleVersion,
  "com.twitter" %% "finagle-stats" % finagleVersion,
  "com.twitter" %% "twitter-server" % "1.19.0",
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
//  "io.zipkin.finagle" % "zipkin-finagle-http_2.11" % "0.2.1",
  "joda-time" % "joda-time" % "2.9.4"
)
