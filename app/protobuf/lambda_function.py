import json
import boto3
import service_pb2.GenerateExternalRequest_pb2 as GenerateExternalRequest_pb2
import service_pb2.GenerateExternalResponse_pb2 as GenerateExternalResponse_pb2

bedrock_runtime = boto3.client(service_name="bedrock-runtime", region_name="us-east-2")

def lambda_handler(event, context):
    try:
        # Parse the API Gateway event body
        body = json.loads(event.get("body", "{}"))
        prompt = body.get("prompt", "")

        if not prompt:
            return {
                "statusCode": 400,
                "body": json.dumps({"error": "Invalid request. 'prompt' field is required."})
            }

        # Create a Protobuf request
        grpc_request = GenerateExternalRequest_pb2.GenerateExternalRequest()
        grpc_request.prompt = prompt

        # Serialize the request
        serialized_request = grpc_request.SerializeToString()

        # Simulate external service response (mock response for testing)
        # Normally, you would send `serialized_request` to an external service here
        grpc_response = GenerateExternalResponse_pb2.GenerateExternalResponse()
        grpc_response.result = f"Processed prompt: {prompt} (via Protobuf)"

        # Serialize Protobuf response (if needed for further processing)
        serialized_response = grpc_response.SerializeToString()

        # Prepare Bedrock invocation payload
        bedrock_payload = {
            "anthropic_version": "bedrock-2023-05-31",
            "max_tokens": 1000,
            "messages": [
                {
                    "role": "user",
                    "content": [
                        {
                            "type": "text",
                            "text": prompt
                        }
                    ]
                }
            ]
        }

        # Invoke Bedrock model
        bedrock_response = bedrock_runtime.invoke_model(
            modelId="arn:aws:bedrock:us-east-2:841162678620:inference-profile/us.anthropic.claude-3-5-sonnet-20240620-v1:0",
            contentType="application/json",
            accept="*/*",
            body=json.dumps(bedrock_payload),
        )

        # Parse Bedrock response
        bedrock_result = json.loads(bedrock_response.get("body").read())

        # Return combined result (Protobuf + Bedrock)
        return {
            "statusCode": 200,
            "body": json.dumps({
                "protobuf_result": grpc_response.result,
                "bedrock_result": bedrock_result
            })
        }

    except Exception as e:
        return {
            "statusCode": 500,
            "body": json.dumps({"error": str(e)})
        }
