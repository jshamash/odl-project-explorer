package com.inocybe.odlexplorer.model

import scala.xml.Node

case class SubFeature(name: String, version: String)

object SubFeature {
  def apply(xml: Node): SubFeature = {
    val name = xml.text
    val version = (xml \ "@version").text
    SubFeature(name, version)
  }
}

