package models

import com.google.inject.Inject
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import java.util.NoSuchElementException
import scala.concurrent.{ExecutionContext, Future}

case class User(username: String, password: String)

class UserTableDef(tag: Tag) extends Table[User](tag, "users") {
  def username = column[String]("username", O.PrimaryKey)
  def password = column[String]("password")

  override def * = (username, password) <> (User.tupled, User.unapply)
}

class UserDAO @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)(implicit executionContext: ExecutionContext)
  extends HasDatabaseConfigProvider[JdbcProfile] {

  val users = TableQuery[UserTableDef]

  def getUser(username: String): Future[Option[User]] ={
    val query = users.filter(_.username === username).result.headOption
    dbConfig.db.run(query)
  }

  def getUser(username: String, password: String): Future[Option[User]] = {
    val query = users.filter(_.username === username).filter(_.password === password).result.headOption
    dbConfig.db.run(query)
  }

  def addUser(userName: String, password: String, userMap: Map[String, User]): Map[String, User] = {
    val existingUser = userMap.get(userName)
    existingUser match {
      case Some(u) => userMap
      case None => userMap + (userName -> User(userName, password))
    }
  }
}
