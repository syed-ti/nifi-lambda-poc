package guexecutor;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.nifi.stateless.config.StatelessConfigurationException;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.amazonaws.services.lambda.runtime.RequestHandler;

public class LambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {
    private static Logger logger = LoggerFactory.getLogger(LambdaHandler.class);

    static {
        try {
            FlowRunner runner = new FlowRunner();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (StatelessConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public APIGatewayProxyResponseEvent handleRequest(final APIGatewayProxyRequestEvent input, final Context context) {
        // try {
        // String command = "ls -lR";
        // Process process = Runtime.getRuntime().exec(command);

        // BufferedReader reader = new BufferedReader(
        // new InputStreamReader(process.getInputStream()));

        // String line;
        // while ((line = reader.readLine()) != null) {
        // System.out.println(line);
        // }

        // int exitCode = process.waitFor();
        // System.out.println("\nExited with error code : " + exitCode);
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        APIGatewayProxyResponseEvent response = new APIGatewayProxyResponseEvent();
        response.setBody("Hello world");
        return response;
    }
}
