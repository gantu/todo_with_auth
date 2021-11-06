package models

case class User(username: String, password: String)

object UserDAO {

  def getUser(userName: String, password: String, userMap: Map[String, User]): Option[User] = {
    userMap.get(userName).filter(u => u.username.equals(userName) && u.password == password)
  }

  def addUser(userName: String, password: String, userMap: Map[String, User]): Map[String, User] = {
    val existingUser = userMap.get(userName)
    existingUser match {
      case Some(u) => userMap
      case None => userMap + (userName -> User(userName, password))
    }
  }
}