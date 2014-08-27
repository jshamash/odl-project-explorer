package models

case class Feature(name: String, desc: String, version: String, project: String, content: List[String])

object Features{

  def fromXml(node: scala.xml.Node, project : String):Seq[Feature] = {
      val features = (node \ "feature")
      val featureList = features.map(  feature =>
      { val name = (feature \ "@name").text
      val description = (feature \ "@description").text
      val version = (feature \  "@version").text
      val content = feature.text.split("\\s").toList.filter( string => (string != "") )
      new Feature(name, description, version, project, content) } )
      featureList
    }
}