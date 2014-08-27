package controllers

import java.io.FileInputStream
import scala.collection.JavaConverters.seqAsJavaListConverter
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.DefaultRepositorySystemSession
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.ArtifactRequest
import org.eclipse.aether.resolution.VersionRangeRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import akka.actor.Actor
import akka.actor.actorRef2Scala
import play.api.Logger
import models.Repositories

case class Project(name: String)

class CrawlerActor extends Actor {
  var features: Seq[models.Feature] = Seq.empty[models.Feature]
  var featuresPerProject : Map[String, Seq[models.Feature]] = Map.empty[String, Seq[models.Feature]]

  val repository = new RemoteRepository.Builder( "central", "default", "http://nexus.opendaylight.org/content/repositories/opendaylight.snapshot" ).build()
  val repositories = List(repository).asJava

  def receive = {
    case "crawl" =>
      Logger.info("Crawl initiated.")
      features = getLatestFeatures
      //println("Features: \n" + features)
    case "getFeatures" =>
      Logger.info("Received a getFeature message")
      sender ! features
    case "getProjects" =>
      sender ! featuresPerProject.keys.toList
    case Project(project) =>
      sender ! featuresPerProject(project)
    case "test" =>
      Logger.info("Testing crawler message")
      sender ! getLatestFeatures2
    case _      =>
      println("AKKA: unknown message received")
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

  def crawlRootFile = {
    val repoSystem : RepositorySystem = newRepositorySystem()
    val session : RepositorySystemSession = newSession(repoSystem)

    // Root feature file
    // TODO
    /*
    val group = "org.opendaylight.integration"
    val artifact = "features-integration"
    //val artifactToSearch = new DefaultArtifact( group, artifact,"features","xml",latestVersion(group,artifact),null )
    val artifactToSearch = new DefaultArtifact( group, artifact,"features","xml","0.2.0-SNAPSHOT",null )

    val artifactRequest = new ArtifactRequest()
    artifactRequest.setArtifact( artifactToSearch )
    artifactRequest.setRepositories( repositories )

    val artifactResult = repoSystem.resolveArtifact( session, artifactRequest )

    val file = artifactResult.getArtifact().getFile()
    val xml = scala.xml.XML.load(new FileInputStream(file) )
    */
    val repos = Repositories.fromXml(Repositories.testXml)
    println("Repos : " + repos.mkString(", "))
    repos
  }

  def getFeatures(group : String, artifact : String) : Seq[models.Feature] = {
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
    val features = models.Features.fromXml(xml, project)
    features
  }

  // Temporary
  def getLatestFeatures : Seq[models.Feature] = {
    getFeatures("org.opendaylight.controller","base-features")
  }

  def getLatestFeatures2 = {
    val repoSystem : RepositorySystem = newRepositorySystem()
    val session : RepositorySystemSession = newSession(repoSystem)

    crawlRootFile.foreach(repo => {
      println(repo)
      featuresPerProject += (repo.group -> getFeatures(repo.group, repo.artifact))
    })

    var allFeatures = Seq.empty[models.Feature]
    featuresPerProject foreach {
      case (project, features) =>
        allFeatures = allFeatures ++ features
    }
    println("allFeatures: "+ allFeatures.mkString(", "))
    allFeatures
  }
}
