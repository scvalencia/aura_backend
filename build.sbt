name := """aura"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.1"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.mindrot" % "jbcrypt" % "0.3m",
  "postgresql" % "postgresql" % "9.1-901.jdbc4",
  "com.amazonaws" % "aws-java-sdk" % "1.3.11",
  "org.apache.httpcomponents" % "httpmime" % "4.3",
  "org.apache.httpcomponents" % "httpcore" % "4.4"
  //"com.google.code.gson" % "gson" % "2.3",
  //"com.googlecode.json-simple" % "json-simple" % "1.1.1",
  //"org.apache.httpcomponents" % "httpclient" % "4.3.6"
)
