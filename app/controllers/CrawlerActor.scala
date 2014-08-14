package controllers

import java.net.URL

import akka.actor.Actor
import akka.actor.Props
import play.api.Logger

class CrawlerActor extends Actor {
  var feature: Seq[models.Feature] = null

  def receive = {
    case "crawl" =>
      crawl
      Logger.info("Crawl initiated.")
    case "getFeatures" =>
      Logger.info("Received a getFeature message")
    case _      =>
      println("AKKA: unknown message received")
      Logger.info("The crawler received an unknown message")
  }

  def crawl = {
    val xml = scala.xml.XML.load(new URL("http://nexus.opendaylight.org/content/repositories/opendaylight.snapshot/org/opendaylight/controller/base-features/1.4.2-SNAPSHOT/base-features-1.4.2-20140807.221612-474-features.xml"))
    feature = models.Features.fromXml(xml)
  }
}
