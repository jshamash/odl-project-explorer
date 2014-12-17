package com.inocybe.odlexplorer.model

import scala.xml.Node

case class Feature(name: String, version: String, description: Option[String], bundles: List[String], subFeatures: List[SubFeature], configFiles: List[String] = List())

object Feature {
  def apply(xml: Node): Feature = {
    val name = (xml \ "@name").text
    val description = xml.attribute("description").map(_.text)
    val version = (xml \ "@version").text
    val bundles = (xml \ "bundle").map(_.text).toList
    val subFeatures = (xml \ "feature").map(SubFeature(_)).toList
    val configFiles = (xml \ "configfile").map(_.text).toList
    Feature(name, version, description, bundles, subFeatures, configFiles)
  }
}
