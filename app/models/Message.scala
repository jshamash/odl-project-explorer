package models

trait Message

case class Delete(resource: String) extends Message
