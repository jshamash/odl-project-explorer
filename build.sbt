organization  := "com.inocybe"

version       := "0.1"

scalaVersion  := "2.11.2"

scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8")

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  val aetherV = "1.0.1.v20141111"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.1",
    "io.spray"            %%  "spray-testkit" % sprayV  % "test",
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "com.typesafe.akka"   %%  "akka-testkit"  % akkaV   % "test",
    "org.specs2"          %%  "specs2-core"   % "2.3.11" % "test",
    "org.scalatest"       %   "scalatest_2.11" % "2.2.1" % "test",
    "org.slf4j"           %   "slf4j-nop"     % "1.7.7",
    "org.eclipse.aether"  %   "aether"        % aetherV,
    "org.eclipse.aether"  %   "aether-api"    % aetherV,
    "org.eclipse.aether"  %   "aether-util"   % aetherV,
    "org.eclipse.aether"  %   "aether-spi"    % aetherV,
    "org.eclipse.aether"  %   "aether-impl"   % aetherV,
    "org.eclipse.aether"  %   "aether-connector-basic" % aetherV,
    "org.eclipse.aether"  %   "aether-transport-file"  % aetherV,
    "org.eclipse.aether"  %   "aether-transport-http"  % aetherV,
    "org.apache.maven"    %   "maven-aether-provider"  % "3.2.3",
    "org.scala-lang.modules" %% "scala-async" % "0.9.2",
    "com.gettyimages"     %% "spray-swagger"  % "0.5.0"
  )
}

Revolver.settings
