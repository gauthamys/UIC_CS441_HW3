package controllers

import com.typesafe.config.ConfigFactory
import play.api.mvc.{Action, AnyContent, BaseController, ControllerComponents}
import play.api.libs.json.{JsValue, Json}
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.nd4j.linalg.factory.Nd4j
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.linalg.api.ndarray.INDArray
import utilz.{EmbeddingUtil, StrUtil}

import javax.inject.Inject
import scala.collection.convert.ImplicitConversions.`map AsScala`
import scala.util.{Failure, Success, Try}

class ModelController @Inject()(val controllerComponents: ControllerComponents) extends BaseController {
  private val modelPath = "conf/model.zip"
  private val modelTry: Try[MultiLayerNetwork] = Try(ModelSerializer.restoreMultiLayerNetwork(modelPath))
  private val conf = ConfigFactory.load()
  private val windowSize = conf.getInt("ModelController.windowSize")
  private val sentenceLen = conf.getInt("ModelController.sentenceLen")
  private val positionalEmbeddings = computePositionalEmbedding(windowSize)
  private val lookup = EmbeddingUtil.loadEmbeddings("conf/embeddings.txt")

  private def tokenizeAndEmbed(tokens: Array[String]): INDArray = {
    // get embedding from hw1
    val embeddingMatrix = Nd4j.zeros(tokens.length, 100)
    for (i <- tokens.indices) {
      val word = tokens(i)
      val embedding = lookup.getOrElse(word, Array.fill(100)(0.0))
      embeddingMatrix.putRow(i, Nd4j.create(embedding))
    }
    embeddingMatrix
  }

  // Compute sinusoidal positional embeddings for a given window size
  private def computePositionalEmbedding(windowSize: Int): INDArray = {
    val embeddingDim = 100 // Dimensionality of word embeddings
    val positionalEncoding = Nd4j.zeros(windowSize, embeddingDim)

    for (pos <- 0 until windowSize) {
      for (i <- 0 until embeddingDim by 2) {
        val angle = pos / math.pow(10000, (2.0 * i) / embeddingDim)
        positionalEncoding.putScalar(Array(pos, i), math.sin(angle))
        positionalEncoding.putScalar(Array(pos, i + 1), math.cos(angle))
      }
    }
    positionalEncoding
  }

  private def createPositionalEmbedding(tokens: Array[String]): INDArray = {
    // Extract input window (windowSize tokens)
    val inputWindow = tokens

    // Convert input tokens into embeddings
    val inputEmbeddings = tokenizeAndEmbed(inputWindow)

    // Add positional embeddings to word embeddings
    val positionAwareEmbedding = inputEmbeddings.add(positionalEmbeddings)

    positionAwareEmbedding
  }

  private def pad(tokens: Array[String]): Array[String] = {
    if (tokens.length < windowSize) {
      val padding = Array.fill(windowSize - tokens.length)("the")
      padding ++ tokens
    } else if (tokens.length > windowSize) {
      tokens.takeRight(windowSize)
    } else {
      tokens
    }
  }

  def generate: Action[AnyContent] = Action { implicit request =>
    val content = request.body
    val jsonObject: Option[JsValue] = content.asJson

    jsonObject match {
      case Some(json) =>
        (json \ "prompt").asOpt[String] match {
          case Some(prompt) =>
            modelTry match {
              case Success(model) =>
                // Initialize the sentence with the input prompt
                // Generate words until the target sentence length is reached
                var i = 0
                var res = ""
                while (i < sentenceLen) {
                  val currentSentence = pad(StrUtil.cleanLine(prompt).split(" "))
                  // Create positional embeddings for the current sentence
                  val testInput = createPositionalEmbedding(currentSentence)

                  // Get the output from the model
                  val output = model.output(testInput.reshape(1, windowSize, 100))
                  val nextWord = EmbeddingUtil.candidates(Nd4j.toFlattened(output).toDoubleVector, lookup)

                  // Append the generated word to the current sentence
                  res = s"$res $nextWord"
                  currentSentence :+ nextWord
                  i += 1
                }

                // Return the generated sentence as a response
                Ok(Json.obj("result" -> res))

              case Failure(exception) =>
                InternalServerError(Json.obj("error" -> s"Failed to load model: ${exception.getMessage}"))
            }
          case None =>
            BadRequest(Json.obj("error" -> "Missing 'prompt' in request body"))
        }
      case None =>
        BadRequest(Json.obj("error" -> "Invalid JSON in request body"))
    }
  }


}
