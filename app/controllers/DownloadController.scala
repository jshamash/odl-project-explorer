package controllers

import models.MyJsonProtocol._
import models.ODLProject
import play.api.Play
import play.api.Play.current
import play.api.libs.json._
import play.api.mvc.{Action, Controller}

import scala.util.Try

object DownloadController extends Controller {

  def download = Action {
    val projects = for {
      filename <- List("BaseFeatures.json", "Addons.json", "Applications.json")
      projects <- loadProjects(filename).toOption
    } yield projects

    projects match {
      case List(baseFeatures, addons, applications) => Ok(views.html.downloadwidget(baseFeatures, addons, applications))
      case _ => InternalServerError("One or more JSON files cannot be parsed")
    }
  }

  def loadProjects(filename: String) : Try[List[ODLProject]] = {
    val is = Play.classloader.getResourceAsStream(filename);
    val str = scala.io.Source.fromInputStream(is).mkString
    val json = Json.parse(str)
    Try((json \ "projects").as[List[ODLProject]])
  }

}