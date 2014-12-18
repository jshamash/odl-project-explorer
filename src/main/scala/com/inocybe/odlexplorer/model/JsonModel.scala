package com.inocybe.odlexplorer.model

import spray.httpx.SprayJsonSupport
import spray.json._

object JsonModel extends DefaultJsonProtocol with SprayJsonSupport {
  implicit object subFeatureJson extends RootJsonFormat[SubFeature] {
    override def read(json: JsValue): SubFeature = deserializationError("SubFeature cannot be read")
    override def write(obj: SubFeature): JsValue = JsString(obj.url)
  }

  implicit val featureJson = jsonFormat6(Feature.apply)
  implicit val repoJson = jsonFormat3(Repository.apply)

  implicit object projectJson extends RootJsonFormat[Project] {
    override def read(json: JsValue): Project = json match {
      case JsObject(fields) => fields.get("name") match {
        case Some(JsString(name)) => Project(name)
        case _ => deserializationError("Missing field 'name'")
      }
      case _ => deserializationError("Project expected")
    }

    override def write(obj: Project): JsValue = JsObject(
      "name" -> JsString(obj.name),
      "featuresUrl" -> JsString(obj.featuresUrl)
    )
  }
}