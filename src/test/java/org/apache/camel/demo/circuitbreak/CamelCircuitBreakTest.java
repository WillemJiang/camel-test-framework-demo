package org.apache.camel.demo.circuitbreak;;

import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Predicate;
import org.apache.camel.Processor;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.demo.MyCustomException;
import org.apache.camel.test.spring.CamelSpringTestSupport;
import org.junit.Test;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CamelCircuitBreakTest extends CamelSpringTestSupport {
    @EndpointInject(uri = "mock:result")
    protected     MockEndpoint result;
    @EndpointInject(uri = "mock:error")
    protected MockEndpoint error;
    @EndpointInject(uri = "mock:catcher")
    protected MockEndpoint catcher;


    @Override
    // setup
    protected AbstractApplicationContext createApplicationContext() {
        return new ClassPathXmlApplicationContext("META-INF/spring/camel-context-circuitbeaker.xml");
    }

    @Test
    public void testCircuitBreakIsOpen() throws Exception {
        result.reset();
        // Just to make sure the MyCustomerException is thrown
        result.whenAnyExchangeReceived(new Processor() {
            @Override
            public void process(Exchange exchange) throws Exception {
                exchange.setException(new MyCustomException());
            }
        });

        template.sendBody("direct:start", "Test message");
        Thread.sleep(100);
        template.sendBody("direct:start", "Test message");
        Thread.sleep(100);
        template.sendBody("direct:start", "Test message");

        error.expectedMessageCount(1);
        error.expectedMessagesMatches(new Predicate() {
            @Override public boolean matches(Exchange exchange) {
                String message = exchange.getIn().getBody(String.class);
                return message.contains("Test message");
            }
        });

        result.expectedMessageCount(1);
        // The circuritBreak is Open
        catcher.expectedMessageCount(2);

        assertMockEndpointsSatisfied();

    }


    @Test
    public void testCircuitBreakIsHalfOpen() throws Exception {
        result.reset();
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

        error.expectedMessageCount(2);
        error.expectedMessagesMatches(new Predicate() {
            @Override public boolean matches(Exchange exchange) {
                String message = exchange.getIn().getBody(String.class);
                return message.contains("Test message");
            }
        });

        result.expectedMessageCount(2);
        // The circuritBreak is Open
        catcher.expectedMessageCount(1);

        assertMockEndpointsSatisfied();

    }
}
