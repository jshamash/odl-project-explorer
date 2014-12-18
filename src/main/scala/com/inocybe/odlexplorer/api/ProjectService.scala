package com.inocybe.odlexplorer.api

import javax.ws.rs.Path

import akka.actor.ActorRefFactory
import com.inocybe.odlexplorer.model.JsonModel._
import com.inocybe.odlexplorer.model.{Feature, Project}
import com.wordnik.swagger.annotations._
import spray.routing.{ExceptionHandler, HttpService}

@Api(value="/projects", description = "Operations about projects")
class ProjectService(projectToFeatures: Map[String, Set[Feature]])(implicit context: ActorRefFactory)
  extends HttpService {

  def routes(implicit eh: ExceptionHandler) = getProjects ~ getProject ~ getFeatures

  def actorRefFactory = context

  @ApiOperation(httpMethod = "GET", response = classOf[Project], responseContainer = "Set", value = "Returns a list of all projects")
  def getProjects = path("projects") {
    get {
      complete { projectToFeatures.keys.map(Project).toList }
    }
  }

  @ApiOperation(httpMethod = "GET", response = classOf[Project], value = "Returns the project with the given name")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "name", value = "Project name", required = true, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse (code = 404, message = "Invalid project name")
  ))
  def getProject = path("projects" / Segment) { project =>
    get {
      complete { projectToFeatures.get(project).map(_ => Project(project))}
    }
  }

  @Path("/{name}/features")
  @ApiOperation(httpMethod = "GET", response = classOf[Feature], responseContainer = "Set", value = "Returns the features associated with a given project")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "name", value = "Project name", required = true, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse (code = 404, message = "Invalid project name")
  ))
  def getFeatures = path("projects" / Segment / "features") { project =>
    get {
      complete { projectToFeatures.get(project) }
    }
  }
}
