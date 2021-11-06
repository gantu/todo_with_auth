package models

import org.joda.time.{LocalDate, LocalDateTime}

import java.util.UUID

case class Session(token: String, userName: String, expires: LocalDateTime)

object SessionDAO {

  def getSession(token: String, sessionMap: Map[String, Session]): Option[Session] = {
    sessionMap.get(token)
  }

  def generateToken(userName: String, sessionMap: Map[String, Session]): Map[String, Session] = {
    val token = s"$userName-token-${UUID.randomUUID().toString}"
    sessionMap + (token -> Session(token, userName, LocalDateTime.now().plusHours(6)))
  }
}
