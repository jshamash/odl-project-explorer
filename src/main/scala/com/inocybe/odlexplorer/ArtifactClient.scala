package com.inocybe.odlexplorer

import java.io.FileInputStream

import com.inocybe.odlexplorer.model.Repository
import com.typesafe.config.ConfigFactory
import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.impl.DefaultServiceLocator
import org.eclipse.aether.repository.{LocalRepository, RemoteRepository}
import org.eclipse.aether.resolution.{ArtifactRequest, VersionRangeRequest}
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.{DefaultRepositorySystemSession, RepositorySystem, RepositorySystemSession}

import scala.collection.JavaConverters._
import scala.concurrent.{ExecutionContext, Future}
import scala.xml.Node
import scala.async.Async._

trait ArtifactClient {
  def get(repo: Repository)(implicit ec: ExecutionContext): Future[Node]
}

object ODLArtifactClient extends ArtifactClient {
  private val config = ConfigFactory.load()
  private val repoUrl = config.getString("repo.url")

  private val repository = new RemoteRepository.Builder("central", "default", repoUrl).build()
  private val repositories = List(repository).asJava

  private def newRepositorySystem(): RepositorySystem = {
    val locator: DefaultServiceLocator = MavenRepositorySystemUtils.newServiceLocator()
    locator.addService(classOf[RepositoryConnectorFactory], classOf[BasicRepositoryConnectorFactory])
    locator.addService(classOf[TransporterFactory], classOf[FileTransporterFactory])
    locator.addService(classOf[TransporterFactory], classOf[HttpTransporterFactory])
    locator.getService(classOf[RepositorySystem])
  }

  private def newSession(system: RepositorySystem) = {
    val session: DefaultRepositorySystemSession = MavenRepositorySystemUtils.newSession()
    val localRepo: LocalRepository = new LocalRepository("target/local-repo")
    session.setLocalRepositoryManager(system.newLocalRepositoryManager(session, localRepo))
    session
  }

  private val repoSystem: RepositorySystem = newRepositorySystem()
  private val session: RepositorySystemSession = newSession(repoSystem)

  private def latestVersion(group: String, artifact: String): String = {
    val artifactToSearch = new DefaultArtifact(group + ":" + artifact + ":[0,)")
    val rangeRequest = new VersionRangeRequest()
    rangeRequest.setArtifact(artifactToSearch)
    rangeRequest.setRepositories(repositories)
    val rangeResult = repoSystem.resolveVersionRange(session, rangeRequest)
    val newestVersion = rangeResult.getHighestVersion

    newestVersion.toString
  }

  def get(repo: Repository)(implicit ec: ExecutionContext): Future[Node] = async {
    val (group, artifact) = (repo.group, repo.artifact)
    val version = repo.version.getOrElse(latestVersion(group, artifact))

    val artifactToSearch = new DefaultArtifact(group, artifact, "features", "xml",
      version, null)

    val artifactRequest = new ArtifactRequest()
    artifactRequest.setArtifact(artifactToSearch)
    artifactRequest.setRepositories(repositories)

    val artifactResult = repoSystem.resolveArtifact(session, artifactRequest)

    val file = artifactResult.getArtifact.getFile
    val xml = scala.xml.XML.load(new FileInputStream(file))

    xml
  }
}