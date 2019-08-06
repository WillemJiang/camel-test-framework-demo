package org.apache.camel.demo;

import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.builder.AdviceWithRouteBuilder;
import org.junit.Test;

public class MyCamelRouteReplaceFromWithTest extends MyCamelRouteAdviceWithTest {
    private static final String TEST_FIRST_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<person user=\"hiram\">\n" +
            "  <firstName>Hiram</firstName>\n" +
            "  <lastName>Chirino</lastName>\n" +
            "  <city>Tampa</city>\n" +
            "</person>";
    private static final String TEST_SECOND_MESSAGE = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<person user=\"willem\">\n" +
            "  <firstName>Willem</firstName>\n" +
            "  <lastName>Jiang</lastName>\n" +
            "  <city>Beijing</city>\n" +
            "</person>";

    protected void doPostSetup() throws Exception {
        super.doPostSetup();
        
        // If we don't want to feed the route with some other message you can redefine the route with AdviceWith
        context.getRouteDefinitions().get(0).adviceWith(context, new AdviceWithRouteBuilder() {
            @Override
            public void configure() throws Exception {
                // Here we replace the from endpoint to do more work
                replaceFromWith("direct:start");
            }
        });
    }

    @Test
    public void testCRBMessage() throws Exception {

        uk.expectedMessageCount(0);
        others.expectedMessageCount(2);
        others.expectedMessagesMatches((Exchange exchange)->{
                String message = exchange.getIn().getBody(String.class);
                return !message.contains("London");
        });
        
        // sending the message to the direct:start
        template.sendBody("direct:start", TEST_FIRST_MESSAGE);
        template.sendBody("direct:start", TEST_SECOND_MESSAGE);

        assertMockEndpointsSatisfied();

    }
}
