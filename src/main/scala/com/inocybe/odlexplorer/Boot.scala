package com.inocybe.odlexplorer

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import com.typesafe.config.ConfigFactory
import spray.can.Http
import akka.pattern.ask
import akka.util.Timeout
import scala.concurrent.duration._

object Boot extends App {

  val config = ConfigFactory.load()
  val interface = config.getString("service.interface")
  val port = config.getInt("service.port")

  // we need an ActorSystem to host our application in
  implicit val system = ActorSystem("odl-project-explorer")

  // create and start our service actor
  val service = system.actorOf(Props[ExplorerServiceActor], "explorer-service")

  implicit val timeout = Timeout(5.seconds)
  // start a new HTTP server on port 8080 with our service actor as the handler
  IO(Http) ? Http.Bind(service, interface = interface, port = port)
}
