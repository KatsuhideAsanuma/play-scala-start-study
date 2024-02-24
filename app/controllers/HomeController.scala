package controllers

import java.sql._
import javax.inject._
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import play.api.db._
import PersonForm._

@Singleton
class HomeController @Inject() (db: Database, cc: MessagesControllerComponents)
    extends MessagesAbstractController(cc) {

  def index() = Action { implicit request =>
    var msg = "database record:<br><ul>"
    try {
      db.withConnection { conn =>
        val stmt = conn.createStatement
        val rs = stmt.executeQuery("SELECT * FROM people")
        while (rs.next) {
          msg += "<li>" + rs.getInt("id") + ":" + rs.getString("name") + "</li>"
        }
        msg += "</ul>"
      }
    } catch {
      case e: SQLException => msg = "exception: " + e.getMessage
    }
    Ok(views.html.index(msg))
  }

  def add() = Action { implicit request =>
    Ok(views.html.add("Fix Forms!", form))
  }

  def create() = Action { implicit request =>
    val formData = form.bindFromRequest()
    val data = formData.get
    try {
      db.withConnection { conn =>
        val ps =
          conn.prepareStatement("INSERT INTO people values (default, ?, ?, ?)")
        ps.setString(1, data.name)
        ps.setString(2, data.mail)
        ps.setString(3, data.tel)
        ps.executeUpdate
      }
    } catch {
      case e: SQLException =>
        Ok(views.html.add("Error, fix them correctly.", form))
    }
    Redirect(routes.HomeController.index())
  }

  def edit(id: Int) = Action { implicit request =>
    var formdata = form.bindFromRequest()
    try {
      db.withConnection { conn =>
        val stmt = conn.createStatement
        val rs = stmt.executeQuery("SELECT * FROM people WHERE id=" + id)
        rs.next
        val name = rs.getString("name")
        val mail = rs.getString("mail")
        val tel = rs.getString("tel")
        val data = Data(name, mail, tel)
        formdata = form.fill(data)
        Ok(views.html.edit("Edit Form!", formdata,id))
      }
    } catch {
      case e: SQLException =>
        Redirect(routes.HomeController.index())
    }
  }

  def update(id: Int) = Action { implicit request =>
    val formData = form.bindFromRequest()
    val data = formData.get
    try {
      db.withConnection { conn =>
        val ps =
          conn.prepareStatement("UPDATE people SET name=?, mail=?, tel=? WHERE id=?")
        ps.setString(1, data.name)
        ps.setString(2, data.mail)
        ps.setString(3, data.tel)
        ps.setInt(4, id)
        ps.executeUpdate
      }
    } catch {
      case e: SQLException =>
        Ok(views.html.edit("Error, fix them correctly.", formData,id))
    }
    Redirect(routes.HomeController.index())
  }

  def delete(id: Int) = Action { implicit request =>
    var pdata:Data = null;
    try {
      db.withConnection { conn =>
        val stmt = conn.createStatement
        val rs = stmt.executeQuery("SELECT * FROM people WHERE id=" + id)
        rs.next
        val name = rs.getString("name")
        val mail = rs.getString("mail")
        val tel = rs.getString("tel")
        pdata = Data(name, mail, tel)
      }
      Ok(views.html.delete("Delete Form!", pdata,id))
    } catch {
      case e: SQLException =>
        Redirect(routes.HomeController.index())
    }
  }

  def remove(id: Int) = Action { implicit request =>
    try {
      db.withConnection { conn =>
        val ps = conn.prepareStatement("DELETE FROM people WHERE id=?")
        ps.setInt(1, id)
        ps.executeUpdate
      }
    } catch {
      case e: SQLException =>
        Redirect(routes.HomeController.index())
    }
    Redirect(routes.HomeController.index())
  }
}
