import boto3
import json

bedrock_runtime = boto3.client(service_name='bedrock-runtime', region_name='us-east-2')

def lambda_handler(event, context):
    body = json.dumps({
        "anthropic_version": "bedrock-2023-05-31",
        "max_tokens": 1000,
        "messages": [
            {
                "role": "user",
                "content": [
                    {
                        "type": "text",
                        "text": event["prompt"]
                    }
                ]
            }
        ]
    })

    kwargs = {
        "modelId": "arn:aws:bedrock:us-east-2:841162678620:inference-profile/us.anthropic.claude-3-5-sonnet-20240620-v1:0",
        "contentType": "application/json",
        "accept": "*/*",
        "body": body
    }

    resp = bedrock_runtime.invoke_model(**kwargs)
    resp_json = json.loads(resp.get("body").read())

    return {
        "statusCode": 200,
        "body": json.dumps(resp_json)
    }
