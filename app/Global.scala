import play.api.{Application, GlobalSettings}
import controllers.CrawlerActor
import play.api.libs.concurrent.Akka
import akka.actor.Props
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import play.api.Logger

object Global extends GlobalSettings{

  override def onStart(app: Application) {
    val controllerPath = controllers.routes.FeaturesController.list.url
    play.api.Play.mode(app) match {
      case play.api.Mode.Test => // do not schedule anything for Test
      case _ => crawlerDaemon(app)
    }
  }

  def crawlerDaemon(app: Application) = {
    Logger.info("The crawler is started")
    val crawlerActor = Akka.system(app).actorOf(Props(new CrawlerActor()))
    Akka.system(app).scheduler.schedule(0 seconds, 5 minutes, crawlerActor, "crawl")
  }
}
