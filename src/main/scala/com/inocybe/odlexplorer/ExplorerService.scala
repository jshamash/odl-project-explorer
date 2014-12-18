package com.inocybe.odlexplorer


import akka.actor.{Actor, Props}
import com.gettyimages.spray.swagger.SwaggerHttpService
import com.inocybe.odlexplorer.Receptionist.Crawl
import com.inocybe.odlexplorer.api.{FeatureService, ProjectService}
import com.inocybe.odlexplorer.model.Feature
import com.wordnik.swagger.model.ApiInfo
import spray.routing._

import scala.concurrent.duration._
import scala.reflect.runtime.universe._

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

  implicit val arf = actorRefFactory
  val swaggerService = new SwaggerHttpService {
    override def apiTypes = Seq(typeOf[ProjectService], typeOf[FeatureService])
    override def apiVersion = "2.0"
    override def baseUrl = "/" // let swagger-ui determine the host and port
    override def docsPath = "api-docs"
    override def actorRefFactory = arf
    override def apiInfo = Some(new ApiInfo("ODL Project Explorer", "An API for exploring features in OpenDaylight.", "", "jshamash@inocybe.com", "Apache V2", "http://www.apache.org/licenses/LICENSE-2.0"))
  }

  def swaggerUI: Route = get {
    path("") {
      getFromResource("swagger-ui/index.html")
    } ~
    getFromResourceDirectory("swagger-ui")
  }

  def myRoute(projectToFeatures: Map[String, Set[Feature]]): Route = {
    val allFeatures = projectToFeatures.values.foldLeft(Set.empty[Feature])(_ ++ _)

    val projects = new ProjectService(projectToFeatures)
    val features = new FeatureService(allFeatures)

    projects.routes ~ features.routes ~ swaggerService.routes ~ swaggerUI
  }
}