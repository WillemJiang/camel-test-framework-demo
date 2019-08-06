package org.apache.camel.demo;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.junit.Test;

public class MyCamelRouteTest extends AbstractMyCameRouteTest {

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
