package com.inocybe.odlexplorer.api

import akka.actor.ActorRefFactory
import com.inocybe.odlexplorer.model.Feature
import com.inocybe.odlexplorer.model.JsonModel._
import com.wordnik.swagger.annotations._
import spray.routing.HttpService

@Api(value="/features", description = "Operations about features")
class FeatureService(allFeatures: Set[Feature])(implicit context: ActorRefFactory) extends HttpService {

  def routes = getFeatures ~ getFeature

  def actorRefFactory = context

  @ApiOperation(httpMethod = "GET", response = classOf[Feature], responseContainer = "Set", value = "Returns a list of all features")
  def getFeatures = path("features") {
    get {
      complete { allFeatures }
    }
  }

  @ApiOperation(httpMethod = "GET", response = classOf[Feature], value = "Returns the feature with the given name")
  @ApiImplicitParams(Array(
    new ApiImplicitParam(name = "name", value = "Feature name", required = true, dataType = "string", paramType = "path")
  ))
  @ApiResponses(Array(
    new ApiResponse (code = 404, message = "Invalid feature name")
  ))
  def getFeature = path("features" / Segment) { feature =>
    get {
      complete { allFeatures.find(_.name == feature) }
    }
  }
}
