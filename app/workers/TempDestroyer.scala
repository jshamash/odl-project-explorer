package workers

import akka.actor.{PoisonPill, Actor}
import models.Delete
import play.api.{Logger, Play}
import play.api.Play.current
import org.apache.commons.io.FileUtils

class TempDestroyer extends Actor {
  def receive = {
    case Delete(resource) =>
      Logger.info(s"Cleaning up resource $resource")
      FileUtils.deleteDirectory(Play.getFile(resource))
      self ! PoisonPill
  }
}
