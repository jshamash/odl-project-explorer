package com.inocybe.odlexplorer

import akka.actor.{Actor, Props}
import com.inocybe.odlexplorer.Receptionist.Crawl
import com.inocybe.odlexplorer.model.JsonModel._
import com.inocybe.odlexplorer.model.{Feature, Project}
import spray.http.StatusCodes
import spray.routing._

import scala.concurrent.duration._

class ExplorerServiceActor extends Actor with ExplorerService {
  import context.dispatcher

  def receptionistProps: Props = Props[Receptionist]

  val receptionist = context.actorOf(receptionistProps, "receptionist")
  val cancellable = context.system.scheduler.schedule(0.minutes, 10.minutes, receptionist, Crawl)

  def actorRefFactory = context

  override def postStop(): Unit = cancellable.cancel()

  def receive = update(Map.empty[String, Set[Feature]])

  def update(projectToFeatures: Map[String, Set[Feature]]): Receive = {
    val listen: Receive = {
      case Receptionist.Updated(p2f) => context.become(update(p2f))
    }
    listen orElse runRoute(myRoute(projectToFeatures))
  }
}


trait ExplorerService extends HttpService {

  implicit val exceptionHandler = ExceptionHandler {
    case e: NoSuchElementException => complete(StatusCodes.NotFound, e.getMessage)
  }

  def myRoute(projectToFeatures: Map[String, Set[Feature]]) = {
    val allFeatures = projectToFeatures.values.foldLeft(Set.empty[Feature])(_ ++ _)

    path("projects") {
      get {
        complete { projectToFeatures.keys.map(Project).toList }
      }
    } ~
    path("projects" / Segment) { project =>
      get {
        complete { Project(project) }
      }
    } ~
    path("projects" / Segment / "features") { project =>
      get {
        complete { projectToFeatures(project) }
      }
    } ~
    path("features") {
      get {
        complete { allFeatures }
      }
    } ~
    path("features" / Segment) { feature =>
      get {
        complete { allFeatures.find(_.name == feature).getOrElse(throw new NoSuchElementException(s"key not found: $feature")) }
      }
    }
  }
}