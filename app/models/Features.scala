package models

import play.api._
import play.api.Play.current
import play.api.libs.json._
import scala.xml.XML

case class Feature(name: String, desc: String, version: String)

object Features{

   def all(): List[Feature] = {
   	 List(Feature("new","none","1.0"))
   }
def fromXml(node: scala.xml.Node):Seq[Feature] = {
    val features = (node \ "feature")
    val featureList = features.map(  feature =>
    { val name = (feature \ "@name").text
    val description = feature \ "@description"
    val version = feature \  "@version"
    new Feature(name,description.text,version.text) } )
    System.out.println(featureList)
    featureList
  }
}


object FeatureCrawler {


}