version := "1.0"
scalaVersion := "2.13.12"

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.5.3"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17" % Test

enablePlugins(AssemblyPlugin)

assembly / mainClass := Some("optolookup.OptoLookup")
assembly /assemblyJarName := "optolookup.jar"