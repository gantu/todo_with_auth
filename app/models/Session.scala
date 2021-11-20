package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import java.time.LocalDateTime
import scala.concurrent.{ExecutionContext, Future}
import java.util.UUID

case class Session(id: String, token: String, username: String, expires: LocalDateTime)

class SessionTableDef(tag: Tag) extends Table[Session](tag, "session") {
  def id = column[String]("id", O.PrimaryKey)
  def token = column[String]("token")
  def username = column[String]("username")
  def expires = column[LocalDateTime]("expires")

  override def * = (id, token, username, expires) <> (Session.tupled, Session.unapply)
}


class SessionDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  val sessions = TableQuery[SessionTableDef]

  //[token1, expired] -->  //[token1, notexpired]
  //[token1, expired]
  // [token1, expired]
  //[token1, notexpired]

  //------generated (22:05)   ------ 20:05 ----- 21:05  ---- 22:06  --> login (invalidate the token)

  def getSession(token: String): Future[Session] = {
    val query = sessions.filter(_.token === token).filter(_.expires > LocalDateTime.now()).result.head
    dbConfig.db.run(query)
  }

  def generateToken(username: String): Future[Option[Session]] = {
    val token = s"$username-token-${UUID.randomUUID().toString}"
    val session = Session(UUID.randomUUID().toString, token, username, LocalDateTime.now().plusHours(6))
    dbConfig.db.run(sessions += session).map(res => Some(session)).recover {
      case ex: Exception => None
    }
  }
}
