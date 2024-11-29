package controllers

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.libs.json.{JsValue, Json}
import protobuf.service.{GenerateExternalRequest, GenerateExternalResponse}
import service.ModelService
import java.io.{BufferedInputStream, BufferedOutputStream}
import java.net.{HttpURLConnection, URL}
import javax.inject.Inject

class ModelController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  private val logger = LoggerFactory.getLogger(this.getClass.getName)
  private val conf = ConfigFactory.load()
  private val AWSHost = conf.getString("AWS.host")

  def generateExternalGrpc: Action[AnyContent] = Action { implicit request =>
    val requestBody: Option[JsValue] = request.body.asJson

    requestBody match {
      case Some(json) =>
        val prompt = (json \ "prompt").as[String]

        // Create Protobuf request
        val grpcRequest = GenerateExternalRequest(prompt = prompt)

        try {
          // Open HTTP connection
          val url = new URL(AWSHost) // Replace with actual endpoint
          val connection = url.openConnection().asInstanceOf[HttpURLConnection]
          connection.setDoOutput(true)
          connection.setRequestMethod("POST")
          connection.setRequestProperty("Content-Type", "application/x-protobuf")

          // Send the Protobuf request
          val outputStream = new BufferedOutputStream(connection.getOutputStream)
          outputStream.write(grpcRequest.toByteArray)
          outputStream.flush()
          outputStream.close()

          // Handle response
          val responseCode = connection.getResponseCode
          if (responseCode == 200) {
            val inputStream = new BufferedInputStream(connection.getInputStream)
            val responseBytes = inputStream.readAllBytes()
            inputStream.close()

            // Deserialize Protobuf response
            val grpcResponse = GenerateExternalResponse.parseFrom(responseBytes)
            Ok(Json.obj("result" -> grpcResponse.result))
          } else {
            val errorStream = Option(connection.getErrorStream)
            val errorMessage = errorStream.map(_.readAllBytes()).map(new String(_)).getOrElse("Unknown error")
            logger.error(s"HTTP error: $responseCode, body: $errorMessage")
            InternalServerError(Json.obj("error" -> "Failed to fetch external model"))
          }

        } catch {
          case e: Exception =>
            logger.error("Error during HTTP request", e)
            InternalServerError(Json.obj("error" -> "HTTP request failed"))
        }

      case None =>
        BadRequest(Json.obj("error" -> "Invalid JSON payload"))
    }
  }
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
            logger.error("Missing 'prompt' in request body")
            BadRequest(Json.obj("error" -> "Missing 'prompt' in request body"))
        }
      case None =>
        logger.error("Invalid JSON in request body")
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
            logger.error("Missing 'prompt' in request body")
            BadRequest(Json.obj("error" -> "Missing 'prompt' in request body"))
        }
      case None =>
        logger.error("Invalid JSON in request body")
        BadRequest(Json.obj("error" -> "Invalid JSON in request body"))
    }
  }

}
