# Deploying a Conversational Agent on the Cloud and Conversing with Ollama

Name - Gautham Satyanarayana <br />
Email - gsaty@uic.edu <br />
UIN - 659368048

## Introduction
As part of the CS441 course, we build deploy the trained model from HW2 as a RESTful api 
using the Play framework. Along with deploying the trained model on an api endpoint, we use 
Amazon Bedrock and AWS Lambda to expose a foundational LLM base model over AWS API Gateway.
Additionally, an api endpoint is exposed to make the AWS bedrock model converse with a local Ollama
model and record the conversation.

Video Link: https://youtu.be/BzAkQEaV7lU

## Frameworks
1. Scala 2.13
2. sbt 1.10
3. REST - Play
4. Deeplearning4j

## Configuration
1. AWS Bedrock Model - Anthropic Claude 3.5 Sonnet
2. Local Ollama server - Ollama 3.2
3. Conversation Reply Count - 5 messages
4. Local Model Output sentence length - 10 words

## Data Flow and Logic
<img src="images/diagram.png" alt="diagram" width="80%"></img>
## AWS Deployment
The deployed API Gateway connected to Lambda that is invoking a bedrock runtime is accessible at - 
```angular2html
https://5lz5bv3g1h.execute-api.us-east-2.amazonaws.com/prod/hw3
```
you can run the following curl command to test it
```angular2html
curl -X POST https://5lz5bv3g1h.execute-api.us-east-2.amazonaws.com/prod/hw3 \
    -H "Content-Type: application/json" \
    -d '{"prompt": "<your_prompt_here>"}'
```
and the lambda function can be found in `lambda_function.py`
## Test Suite
Test cases can be found in the `test` folder, to run tests, run
```angular2html
sbt test
```
## Results
You can generate the results of the conversation by running 
```
sbt "runMain agent.ConversationalAgent '<your-prompt-here>'"
```
This should record the conversation and write it to `results/conversation.txt`, a sample conversation
with the prompt "what do you think about the soviet union" has been recorded.

## Usage
Clone this repository, and
1. Make sure Ollama server is running
```angular2html
ollama serve
```
2. Open a new terminal, compile protobuf classes and Install dependencies
```angular2html
sbt clean compile update
```
3. Run the Play application
```angular2html
sbt run
```
4. Import the postman collection found in `hw3.postman_collection.json` and test out the endpoints in postman.
```angular2html
GET  /health            Health check endpoint
POST /modelLocal        Call the local trained model (only outputs one word)
POST /modelExternal     Call the AWS bedrock model
```
5. To run the conversation agent,
```
sbt "runMain agent.ConversationalAgent '<your-prompt-here>'"
```
This takes 5-6 min to run and generate the results in `results/conversation.txt`