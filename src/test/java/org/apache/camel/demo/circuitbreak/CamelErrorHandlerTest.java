package org.apache.camel.demo.circuitbreak;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.demo.MyCustomException;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CamelErrorHandlerTest extends CamelSpringTestSupport {
    @EndpointInject(uri = "mock:result")
    protected     MockEndpoint result;
    @EndpointInject(uri = "mock:error")
    protected MockEndpoint error;


    @Override
    // setup
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context-errorhandler.xml");
    }


    @Test
    public void testErrorHandlerMessage() throws Exception {
        // Just to make sure the MyCustomerException is thrown
        result.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.setException(new MyCustomException());
            }
        });

        template.sendBody("direct:start", "Test message");
        Thread.sleep(1000);
        template.sendBody("direct:start", "Test message");
        Thread.sleep(1000);
        template.sendBody("direct:start", "Test message");

        error.expectedMessageCount(3);
        error.expectedMessagesMatches(new Predicate() {
            @Override public boolean matches(Exchange exchange) {
                String message = exchange.getIn().getBody(String.class);
                return message.contains("Test message");
            }
        });

        // The FatalFallbackErrorHandler always send the message twice here
        result.expectedMessageCount(6);

        assertMockEndpointsSatisfied();

    }
}
