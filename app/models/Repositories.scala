package models

case class Repository(group: String, artifact: String, version: String)

object Repositories{

  // TODO
  def fromXml(node: scala.xml.Node):Seq[Repository] = {
    ???
  }
}