package com.inocybe.odlexplorer.model

import com.wordnik.swagger.annotations.{ApiModel, ApiModelProperty}

import scala.annotation.meta.field

@ApiModel(description = "A project")
case class Project(
      @(ApiModelProperty @field)(position = 1, value = "project name", required = true) name: String) {

  @(ApiModelProperty @field)(position = 2, value = "features URL", required = true)
  def featuresUrl = s"/projects/$name/features"
}
