package controllers

import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.libs.json.{JsValue, Json}
import service.ModelService

import javax.inject.Inject

class ModelController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  def generateExternalModel: Action[AnyContent] = Action { implicit request =>
    val content = request.body
    val jsonObject: Option[JsValue] = content.asJson

    jsonObject match {
      case Some(json) =>
        (json \ "prompt").asOpt[String] match {
          case Some(prompt) =>
            val res = ModelService.generateExternal(prompt)
            Ok(Json.obj("result" -> res))
          case None =>
            BadRequest(Json.obj("error" -> "Missing 'prompt' in request body"))
        }
      case None =>
        BadRequest(Json.obj("error" -> "Invalid JSON in request body"))
    }
  }
  def generateLocalModel: Action[AnyContent] = Action { implicit request =>
    val content = request.body
    val jsonObject: Option[JsValue] = content.asJson

    jsonObject match {
      case Some(json) =>
        (json \ "prompt").asOpt[String] match {
          case Some(prompt) =>
            val res = ModelService.generate(prompt)
            Ok(Json.obj("result" -> res))
          case None =>
            BadRequest(Json.obj("error" -> "Missing 'prompt' in request body"))
        }
      case None =>
        BadRequest(Json.obj("error" -> "Invalid JSON in request body"))
    }
  }


}
