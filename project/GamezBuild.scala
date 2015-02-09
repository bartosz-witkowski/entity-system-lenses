import sbt._
import sbt.Keys._

object GamezBuild extends Build {

  lazy val pedanticScalac = Seq(
    "-deprecation",           
    "-encoding", "UTF-8",       
    "-feature",                
    "-language:existentials",
    "-language:experimental.macros",
    "-language:higherKinds",
    "-language:implicitConversions",
    "-unchecked",
    "-Xfatal-warnings",       
    "-Xlint",
    "-Yno-adapted-args",       
    "-Ywarn-dead-code",        
    "-Ywarn-numeric-widen",   
    "-Ywarn-value-discard"     
  )

  val scalazVersion = "7.1.0"

  val monocleVersion = "1.0.1"   // or "1.1.0-SNAPSHOT"

  val deps = List(
    "org.scalaz" %% "scalaz-core" % scalazVersion,
    "com.github.julien-truffaut"  %%  "monocle-core"  % monocleVersion)

  lazy val gamez = Project(
    id = "gamez",
    base = file("."),
    settings = Project.defaultSettings ++ Seq(
      name := "gamez",
      organization := "gamez",
      version := "0.1-SNAPSHOT",
      scalaVersion := "2.11.4",
      scalacOptions ++= pedanticScalac,
      libraryDependencies ++= deps
      // add other settings here
    )
  )
}
