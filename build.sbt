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
  "com.google.code.gson" % "gson" % "2.2"
)
