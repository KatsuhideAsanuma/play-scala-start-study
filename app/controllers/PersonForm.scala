package controllers

object PersonForm {
  import play.api.data.Forms._
  import play.api.data._

  case class Data(name: String, mail: String, tel: String)

  val form = Form(
    mapping(
      "name" -> text,
      "mail" -> text,
        "tel" -> text
    )(Data.apply)(Data.unapply)
  )
}

