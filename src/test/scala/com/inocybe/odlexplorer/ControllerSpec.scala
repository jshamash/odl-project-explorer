package com.inocybe.odlexplorer

import akka.actor.{ActorSystem, ActorRef, Props, Actor}
import akka.testkit.{ImplicitSender, TestKit}
import com.inocybe.odlexplorer.StepParent.SendToChild
import com.inocybe.odlexplorer.model.{Feature, Repository}
import org.scalatest.{Matchers, BeforeAndAfterAll, WordSpecLike}

object ControllerSpec {
  val r = List(
    Repository("org.opendaylight.project1", "artifact1", Some("1.0")),
    Repository("org.opendaylight.project1", "artifact2", Some("1.0")),
    Repository("org.opendaylight.project2", "artifact1", Some("1.0")),
    Repository("org.opendaylight.project3", "artifact1", Some("1.0"))
  )
  val f = List(
    Feature("f1", "1.0", None, List(), List()),
    Feature("f2", "1.0", None, List(), List()),
    Feature("f3", "1.0", None, List(), List())
  )
  val result = Map(
    "project1" -> Set(f(0), f(1), f(2)),
    "project2" -> Set(f(2))
  )
  
  val artifacts: Map[Repository, List[Any]] = Map(
    r(3) -> List(r(0), r(2)),
    r(2) -> List(f(2)),
    r(0) -> List(r(1), f(0), f(2)),
    r(1) -> List(r(0), f(0), f(1))
  )

  class FakeParser(repo: Repository) extends Actor {
    artifacts(repo) map {
      case r: Repository => context.parent ! Controller.Check(r)
      case f: Feature => context.parent ! Controller.Feature(repo.project, f)
    }
    context.parent ! Parser.Done
    context.stop(self)
    def receive = {
      case _ =>
    }
  }

  def fakeController(): Props = Props(new Controller() {
    override def parserProps(repo: Repository) = Props(new FakeParser(repo))
  })
}

class ControllerSpec extends TestKit(ActorSystem("ControllerSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender with Matchers {

  import ControllerSpec._

  override def afterAll(): Unit = {
    system.shutdown()
  }

  "A Controller" must {
    "return the correct features map" in {
      val controller = system.actorOf(Props(new StepParent(fakeController(), testActor)), "correctFeatures")
      controller ! SendToChild(Controller.Check(r(3)))
      expectMsg(Controller.Result(result))
    }
  }

}
