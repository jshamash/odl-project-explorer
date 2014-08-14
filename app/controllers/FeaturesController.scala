package controllers

import play.api._
import play.api.mvc._
import org.ops4j.pax.url.mvn.internal.config.MavenRepositoryURL
import scala.xml.XML
import models._
import java.net.URL
import scala.concurrent.{ExecutionException, Future}

object FeaturesController extends Controller {

  val crawlerActor: CrawlerActor = null

  def list = Action { implicit request =>
    Ok(views.html.featureslist(mapFeatures))
  }

  def details(name:String) = Action { implicit request =>
    Ok(views.html.featuresdetails(findFeature(name)))
  }

  def findFeature(feature: String): Feature = {
    val f_array = mapFeatures()
    val elem = f_array.find{f => f.name == feature}
    if (elem.isDefined){
      elem.get
    }
    else {
      null
    }
  }

  // Should use the crawler here
  @scala.deprecated
  def mapFeatures(): Seq[Feature] = {
    val xml = scala.xml.XML.load(new URL("http://nexus.opendaylight.org/content/repositories/opendaylight.snapshot/org/opendaylight/controller/base-features/1.4.2-SNAPSHOT/base-features-1.4.2-20140807.221612-474-features.xml"))
    val feature = models.Features.fromXml(xml)
    feature
  }

  // TODO
  def getFeatures(): Seq[Feature] = {
    //val features = crawlerActor ! "getFeatures"
    null
  }
}