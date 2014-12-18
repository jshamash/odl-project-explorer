package com.inocybe.odlexplorer.model

import scala.xml.Node

case class SubFeature(name: String, version: String) {
  def url = s"/features/$name"
}

object SubFeature {
  def apply(xml: Node): SubFeature = {
    val name = xml.text
    val version = (xml \ "@version").text
    SubFeature(name, version)
  }
}

