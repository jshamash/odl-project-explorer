package controllers

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import models.Feature
import play.api.mvc.{Action, Controller}
import play.api.mvc.Results

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

object FeaturesController extends Controller {

  var crawler : ActorRef = null
  implicit val timeout = Timeout(5 minutes)

  def all = Action { implicit request =>
    Ok(views.html.featureslist(getFeatures, getProjects))
  }

  def details(name:String) = Action { implicit request =>
    findFeature(name) match {
      case Some(feature) => Ok(views.html.featuresdetails(feature))
      case None => Results.NotFound("Feature not found")
    }
  }
  
  def project(name: String) = Action { implicit request =>
    getProjectFeatures(name) match {
      case Some(features) => Ok(views.html.featureslist(features, getProjects))
      case None => Results.NotFound("Project not found")
    }
  }
  
  def test = Action { implicit request =>
    Ok(views.html.featureslist( getFeatures, getProjects))
  }

  def findFeature(feature: String): Option[Feature] = {
    val features = getFeatures
    features.find{f => f.name == feature}
  }
  
  def setCrawler(actor : ActorRef) {
    crawler = actor
  }

  def getFeatures = {
    Await.result((crawler ? "getFeatures").mapTo[List[Feature]], 5 minutes)
  }
  
  def getProjectFeatures(name : String)= {
    Await.result((crawler ? Project(name)).mapTo[Option[List[Feature]]], 5 minutes)
  }
  
  def getProjects = {
    Await.result((crawler ? "getProjects").mapTo[List[String]], 5 minutes)
  }
}