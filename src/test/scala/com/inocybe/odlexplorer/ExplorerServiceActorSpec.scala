package com.inocybe.odlexplorer

import akka.actor.{Props, Actor, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.inocybe.odlexplorer.Receptionist.{Updated, Crawl}
import com.inocybe.odlexplorer.model.Feature
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}
import spray.http.{StatusCodes, HttpResponse}
import spray.httpx.RequestBuilding._
import scala.concurrent.duration._
import spray.json._
import model.JsonModel._

object ExplorerServiceActorSpec {
  val features = List(
    Feature("f1", "1.0", None, List(), List()),
    Feature("f2", "1.0", None, List(), List()),
    Feature("f3", "1.0", None, List(), List())
  )

  val result = Map(
    "project1" -> Set(features(0), features(1), features(2)),
    "project2" -> Set(features(2))
  )
  
  class FakeReceptionist extends Actor {
    import context.dispatcher
    def receive = {
      case Crawl => context.system.scheduler.scheduleOnce(1.second, sender, Updated(result))
    }
  }
  
  def fakeServiceActor: Props = Props(new ExplorerServiceActor() {
    override def receptionistProps = Props[FakeReceptionist]
  })
}

class ExplorerServiceActorSpec extends TestKit(ActorSystem("ExplorerServiceActorSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender {
  import ExplorerServiceActorSpec._
  import system.dispatcher

  "ExplorerServiceActor" must {
    "be updated after a crawl" in {
      val service = system.actorOf(fakeServiceActor, "crawlUpdate")
      service ! Get("/projects/project1/features")
      expectMsgPF(1.second){
        case HttpResponse(StatusCodes.NotFound, _, _, _) =>
      }
      system.scheduler.scheduleOnce(2.seconds, service, Get("/projects/project1/features"))
      expectMsgPF(3.seconds){
        case HttpResponse(StatusCodes.OK, entity, _, _) if entity.asString.parseJson.convertTo[Set[Feature]] == result("project1") =>
      }
    }
  }
}
