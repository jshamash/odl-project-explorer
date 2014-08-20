package models

case class Feature(name: String, desc: String, version: String)

object Features{

  def fromXml(node: scala.xml.Node):Seq[Feature] = {
      val features = (node \ "feature")
      val featureList = features.map(  feature =>
      { val name = (feature \ "@name").text
      val description = feature \ "@description"
      val version = feature \  "@version"
      new Feature(name,description.text,version.text) } )
      featureList
    }
}