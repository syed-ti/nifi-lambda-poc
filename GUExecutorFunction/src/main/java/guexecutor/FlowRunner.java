package guexecutor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import org.apache.nifi.stateless.config.ParameterOverride;
import org.apache.nifi.stateless.config.PropertiesFileEngineConfigurationParser;
import org.apache.nifi.stateless.config.StatelessConfigurationException;
import org.apache.nifi.stateless.engine.StatelessEngineConfiguration;
import org.apache.nifi.stateless.flow.DataflowDefinition;
import org.apache.nifi.stateless.flow.StatelessDataflow;
import org.apache.nifi.stateless.flow.StatelessDataflowValidation;
import org.crac.Context;
import org.crac.Core;
import org.crac.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class FlowRunner implements Resource {

    private static final Logger logger = LoggerFactory.getLogger(FlowRunner.class);

    public FlowRunner() throws IOException, StatelessConfigurationException {
        try {
            // /tmp/lib is NAR directory
            String command = "mv ./lib/ /tmp/lib/";
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();

            // copies NARs and JARs to NAR directory
            command = "cp -r /opt/jars-and-nars/. /tmp/lib/";
            process = Runtime.getRuntime().exec(command);
            process.waitFor();

            // prints contents of NAR directory for debugging
            command = "ls -R /tmp/lib";
            process = Runtime.getRuntime().exec(command);

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(process.getInputStream()));

            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }

            int exitCode = process.waitFor();
            System.out.println("\nExited with error code : " + exitCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final PropertiesFileEngineConfigurationParser engineConfigParser = new PropertiesFileEngineConfigurationParser();

        File engineConfigFile = new File("/opt/stateless.properties");
        File flowDefinitionFile = new File("/opt/env-flow-config.properties");

        final StatelessEngineConfiguration engineConfiguration = engineConfigParser
                .parseEngineConfiguration(engineConfigFile);

        final StatelessDataflow dataflow = createDataflow(engineConfiguration,
                flowDefinitionFile);

        Core.getGlobalContext().register(this);
    }

    @Override
    public void beforeCheckpoint(Context<? extends Resource> context) {
        logger.info("beforeCheckpoint hook");
    }

    @Override
    public void afterRestore(Context<? extends Resource> context) {
        System.out.println("afterRestore hook");
    }

    public static StatelessDataflow createDataflow(final StatelessEngineConfiguration engineConfiguration,
            final File flowDefinitionFile)
            throws IOException, StatelessConfigurationException {
        final long initializeStart = System.currentTimeMillis();

        final CustomStatelessBootstrap bootstrap = CustomStatelessBootstrap
                .bootstrap(engineConfiguration);

        final DataflowDefinition dataflowDefinition = bootstrap.parseDataflowDefinition(flowDefinitionFile,
                new ArrayList<ParameterOverride>());

        final StatelessDataflow dataflow = bootstrap.createDataflow(dataflowDefinition);
        dataflow.initialize();

        final StatelessDataflowValidation validation = dataflow.performValidation();
        if (!validation.isValid()) {
            logger.error(validation.toString());
            throw new IllegalStateException("Dataflow is not valid");
        }

        final long initializeMillis = System.currentTimeMillis() - initializeStart;
        logger.info("Initialized Stateless NiFi in {} millis", initializeMillis);

        return dataflow;
    }
}
