name := "coordinateGeometryInScala"

version := "1.0"

scalaVersion := "2.11.5"

val scalazV = "7.1.1"

val scalaTestV = "2.1.7"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % scalazV,
  "org.scalatest" %% "scalatest" % scalaTestV % "test"
)

resolvers ++= Seq(
  "oss snapshots" at "http://oss.sonatype.org/content/repositories/snapshots",
  "oss releases" at "http://oss.sonatype.org/content/repositories/releases",
  "Scalaz Bintray Repo" at "http://dl.bintray.com/scalaz/releases"
)

