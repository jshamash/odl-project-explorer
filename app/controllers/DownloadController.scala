package controllers

import java.io.{FileInputStream, FileOutputStream}
import java.util.{Properties, UUID}

import akka.actor.Props
import models.MyJsonProtocol._
import models.{Delete, ODLProject}
import org.apache.commons.io._
import org.zeroturnaround.zip.ZipUtil
import play.api.Play
import play.api.Play.current
import play.api.libs.concurrent.Akka
import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import workers.TempDestroyer

import scala.concurrent.duration._
import scala.language.postfixOps
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

  def selectFeatures = Action { request =>
    val form = request.body.asFormUrlEncoded.getOrElse(Map.empty).map{ case (k,v) => k -> v.mkString }
    
    val json = Json.parse(form.getOrElse("data", ""))
    val features = (json \ "features").asOpt[List[String]].getOrElse(List())

    // Create a unique temporary folder
    val copyID = UUID.randomUUID()
    Play.getFile(s"resources/temp/$copyID").mkdir()

    // Copy config to temp folder
    println(s"Copying config to resources/temp/$copyID/config")
    val config = Play.getFile("resources/org.apache.karaf.features.cfg")
    val configCopy = Play.getFile(s"resources/temp/$copyID/config")
    FileUtils.copyFile(config, configCopy)
    println("done.")

    // Copy distro to temp folder
    println(s"Copying distro to resources/temp/$copyID/distro")
    val distro = Play.getFile("resources/distribution-karaf-0.2.1-Helium-SR1")
    val distroCopy = Play.getFile(s"resources/temp/$copyID/distro")
    FileUtils.copyDirectory(distro, distroCopy)
    println("done.")

    // Load properties of copied config file
    val is = new FileInputStream(configCopy)
    val props = new Properties()
    props.load(is)
    is.close()

    // Edit the featuresBoot property
    val allFeatures = props.getProperty("featuresBoot") + (if (features.isEmpty) "" else "," + features.mkString(","))
    props.setProperty("featuresBoot", allFeatures)

    // Copy new config into new distro
    props.store(new FileOutputStream(Play.getFile(s"resources/temp/$copyID/distro/etc/org.apache.karaf.features.cfg")), "Customized features config")

    // Zip the new distro
    println("Starting zip...")
    val zipFile = Play.getFile(s"resources/temp/$copyID/distribution-karaf-0.2.1-Helium-SR1.zip")
    ZipUtil.pack(distroCopy, zipFile)
    println("done")

    // Delete temporary files in the future
    implicit val context = Akka.system.dispatcher
    val destroyer = Akka.system.actorOf(Props[TempDestroyer])
    Akka.system.scheduler.scheduleOnce(1 hour){destroyer ! Delete(s"resources/temp/$copyID")}

    Ok.sendFile(zipFile)
  }

}