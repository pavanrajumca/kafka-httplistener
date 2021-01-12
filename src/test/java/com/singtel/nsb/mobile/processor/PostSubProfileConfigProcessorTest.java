package com.singtel.nsb.mobile.processor;

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
public class PostSubProfileConfigProcessorTest extends CamelTestSupport {
	
	@InjectMocks
	@Spy
	private PostSubProfileConfigProcessor postSubProfileConfigProcessor;

	@EndpointInject(uri = "mock:output")
	protected MockEndpoint resultEndpoint;

	@Produce(uri = "direct:producer")
	protected ProducerTemplate template;

	@Override
	protected RouteBuilder createRouteBuilder() {
		return new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:producer").process(postSubProfileConfigProcessor).to("mock:output");
			}
		};
	}

	@Test
	public void given_Post_Request_Then_Process() throws InterruptedException {
		String request = "{\"reqId\": \"00147952_500828\",\"name\": \"mobileActivate\",\"serviceCharacteristic\": [{\"name\": \"apnContext\",\"value\": \"1,631,644\"},{\"name\": \"imsi\",\"value\": \"12345\"},{\"name\": \"apnDefaultContext\",\"value\": \"631\"},{\"name\": \"apnTrafficMappingContext\",\"value\": \"2:631,3:632\"}]}";

		resultEndpoint.setExpectedCount(1);
		Map<String, Object> headers = new HashMap<>();
		template.sendBodyAndHeaders("direct:producer", request, headers);
		resultEndpoint.assertIsSatisfied();
	}

}
