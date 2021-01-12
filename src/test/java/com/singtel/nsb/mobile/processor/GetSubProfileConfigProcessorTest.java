package com.singtel.nsb.mobile.processor;

import static com.singtel.nsb.mobile.constants.HeaderConstants.EXTERNAL_TRANSACTION_ID;
import static com.singtel.nsb.mobile.constants.HeaderConstants.ID;
import static com.singtel.nsb.mobile.constants.HeaderConstants.NAME;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.EndpointInject;
import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class GetSubProfileConfigProcessorTest extends CamelTestSupport {
	
	@InjectMocks
	@Spy
	private GetSubProfileConfigProcessor getSubProfileConfigProcessor;

	@EndpointInject(uri = "mock:output")
	protected MockEndpoint resultEndpoint;

	@Produce(uri = "direct:producer")
	protected ProducerTemplate template;

	@Override
	protected RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:producer").process(getSubProfileConfigProcessor).to("mock:output");
			}
		};
	}

	@Test
	public void test() throws InterruptedException {
		resultEndpoint.setExpectedCount(1);
		Map<String, Object> headers = new HashMap<>();
		headers.put(ID, "12345");
		headers.put(NAME, "mobileActivate");
		headers.put(EXTERNAL_TRANSACTION_ID, "465_0");
		template.sendBodyAndHeaders("direct:producer", "", headers);
		resultEndpoint.assertIsSatisfied();
	}

}
