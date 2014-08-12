name := """project-explorer"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

libraryDependencies += "org.ops4j.pax.url" % "pax-url-aether" % "2.1.0"

libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)
