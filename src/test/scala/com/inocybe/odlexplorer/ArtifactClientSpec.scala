package com.inocybe.odlexplorer

import com.inocybe.odlexplorer.model.Repository
import com.typesafe.config.ConfigFactory
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}


object ArtifactClientSpec {
  val config = ConfigFactory.load()
  val rootFeatureGroup = config.getString("repo.rootFeatureFile.group")
  val rootFeatureArtifact = config.getString("repo.rootFeatureFile.artifact")
  val rootRepo = Repository(rootFeatureGroup, rootFeatureArtifact, None)
}

class ArtifactClientSpec extends FlatSpec with Matchers with ScalaFutures {
  import ArtifactClientSpec._
  "An ODLArtifactClient" should "return features" in {
    import scala.concurrent.ExecutionContext.Implicits.global
    val future = ODLArtifactClient.get(rootRepo)
    whenReady(future, timeout(Span(5, Seconds))) { xml =>
      (xml \ "feature").size should be > 0
    }
  }
}
