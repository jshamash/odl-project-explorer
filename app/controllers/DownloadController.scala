package controllers

import java.io.{File, FileOutputStream, OutputStream, FileInputStream}
import java.util.Properties

import models.MyJsonProtocol._
import models.ODLProject
import play.Logger
import play.api.Play
import play.api.Play.current
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import org.zeroturnaround.zip.ZipUtil

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
    val is = Play.classloader.getResourceAsStream(filename)
    val str = scala.io.Source.fromInputStream(is).mkString
    is.close()
    val json = Json.parse(str)
    Try((json \ "projects").as[List[ODLProject]])
  }

  def selectFeatures = Action(parse.json) { request =>
    val json = request.body
    val features = (json \ "features").asOpt[List[String]].getOrElse(List())

    val config = Play.getExistingFile("resources/org.apache.karaf.features.cfg")
    config match {
      case Some(file) =>
        val is = new FileInputStream(file)
        val props = new Properties()
        props.load(is)

        val allFeatures = props.getProperty("featuresBoot") + (if (features.isEmpty) "" else "," + features.mkString(","))
        props.setProperty("featuresBoot", allFeatures)
        is.close()

        props.store(new FileOutputStream(Play.getFile("resources/distribution-karaf-0.2.1-Helium-SR1/etc/org.apache.karaf.features.cfg")), "Customized features config")
        println("Starting zip...")
        ZipUtil.pack(Play.getFile("resources/distribution-karaf-0.2.1-Helium-SR1"), Play.getFile("resources/distribution-karaf-0.2.1-Helium-SR1.zip"))
        println("done")
        Ok.sendFile(Play.getFile("resources/distribution-karaf-0.2.1-Helium-SR1.zip"))
      case None =>
        Logger.warn("Couldn't open config file")
        InternalServerError("Couldn't open config file")
    }
  }

}