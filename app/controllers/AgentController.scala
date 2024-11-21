package controllers

import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}

import javax.inject.Inject

class AgentController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  private val conf = ConfigFactory.load()
  private val host = conf.getString("Ollama.host")
  private val timeout = conf.getInt("Ollama.request-timeout-seconds")
  private val model = conf.getString("Ollama.model")
  private val logger = LoggerFactory.getLogger(this.getClass.getName)

  def converse(seed: String): Action[AnyContent] = Action { implicit request =>
    Ok
  }
}
