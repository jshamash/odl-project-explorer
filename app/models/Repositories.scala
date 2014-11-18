package models

case class Repository(group: String, artifact: String, version: String)

object Repositories{
  def fromXml(node: scala.xml.Node):List[Repository] = {
    val repositories = node \ "repository"
    val repositoryList = repositories.map( repo => {
      val repostring = repo.text.split("/")
      Repository(repostring(0).split(":")(1), repostring(1), repostring(2))
    } )
    repositoryList.toList
  }
}