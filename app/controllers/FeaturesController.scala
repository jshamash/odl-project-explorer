package controllers

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import models.Feature
import play.api.mvc.Action
import play.api.mvc.Controller

object FeaturesController extends Controller {

  var crawler : ActorRef = null
  implicit val timeout = Timeout(5 minutes)

  def list = Action { implicit request =>
    Ok(views.html.featureslist(getFeatures))
  }

  def details(name:String) = Action { implicit request =>
    Ok(views.html.featuresdetails(findFeature(name)))
  }
  
  def test = Action { implicit request =>
    Ok(views.html.test( getFeatures.mkString(", ") ))
  }

  def findFeature(feature: String): Feature = {
    val f_array = getFeatures 
    val elem = f_array.find{f => f.name == feature}
    if (elem.isDefined){
      elem.get
    }
    else {
      null
    }
  }
  
  def setCrawler(actor : ActorRef) {
    crawler = actor
  }

  def getFeatures = {
    val features = Await.result((crawler ? "getFeatures"), 5 minutes)
    features.asInstanceOf[Seq[Feature]]
  }
}