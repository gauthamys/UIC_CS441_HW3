package controllers

import com.typesafe.config.ConfigFactory
import io.github.ollama4j.OllamaAPI
import io.github.ollama4j.utils.Options
import org.slf4j.LoggerFactory
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.libs.json.{JsValue, Json}
import service.ModelService

import javax.inject.Inject
import java.util
import scala.collection.mutable.ArrayBuffer

class AgentController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  private val conf = ConfigFactory.load()
  private val host = conf.getString("Ollama.host")
  private val timeout = conf.getInt("Ollama.request-timeout-seconds")
  private val model = conf.getString("Ollama.model")
  private val numReplies = conf.getInt("Conversation.numReplies")
  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  def converse: Action[AnyContent] = Action { implicit request =>
    val content = request.body
    val jsonObject: Option[JsValue] = content.asJson

    jsonObject match {
      case Some(json) =>
        (json \ "prompt").asOpt[String] match {
          case Some(seed) =>
            val ollamaApi = new OllamaAPI(host)
            ollamaApi.setRequestTimeoutSeconds(timeout)
            val prompt = s"how can you respond to the statement: $seed"
            val res = new ArrayBuffer[String]()
            res.addOne(seed)
            var i = 0
            var inter = prompt
            logger.info(s"Starting conversation with prompt $inter... ")
            while(i < numReplies) {
              Thread.sleep(20 * 1000)
              inter = ModelService.generateExternal(inter)
              logger.info(s"Claude: $inter")
              res.addOne(inter)
              inter = ollamaApi.generate(model, inter, false, new Options(new util.HashMap[String, Object])).getResponse
              logger.info(s"Ollama: $inter")
              res.addOne(inter)
              i = i + 1
            }
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
