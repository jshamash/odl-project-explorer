package com.inocybe.odlexplorer.api

import akka.actor.ActorRefFactory
import com.inocybe.odlexplorer.model.{Project, Feature}
import spray.http.StatusCodes
import spray.routing.{ExceptionHandler, HttpService}
import com.inocybe.odlexplorer.model.JsonModel._

class ProjectService(context: ActorRefFactory, projectToFeatures: Map[String, Set[Feature]])
  extends HttpService {

  def routes(implicit eh: ExceptionHandler) = getProjects ~ getProject ~ getFeatures

  def actorRefFactory = context

  def getProjects = path("projects") {
    get {
      complete { projectToFeatures.keys.map(Project).toList }
    }
  }

  def getProject = path("projects" / Segment) { project =>
    get {
      complete { Project(project) }
    }
  }

  def getFeatures = path("projects" / Segment / "features") { project =>
    get {
      projectToFeatures.get(project) match {
        case Some(f) => complete(f)
        case None    => complete(StatusCodes.NotFound, s"no such project: $project")
      }
    }
  }
}
