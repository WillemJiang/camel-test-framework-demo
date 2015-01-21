package org.apache.camel.demo;

import java.util.concurrent.TimeUnit;
import org.apache.camel.CamelContext;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.NotifyBuilder;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.main.Main;

/**
 * A Camel Application
 */
public class MainApp {

    /**
     * A main() so we can easily run these routing rules in our IDE
     */
    public static void main(String... args) throws Exception {
        // Start with camel main wrapper
        starWithCamelContextAndNotifyBuilder();
        // Start with Default Camel Context
        // startWithCamelMainWrapper(args);
    }

    private static void startWithCamelContextAndSleep() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new MyRouteBuilder());
        // Create the template so we can talk to the camel route
        ProducerTemplate template = camelContext.createProducerTemplate();

        camelContext.start();
        // Sleep a while to keep the camel route running for about 6 seconds
        Thread.sleep(6000);
        camelContext.stop();

    }

    private static void starWithCamelContextAndNotifyBuilder() throws Exception {
        CamelContext camelContext = new DefaultCamelContext();
        camelContext.addRoutes(new MyRouteBuilder());

        NotifyBuilder notify = new NotifyBuilder(camelContext).whenDone(2).create();
        camelContext.start();
        boolean done = notify.matches(5, TimeUnit.SECONDS);
        System.out.println("The messages should be processed : " + done);
        camelContext.stop();

    }

    private static void startWithCamelMainWrapper(String[] args) throws Exception {
        Main main = new Main();
        main.enableHangupSupport();
        main.addRouteBuilder(new MyRouteBuilder());
        main.run(args);
    }

}

