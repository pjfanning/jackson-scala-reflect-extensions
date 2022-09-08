package com.github.pjfanning.jackson.reflect

import com.fasterxml.jackson.core.Version
import com.fasterxml.jackson.core.util.VersionUtil
import com.fasterxml.jackson.module.scala.JacksonModule

import java.util.Properties
import scala.collection.mutable
import scala.collection.JavaConverters._

private[reflect] object JacksonModule {
  private val cls = classOf[JacksonModule]
  private val buildPropsFilename = cls.getPackage.getName.replace('.','/') + "/build.properties"
  lazy val buildProps: mutable.Map[String, String] = {
    val props = new Properties
    val stream = cls.getClassLoader.getResourceAsStream(buildPropsFilename)
    if (stream ne null) props.load(stream)

    props.asScala
  }
  lazy val version: Version = {
    val groupId = buildProps("groupId")
    val artifactId = buildProps("artifactId")
    val version = buildProps("version")
    VersionUtil.parseVersion(version, groupId, artifactId)
  }
}
