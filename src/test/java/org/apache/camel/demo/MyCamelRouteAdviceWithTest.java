package org.apache.camel.demo;

import org.apache.camel.builder.RouteBuilder;

public class MyCamelRouteAdviceWithTest extends MyCamelRouteTest {

    protected void doPostSetup() throws Exception {
        super.doPostSetup();
        // advice the first route using the inlined route builder
        // it should be better if we could use the route id to look up the route
        context.getRouteDefinitions().get(0).adviceWith(context, new RouteBuilder() {
            @Override
            public void configure() throws Exception {
                // intercept sendToEndpoint
                interceptSendToEndpoint("file:target/messages/uk").to("mock:uk").skipSendToOriginalEndpoint();
                interceptSendToEndpoint("file:target/messages/others").to("mock:others").skipSendToOriginalEndpoint();
            }
        });
    }

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new MyRouteBuilder();
    }

}
