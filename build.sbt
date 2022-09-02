import org.typelevel.sbt.gha.JavaSpec.Distribution.Zulu

name := "jackson-scala-reflect-extensions"
organization := "com.github.pjfanning"
description := "Extension to jackson-module-scala that uses scala-reflect to get type info"

ThisBuild / scalaVersion := "2.13.8"

ThisBuild / crossScalaVersions := Seq("2.11.12", "2.12.15", "2.13.8")

val jacksonVersion = "2.13.3"
val scalaReflectionVersion = "1.1.4"

resolvers ++= Resolver.sonatypeOssRepos("snapshots")

libraryDependencies ++= Seq(
  "org.scala-lang" % "scala-reflect" % scalaVersion.value,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.13.3+27-8e264e75-SNAPSHOT",
  "org.slf4j" % "slf4j-api" % "1.7.36",
  "org.scalatest" %% "scalatest" % "3.2.13" % Test,
  "org.slf4j" % "slf4j-simple" % "1.7.36" % Test,
  "com.fasterxml.jackson.dataformat" % "jackson-dataformat-csv" % jacksonVersion % Test
)

homepage := Some(url("https://github.com/pjfanning/jackson-scala-reflect-extensions"))

licenses := Seq("APL2" -> url("https://www.apache.org/licenses/LICENSE-2.0.txt"))

developers := List(
  Developer(id="pjfanning", name="PJ Fanning", email="", url=url("https://github.com/pjfanning"))
)

Test / parallelExecution := false

ThisBuild / githubWorkflowJavaVersions := Seq(JavaSpec(Zulu, "8"))
ThisBuild / githubWorkflowBuild := Seq(WorkflowStep.Sbt(List("test")))
ThisBuild / githubWorkflowPublishTargetBranches := Seq(
  RefPredicate.Equals(Ref.Branch("main")),
  RefPredicate.StartsWith(Ref.Tag("v"))
)

ThisBuild / githubWorkflowPublish := Seq(
  WorkflowStep.Sbt(
    List("ci-release"),
    env = Map(
      "PGP_PASSPHRASE" -> "${{ secrets.PGP_PASSPHRASE }}",
      "PGP_SECRET" -> "${{ secrets.PGP_SECRET }}",
      "SONATYPE_PASSWORD" -> "${{ secrets.SONATYPE_PASSWORD }}",
      "SONATYPE_USERNAME" -> "${{ secrets.SONATYPE_USERNAME }}",
      "CI_SNAPSHOT_RELEASE" -> "+publishSigned"
    )
  )
)
