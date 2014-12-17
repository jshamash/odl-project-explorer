package com.inocybe.odlexplorer

import akka.actor.{ActorRef, Props, Actor, ActorSystem}
import akka.testkit.{ImplicitSender, TestKit}
import com.inocybe.odlexplorer.model.{SubFeature, Feature, Repository}
import org.scalatest.{Matchers, BeforeAndAfterAll, WordSpecLike}

import scala.concurrent.{Future, ExecutionContext}
import scala.xml.Node


object ParserSpec {
  val rootRepo = Repository("org.opendaylight.repoA", "projectA", Some("1.0"))
  val bodies: Map[Repository, Node] = Map(
    rootRepo ->
      <features>
        <repository>mvn:org.opendaylight.repoB/projectA/1.0/xml/features</repository>
        <feature version="0.3.0-SNAPSHOT" name="RepoA-projectA-Feature1">
          <feature version="0.2.0-SNAPSHOT">subFeature1</feature>
          <feature version="0.2.0-SNAPSHOT">subFeature2</feature>
        </feature>
        <feature version="0.1.0-SNAPSHOT" name="RepoA-projectA-Feature2" description="RepoA :: projectA :: Feature2">
          <feature version="0.2.0-SNAPSHOT">subFeature1</feature>
          <bundle>mvn:blah/blah/0.1.0-SNAPSHOT</bundle>
        </feature>
      </features>
    )
  
  val features = Map(
    rootRepo -> (
      Feature("RepoA-projectA-Feature1", "0.3.0-SNAPSHOT", None, List(), SubFeature("subFeature1", "0.2.0-SNAPSHOT") :: SubFeature("subFeature2", "0.2.0-SNAPSHOT") :: Nil) ::
      Feature("RepoA-projectA-Feature2", "0.1.0-SNAPSHOT", Some("RepoA :: projectA :: Feature2"), List("mvn:blah/blah/0.1.0-SNAPSHOT"), SubFeature("subFeature1", "0.2.0-SNAPSHOT") :: Nil) ::
      Nil
    )
  )
  
  val repos = Map(
    rootRepo -> (Repository("org.opendaylight.repoB", "projectA", Some("1.0")) :: Nil)
  )

  object FakeClient extends ArtifactClient {
    def get(repo: Repository)(implicit ec: ExecutionContext): Future[Node] =
    bodies get repo match {
      case None => Future.failed(new RuntimeException("No such repository"))
      case Some(xml) => Future.successful(xml)
    }
  }

  def fakeParser(repo: Repository): Props =
    Props(new Parser(repo) {
      override def artifactClient = FakeClient
    })
}

class ParserSpec extends TestKit(ActorSystem("ParserSpec"))
  with WordSpecLike with BeforeAndAfterAll with ImplicitSender with Matchers {
  
  import ParserSpec._
  
  override def afterAll(): Unit = {
    system.shutdown()
  }
  
  "A Parser" must {

    "return the right features and repos" in {
      val parser = system.actorOf(Props(new StepParent(fakeParser(rootRepo), testActor)), "rightFeaturesAndRepos")
      for (feature <- features(rootRepo))
        expectMsg(Controller.Feature(rootRepo.project, feature))
      for (repo <- repos(rootRepo))
        expectMsg(Controller.Check(repo))
      expectMsg(Parser.Done)
    }

    "properly finish in case of errors" in {
      val parser = system.actorOf(Props(new StepParent(fakeParser(Repository("fake", "fake", Some("fake"))), testActor)), "wrongRepo")
      expectMsg(Parser.Done)
    }
  }
}
