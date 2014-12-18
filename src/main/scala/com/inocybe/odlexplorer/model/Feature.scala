package com.inocybe.odlexplorer.model

import com.wordnik.swagger.annotations.{ApiModelProperty, ApiModel}

import scala.annotation.meta.field
import scala.xml.Node

@ApiModel(description = "A feature")
case class Feature(
                    @(ApiModelProperty @field)(value = "Feature name", required = true) name: String,
                    @(ApiModelProperty @field)(value = "Feature version", required = true) version: String,
                    @(ApiModelProperty @field)(value = "Feature description", required = false) description: Option[String],
                    @(ApiModelProperty @field)(value = "The bundles contained in this feature", required = true) bundles: List[String],
                    @(ApiModelProperty @field)(value = "The subfeatures of this feature", required = true, dataType = "array[string]") subFeatures: List[SubFeature],
                    @(ApiModelProperty @field)(value = "Any config files associated with this feature", required = true) configFiles: List[String] = List())

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
