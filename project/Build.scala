import sbt._
import Keys._

object IslandBuild extends Build {
  var root = Project("island-main", file("."),
    settings = Defaults.defaultSettings ++ Seq(
      name := "island-main",
      version := "1.0-SNAPSHOT",
      organization := "org.unlimited",
      scalaVersion := "2.9.1",
      fork := true,

      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2" % "1.6.1",
        "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
        "org.scala-tools.testing" %% "scalacheck" % "1.9" % "test"
      ),

      resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
        "releases" at "http://scala-tools.org/repo-releases"),

      unmanagedJars in Compile <<= baseDirectory map { base => (base ** "*.jar").classpath }
    ) ++ JMonkeyProject.jmonkeySettings
  )
}