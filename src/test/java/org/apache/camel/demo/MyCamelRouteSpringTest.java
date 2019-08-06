package org.apache.camel.demo;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MyCamelRouteSpringTest extends CamelSpringTestSupport {
    @EndpointInject(uri = "mock:uk")
    protected MockEndpoint uk;
    @EndpointInject(uri = "mock:others")
    protected MockEndpoint others;

    @Override
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context.xml");
    }

    @Override
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

    @Test
    public void testCRBMessage() throws Exception {

        uk.expectedMessageCount(1);
        uk.expectedMessagesMatches((Exchange exchange) -> {
                String message = exchange.getIn().getBody(String.class);
                return message.contains("London");
        });

        others.expectedMessageCount(1);
        others.expectedMessagesMatches((Exchange exchange) -> {
                String message = exchange.getIn().getBody(String.class);
                return !message.contains("London");
        });

        assertMockEndpointsSatisfied();

    }



}
