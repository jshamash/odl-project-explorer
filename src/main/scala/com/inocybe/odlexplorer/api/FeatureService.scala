package com.inocybe.odlexplorer.api

import akka.actor.ActorRefFactory
import com.inocybe.odlexplorer.model.Feature
import spray.http.StatusCodes
import spray.routing.{ExceptionHandler, HttpService}
import com.inocybe.odlexplorer.model.JsonModel._

class FeatureService(context: ActorRefFactory, allFeatures: Set[Feature])(implicit exceptionHandler: ExceptionHandler) extends HttpService {

  def routes = getFeatures ~ getFeature

  def actorRefFactory = context

  def getFeatures = path("features") {
    get {
      complete { allFeatures }
    }
  }

  def getFeature = path("features" / Segment) { feature =>
    get {
      allFeatures.find(_.name == feature) match {
        case Some(f) => complete(f)
        case None => complete(StatusCodes.NotFound, s"key not found: $feature")
      }
    }
  }
}
