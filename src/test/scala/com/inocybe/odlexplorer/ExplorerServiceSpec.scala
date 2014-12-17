package com.inocybe.odlexplorer

import com.inocybe.odlexplorer.model.{Project, Feature}
import org.scalatest._
import spray.testkit.ScalatestRouteTest
import spray.http.StatusCodes._

object ExplorerServiceSpec {

  val features = List(
    Feature("f1", "1.0", None, List(), List()),
    Feature("f2", "1.0", None, List(), List()),
    Feature("f3", "1.0", None, List(), List())
  )
  val projects = List(
    Project("project1"),
    Project("project2")
  )
  val p2f = Map(
    "project1" -> Set(features(0), features(1), features(2)),
    "project2" -> Set(features(2))
  )
}

class ExplorerServiceSpec extends FlatSpec with ScalatestRouteTest with Matchers with ExplorerService {

  import ExplorerServiceSpec._
  import model.JsonModel._

  def actorRefFactory = system

  val route = myRoute(p2f)

  "ExplorerService" should "return a list of projects for GET /projects" in {
      Get("/projects") ~> route ~> check {
        responseAs[List[Project]] === projects
      }
    }

    it should "return all features on GET /features" in {
      Get("/features") ~> route ~> check {
        responseAs[List[Feature]] === features
      }
    }

    it should "return a NotFound error when a resource doesn't exist" in {
      Get("/projects/kermit/features") ~> sealRoute(route) ~> check {
        status === NotFound
        responseAs[String] === "key not found: kermit"
      }
      Get("/features/kermit") ~> sealRoute(route) ~> check {
        status === NotFound
        responseAs[String] === "key not found: kermit"
      }
    }

    it should "leave GET requests to other paths unhandled" in {
      Get("/kermit") ~> route ~> check {
        handled shouldBe false
      }
    }

    it should "return a MethodNotAllowed error for PUT requests to a path" in {
      Put("/projects") ~> sealRoute(route) ~> check {
        status === MethodNotAllowed
        responseAs[String] === "HTTP method not allowed, supported methods: GET"
      }
    }
}
