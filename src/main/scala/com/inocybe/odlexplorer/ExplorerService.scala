package com.inocybe.odlexplorer

import akka.actor.{Actor, Props}
import com.inocybe.odlexplorer.Receptionist.Crawl
import com.inocybe.odlexplorer.api.{FeatureService, ProjectService}
import com.inocybe.odlexplorer.model.Feature
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

  def myRoute(projectToFeatures: Map[String, Set[Feature]]): Route = {
    val allFeatures = projectToFeatures.values.foldLeft(Set.empty[Feature])(_ ++ _)

    val projects = new ProjectService(actorRefFactory, projectToFeatures)
    val features = new FeatureService(actorRefFactory, allFeatures)

    projects.routes ~ features.routes
  }
}