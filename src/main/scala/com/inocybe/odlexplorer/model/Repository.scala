package com.inocybe.odlexplorer.model

// Version = None -- get latest version
case class Repository(group: String, artifact: String, version: Option[String]) {
  def project = group.split("\\.")(2)
}

object Repository {
  def apply(url: String): Repository = {
    // TODO replace with regex
    val splitUrl = url.split("/")
    Repository(splitUrl(0).split(":")(1), splitUrl(1), Some(splitUrl(2)))
  }
}