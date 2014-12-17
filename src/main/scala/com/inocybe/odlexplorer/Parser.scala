package com.inocybe.odlexplorer

import akka.actor.Actor
import akka.actor.Status.Failure
import akka.pattern.pipe
import com.inocybe.odlexplorer.model.{Feature, Repository}

import scala.xml.Node

object Parser {
  case object Done
  case object Abort
}

class Parser(repo: Repository) extends Actor {
  import com.inocybe.odlexplorer.Parser._

  implicit val ec = context.dispatcher
  def artifactClient: ArtifactClient = ODLArtifactClient

  artifactClient get repo pipeTo self

  def receive = {
    case xml: Node =>
      val features = (xml \ "feature").map(Feature(_))
      val repos = (xml \ "repository").map(node => Repository(node.text))
      features foreach (context.parent ! Controller.Feature(repo.project, _))
      repos foreach (context.parent ! Controller.Check(_))
      stop()
    case _: Failure => stop()
    case Abort => stop()
  }

  def stop(): Unit = {
    context.parent ! Done
    context.stop(self)
  }
}
