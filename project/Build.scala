import sbt._
import Keys._

object IslandBuild extends Build {
  var root = Project("island-main", file("."),
    settings = Defaults.defaultSettings ++ Seq(
      name := "island-main",
      version := "1.0-SNAPSHOT",
      organization := "org.unlimited",
      scalaVersion := "2.10.0-RC2",
      fork := true,

      libraryDependencies ++= Seq(
        "org.specs2" %% "specs2" % "1.12.3",
        "org.specs2" %% "specs2-scalaz-core" % "6.0.1" % "test",
        "org.scalacheck" %% "scalacheck" % "1.10.0" % "test"
      ),

      resolvers ++= Seq("snapshots" at "http://scala-tools.org/repo-snapshots",
        "releases" at "http://scala-tools.org/repo-releases",
        "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
        "Sonatype Releases" at "http://oss.sonatype.org/content/repositories/releases"),

      unmanagedJars in Compile <<= baseDirectory map { base => (base ** "*.jar").classpath }
    ) ++ JMonkeyProject.jmonkeySettings
  )
}
