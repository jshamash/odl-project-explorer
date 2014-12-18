package com.inocybe.odlexplorer.model

case class Project(name: String) {
  def featuresUrl = s"/projects/$name/features"
}
