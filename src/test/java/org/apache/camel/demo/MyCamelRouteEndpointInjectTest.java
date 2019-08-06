package org.apache.camel.demo;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;

public class MyCamelRouteEndpointInjectTest extends CamelTestSupport {
    @EndpointInject(uri = "mock:uk")
    protected MockEndpoint uk;
    @EndpointInject(uri = "mock:others")
    protected MockEndpoint others;

    protected RouteBuilder createRouteBuilder() throws Exception {
        return new MyTestRouteBuilder();
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

    static class MyTestRouteBuilder extends MyRouteBuilder {
        public void configure() {
            // We can leverage the sendToInterceptor to setup the mock endpoint for verification
            interceptSendToEndpoint("file:target/messages/uk").to("mock:uk").skipSendToOriginalEndpoint();
            interceptSendToEndpoint("file:target/messages/others").to("mock:others").skipSendToOriginalEndpoint();
            super.configure();
        }
    }

}
