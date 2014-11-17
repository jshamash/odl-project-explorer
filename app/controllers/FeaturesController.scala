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

  def all = Action { implicit request =>
    Ok(views.html.featureslist(getFeatures, getProjects))
  }

  def details(name:String) = Action { implicit request =>
    Ok(views.html.featuresdetails(findFeature(name)))
  }
  
  def project(name: String) = Action { implicit request =>
    Ok(views.html.featureslist(getProjectFeatures(name), getProjects))
  }
  
  def test = Action { implicit request =>
    Ok(views.html.featureslist( getFeatures, getProjects))
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
    Await.result((crawler ? "test").mapTo[List[Feature]], 5 minutes)
  }
  
  def getProjectFeatures(name : String)= {
    val features = Await.result((crawler ? Project(name)), 5 minutes)
    features.asInstanceOf[Seq[Feature]]
  }
  
  def getProjects = {
    val projects = Await.result((crawler ? "getProjects"), 5 minutes)
    projects.asInstanceOf[List[String]]
  }
}