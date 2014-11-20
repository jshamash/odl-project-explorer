package controllers

import java.io.FileInputStream

import akka.actor.{Actor, actorRef2Scala}
import models.{Features, Feature, Repositories, Repository}
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.{DefaultRepositorySystemSession, RepositorySystem, RepositorySystemSession}
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.{LocalRepository, RemoteRepository}
import org.eclipse.aether.resolution.{ArtifactRequest, VersionRangeRequest}
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import play.api.Logger

import scala.collection.JavaConverters.seqAsJavaListConverter

case class Project(name: String)

class CrawlerActor extends Actor {
  var features: List[Feature] = List()
  var featuresPerProject : Map[String, List[Feature]] = Map.empty[String, List[Feature]]

  val repository = new RemoteRepository.Builder( "central", "default", "http://nexus.opendaylight.org/content/repositories/opendaylight.snapshot" ).build()
  val repositories = List(repository).asJava

  // Info about root feature file
  val ROOT_FEATURE_GROUP = "org.opendaylight.integration"
  val ROOT_FEATURE_ARTIFACT = "features-integration"

  def receive = {
    case "crawl" =>
      Logger.info("Crawl initiated.")
      features = crawl(parse(ROOT_FEATURE_GROUP, ROOT_FEATURE_ARTIFACT)).distinct
      featuresPerProject = features.foldLeft(Map.empty[String, List[Feature]]){ (map, feature) =>
        val features = map.getOrElse(feature.project, List())
        map + (feature.project -> (feature :: features))
      }
      //println("Features: \n" + features)
    case "getFeatures" =>
      Logger.info("Received a getFeature message")
      sender ! features
    case "getProjects" =>
      sender ! featuresPerProject.keys.toList
    case Project(project) =>
      sender ! featuresPerProject.get(project)
    case _      =>
      Logger.info("The crawler received an unknown message")
  }


  def newRepositorySystem() = {
    val locator : DefaultServiceLocator = MavenRepositorySystemUtils.newServiceLocator()
    locator.addService( classOf[RepositoryConnectorFactory], classOf[BasicRepositoryConnectorFactory] )
    locator.addService( classOf[TransporterFactory], classOf[FileTransporterFactory] )
    locator.addService( classOf[TransporterFactory], classOf[HttpTransporterFactory] )

    locator.getService( classOf[RepositorySystem] )
  }

  def newSession(system : RepositorySystem) = {
    val session : DefaultRepositorySystemSession = MavenRepositorySystemUtils.newSession()
    val localRepo : LocalRepository = new LocalRepository( "target/local-repo" )
    session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) )
    session

  }

  def latestVersion(group : String, artifact: String) : String = {
    val repoSystem : RepositorySystem = newRepositorySystem()
    val session : RepositorySystemSession = newSession(repoSystem)

    val artifactToSearch = new DefaultArtifact(group + ":" + artifact + ":[0,)")
    val rangeRequest = new VersionRangeRequest()
    rangeRequest.setArtifact( artifactToSearch )
    rangeRequest.setRepositories(repositories)
    val rangeResult = repoSystem.resolveVersionRange( session, rangeRequest )
    val newestVersion = rangeResult.getHighestVersion

    newestVersion.toString
  }

  type FileInfo = (List[Repository], List[Feature])

  def parse(group: String, artifact: String): FileInfo = {
    //println(s"Parsing $group.$artifact")

    val repoSystem : RepositorySystem = newRepositorySystem()
    val session : RepositorySystemSession = newSession(repoSystem)
    val project = group.split("\\.")(2)

    val artifactToSearch = new DefaultArtifact( group, artifact,"features","xml",latestVersion(group,artifact),null )

    val artifactRequest = new ArtifactRequest()
    artifactRequest.setArtifact( artifactToSearch )
    artifactRequest.setRepositories( repositories )

    val artifactResult = repoSystem.resolveArtifact( session, artifactRequest )

    val file = artifactResult.getArtifact().getFile()
    val xml = scala.xml.XML.load(new FileInputStream(file) )
    val repos = Repositories.fromXml(xml)
    val features = Features.fromXml(xml, project)

    (repos, features)
  }

  val crawl: (FileInfo => List[Feature]) = {

    def helper(info: FileInfo, reposVisited: List[Repository]): List[Feature] = {
      val (repos, features) = info
      val unvisited = repos diff reposVisited
      val parsed = unvisited.map(r => parse(r.group, r.artifact))
      features ++ (parsed flatMap (helper(_, reposVisited ++ unvisited)))
    }
        //def helper2(reposVisited: List[Repository]) = (helper(_, _, reposVisited)).tupled
    helper(_, List())
  }

}
