## Requirements
* AWS CLI
* AWS SAM
* Maven

## Deployment Instructions
1. Clone the repo
2. Build the function
    ```
    sam build
    ```
3. Deploy the infrastructure
    ```
    sam deploy --stack-name grouped-unit-executor --guided
    ```

## Testing

```bash
curl --location --request POST $(aws cloudformation describe-stacks --stack-name grouped-unit-executor --query "Stacks[0].Outputs[?OutputKey=='GUExecutorEndpoint'].OutputValue" --output text)'/test' 
```
