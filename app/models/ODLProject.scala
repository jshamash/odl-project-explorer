package models

import play.api.libs.json._
import play.api.libs.functional.syntax._

case class ODLProject(name: String, img: String, description: String, coreFeatures: List[ODLFeature], optionalFeatures: List[ODLFeature])
case class ODLFeature(name: String, description: String)

object MyJsonProtocol {
  implicit val featureReads: Reads[ODLFeature] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "description").read[String]
    )(ODLFeature.apply _)

  implicit val projectReads: Reads[ODLProject] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "img").read[String] and
      (JsPath \ "description").read[String] and
      (JsPath \ "coreFeatures").read[List[ODLFeature]] and
      (JsPath \ "optionalFeatures").read[List[ODLFeature]]
    )(ODLProject.apply _)
}