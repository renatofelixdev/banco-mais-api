
name := """banco-mais-api"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava, PlayEbean)

scalaVersion := "2.11.11"

libraryDependencies ++= Seq(
  "org.postgresql" % "postgresql" % "9.4.1212.jre7",
  javaJdbc,
  cache,
  javaWs
)
