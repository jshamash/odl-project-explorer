package com.inocybe.odlexplorer

import akka.actor._
import com.inocybe.odlexplorer.model.{Repository, Feature}
import scala.concurrent.duration._

object Controller {
  case class Check(repo: Repository)
  case class Feature(project: String, feature: model.Feature)
  case class Result(features: Map[String, Set[model.Feature]])
}

class Controller extends Actor with ActorLogging {

  var features = Map.empty[String, Set[Feature]]
  var children = Set.empty[ActorRef]
  var cache = Set.empty[Repository]

  context.setReceiveTimeout(10.seconds)

  def parserProps(repo: Repository): Props = Props(new Parser(repo))

  def receive = {
    case Controller.Check(repo) =>
      log.debug("checking repository {}::{}::{}", repo.group, repo.artifact, repo.version)
      if (!cache(repo))
        children += context.actorOf(parserProps(repo))
      cache += repo
    case Controller.Feature(project, feature: model.Feature) =>
      val prev = features getOrElse (project, Set())
      if (!prev(feature))
        features += (project -> (prev + feature))
    case Parser.Done =>
      children -= sender
      if (children.isEmpty)
        context.parent ! Controller.Result(features)
    case ReceiveTimeout =>
      log.warning("got receive timeout")
      context.children foreach (_ ! Parser.Abort)
  }
}
