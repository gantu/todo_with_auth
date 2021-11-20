package service

import com.google.inject.Inject
import models.{Session, SessionDAO, User, UserDAO}

import scala.concurrent.Future

class AuthService @Inject()(userDAO: UserDAO, sessionDAO: SessionDAO){
  def generateToken(username: String): Future[Option[Session]]= {
    sessionDAO.generateToken(username)
  }

  def getUserByUsernameAndPassword(username: String, password: String): Future[User] = {
    userDAO.getUser(username, password)
  }

  def getUserByUsername(username: String): Future[User] = {
    userDAO.getUser(username)
  }

  def getSession(token: String): Future[Session] = sessionDAO.getSession(token)
}
