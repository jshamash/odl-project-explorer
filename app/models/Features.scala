package models

import controllers.CrawlerActor
import play.api.Application
import play.api.GlobalSettings
import play.api.Logger
import scala.concurrent.duration.DurationInt
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.concurrent.Akka
import akka.actor.Props

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
      featureList
    }
}