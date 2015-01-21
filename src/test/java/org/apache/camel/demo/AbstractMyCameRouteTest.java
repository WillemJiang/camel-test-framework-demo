package org.apache.camel.demo;

import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;


public class AbstractMyCameRouteTest extends CamelTestSupport {
    protected MockEndpoint uk;
    protected MockEndpoint others;

    protected void doPostSetup() throws Exception {
        uk = context.getEndpoint("mock:uk", MockEndpoint.class);
        others = context.getEndpoint("mock:others", MockEndpoint.class);
    }

}
