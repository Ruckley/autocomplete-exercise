version := "1.0"
scalaVersion := "2.13.12"


libraryDependencies ++= Seq(
  "org.typelevel" %% "cats-effect" % "3.5.3",
  "org.http4s" %% "http4s-blaze-server" % "1.0.0-M28",
  "org.http4s" %% "http4s-dsl" % "1.0.0-M28",
  "org.http4s" %% "http4s-circe" % "1.0.0-M28",
  "com.typesafe" % "config" % "1.4.1",
  "io.opentelemetry" % "opentelemetry-sdk" % "1.36.0",
  "io.opentelemetry" % "opentelemetry-api" % "1.36.0",
  "org.slf4j" % "slf4j-api" % "2.0.12",
  "org.scalatest" %% "scalatest" % "3.2.17" % Test
)


enablePlugins(AssemblyPlugin)

assembly / mainClass := Some("optolookup.OptoLookup")
assembly /assemblyJarName := "optolookup.jar"