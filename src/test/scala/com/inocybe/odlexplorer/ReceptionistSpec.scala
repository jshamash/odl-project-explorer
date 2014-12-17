package com.inocybe.odlexplorer

import akka.actor.{Actor, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.inocybe.odlexplorer.StepParent._
import com.inocybe.odlexplorer.model.Feature
import org.scalatest.{BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.duration._

object ReceptionistSpec {

  val f = List(
    Feature("f1", "1.0", None, List(), List()),
    Feature("f2", "1.0", None, List(), List()),
    Feature("f3", "1.0", None, List(), List())
  )

  val result = Map(
    "project1" -> Set(f(0), f(1), f(2)),
    "project2" -> Set(f(2))
  )

  class FakeController extends Actor {
    import context.dispatcher
    def receive = {
      case Controller.Check(repo) =>
        context.system.scheduler.scheduleOnce(1.second, sender, Controller.Result(result))
    }
  }

  def fakeReceptionist: Props =
    Props(new Receptionist {
      override def controllerProps = Props[FakeController]
    })
}

class ReceptionistSpec extends TestKit(ActorSystem("ReceptionistSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender{

  import com.inocybe.odlexplorer.Receptionist._
  import com.inocybe.odlexplorer.ReceptionistSpec._

  override def afterAll(): Unit = {
    system.shutdown()
  }

  "A Receptionist" must {
    "update parent after a crawl message" in {
      val receptionist = system.actorOf(Props(new StepParent(fakeReceptionist, testActor)), "crawlResponse")
      receptionist ! SendToChild(Crawl)
      expectMsg(Updated(result))
    }

    "ignore overlapping crawl requests" in {
      val receptionist = system.actorOf(Props(new StepParent(fakeReceptionist, testActor)), "crawlIgnore")
      receptionist ! SendToChild(Crawl)
      receptionist ! SendToChild(Crawl)
      expectMsg(Updated(result))
      expectNoMsg()
    }
  }
}
