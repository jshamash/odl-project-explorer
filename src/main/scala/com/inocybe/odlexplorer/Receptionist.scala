package com.inocybe.odlexplorer

import akka.actor.{Status, ActorLogging, Props, Actor}
import com.inocybe.odlexplorer.model.{Project, Feature, Repository}
import com.typesafe.config.ConfigFactory

object Receptionist {
  case object Crawl
  case class Updated(projectToFeatures: Map[String, Set[Feature]])
}

// todo merge Controller's result with preconfigured JSON
class Receptionist extends Actor with ActorLogging {

  import Receptionist._

  var projectToFeatures = Map.empty[String, Set[Feature]]
  var allFeatures = Set.empty[Feature]

  val rootRepo: Repository = {
    val config = ConfigFactory.load()
    val rootFeatureGroup = config.getString("repo.rootFeatureFile.group")
    val rootFeatureArtifact = config.getString("repo.rootFeatureFile.artifact")
    Repository(rootFeatureGroup, rootFeatureArtifact, None)
  }

  def controllerProps: Props = Props[Controller]

  def receive = waiting

  def waiting: Receive = {
    case Crawl => context.become(startCrawl)
  }

  def crawling: Receive = {
    case Crawl => log.warning("crawl is already under way")
    case Controller.Result(res) =>
      context.parent ! Updated(res)
      context.stop(sender)
      context.become(waiting)
  }

  def startCrawl: Receive = {
    val controller = context.actorOf(controllerProps, "controller")
    controller ! Controller.Check(rootRepo)
    crawling
  }
}
