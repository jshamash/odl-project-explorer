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

class CrawlerActor extends Actor {
  var features: Seq[models.Feature] = Seq.empty[models.Feature]
  var featuresPerProject : Map[String, Seq[models.Feature]]

  def receive = {
    case "crawl" =>
      Logger.info("Crawl initiated.")
      features = getLatestFeatures
      println("Features: \n" + features)
    case "getFeatures" =>
      Logger.info("Received a getFeature message")
      sender ! features
    case _      =>
      println("AKKA: unknown message received")
      Logger.info("The crawler received an unknown message")
  }

  val repository = new RemoteRepository.Builder( "central", "default", "http://nexus.opendaylight.org/content/repositories/opendaylight.snapshot" ).build()
  val repositories = List(repository).asJava
  
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
    val group = ""
    val artifact = ""
    val artifactToSearch = new DefaultArtifact( group, artifact,"features","xml",latestVersion(group,artifact),null )
    
    val artifactRequest = new ArtifactRequest()
    artifactRequest.setArtifact( artifactToSearch )
    artifactRequest.setRepositories( repositories )

    val artifactResult = repoSystem.resolveArtifact( session, artifactRequest )

    val file = artifactResult.getArtifact().getFile()
    val xml = scala.xml.XML.load(new FileInputStream(file) )
    val artifacts = Repositories.fromXml(xml)
    artifacts
  }
  
  def getFeatures(group : String, artifact : String) : Seq[models.Feature] = {
    val repoSystem : RepositorySystem = newRepositorySystem()
    val session : RepositorySystemSession = newSession(repoSystem)

    val artifactToSearch = new DefaultArtifact( group, artifact,"features","xml",latestVersion(group,artifact),null )
    
    val artifactRequest = new ArtifactRequest()
    artifactRequest.setArtifact( artifactToSearch )
    artifactRequest.setRepositories( repositories )

    val artifactResult = repoSystem.resolveArtifact( session, artifactRequest )

    val file = artifactResult.getArtifact().getFile()
    val xml = scala.xml.XML.load(new FileInputStream(file) )
    val features = models.Features.fromXml(xml)
    features
  }
  
  // Temporary
  def getLatestFeatures : Seq[models.Feature] = {
    getFeatures("org.opendaylight.controller","base-features")
  }
  
  // Final version
  // TODO
  def getLatestFeatures2 = {
    val repoSystem : RepositorySystem = newRepositorySystem()
    val session : RepositorySystemSession = newSession(repoSystem)

    crawlRootFile.foreach(repo => 
      featuresPerProject += (repo.group -> getFeatures(repo.group, repo.artifact)
    ))
    
    var allFeatures = Seq.empty[models.Feature]
    featuresPerProject foreach {
      case (project, features) =>
        allFeatures = allFeatures ++ features
    }
    allFeatures
  }
}
