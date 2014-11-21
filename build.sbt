name := """project-explorer"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.1"

includeFilter in (Assets, LessKeys.less) := "*.less"

excludeFilter in (Assets, LessKeys.less) := "_*.less"

//libraryDependencies += "org.ops4j.pax.url" % "pax-url-aether" % "2.1.0"
libraryDependencies += "org.eclipse.aether" % "aether-api" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-util" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-spi" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-impl" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-connector-basic" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-transport-http" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-test-util" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-transport-file" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-transport-classpath" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether-transport-wagon" % "1.0.0.v20140518"

libraryDependencies += "org.eclipse.aether" % "aether" % "1.0.0.v20140518"

libraryDependencies += "org.apache.maven" % "maven-aether-provider" % "3.2.3"

libraryDependencies += "org.zeroturnaround" % "zt-zip" % "1.8"

libraryDependencies += "commons-io" % "commons-io" % "2.4"


libraryDependencies ++= Seq(
  jdbc,
  anorm,
  cache,
  ws
)
