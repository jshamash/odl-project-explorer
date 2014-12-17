package com.inocybe.odlexplorer

import akka.actor.{Actor, ActorRef, Props}

object StepParent {
  case class SendToChild(msg: Any)
}
class StepParent(childProps: Props, fwd: ActorRef) extends Actor {
  import StepParent._
  val child = context.actorOf(childProps, "child")
  def receive = {
    case SendToChild(msg) => child ! msg
    case msg => fwd forward msg
  }
}
