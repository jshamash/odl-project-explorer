package controllers

import scala.concurrent.Await
import scala.concurrent.duration.DurationInt

import akka.actor.ActorRef
import akka.pattern.ask
import akka.util.Timeout
import play.api.mvc.Action
import play.api.mvc.Controller

object DownloadController extends Controller {

  var crawler : ActorRef = null
  implicit val timeout = Timeout(5 minutes)

  def index = Action { implicit request =>
    Ok(views.html.downloadwidget)
  }

}