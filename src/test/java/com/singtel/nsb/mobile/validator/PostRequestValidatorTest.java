package com.singtel.nsb.mobile.validator;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.annotation.DirtiesContext;

import com.singtel.nsb.mobile.sync.config.CompletableFutureReplyingKafkaOperations;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PostRequestValidatorTest {

	private static final String REQUEST_CHANNEL = "direct:postRequestValidator";
	private static final String BASE_REQUEST = "{\"requestId\": \"00147952_500828\",\"name\": \"mobileActivate\",\"serviceCharacteristic\": [{\"name\": \"imsi\",\"value\": \"525016143034299\"},{\"name\": \"profileId\",\"value\": \"HSS-EsmDefaultAutomaticProfile\"},{\"name\": \"userType\",\"value\": \"epcEMBB\"},{\"name\": \"apnContext\",\"value\": \"644\"},{\"name\": \"apnDefaultContext\",\"value\": \"644\"},{\"name\": \"maxUplinkIpFlow\",\"value\": \"100000000\"},{\"name\": \"maxDownlinkIpFlow\",\"value\": \"500000000\"}]}";
	static {
		System.setProperty("JAEGER_SERVICE_NAME", PostRequestValidatorTest.class.getName());
	}

	@Autowired
	ProducerTemplate template;

	@Autowired
	CamelContext context;

	@Autowired
	Environment env;

	@Autowired
	private PostRequestValidator postRequestValidator;

	@EndpointInject(uri = "mock:postRequestValidator")
	protected MockEndpoint resultEndpoint;

	@Autowired
	CompletableFutureReplyingKafkaOperations<String, String, String> syncKafkaTemplate;
	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	@ClassRule
	public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true,
			"NormalHttpSubProfile5gActTransformTopic", "SvcActConfigReplyTopic");

	@BeforeClass
	public static void setUpBeforeClass() {
		System.setProperty("kafka.broker", embeddedKafkaRule.getEmbeddedKafka().getBrokersAsString());
	}

	@Before
	public void setUp() throws Exception {
		for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
				.getListenerContainers()) {
			ContainerTestUtils.waitForAssignment(messageListenerContainer,
					embeddedKafkaRule.getEmbeddedKafka().getPartitionsPerTopic());
		}
	}

	@Before
	public void init() throws Exception {
		MockitoAnnotations.initMocks(this);
		template.getCamelContext().addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from(REQUEST_CHANNEL).process(postRequestValidator).to("mock:postRequestValidator");
			}
		});
	}

	@Test
	public void given_Post_Request_When_Its_Valid_Then_Process() throws InterruptedException {
		template = context.createProducerTemplate();
		Exchange exchangeInput = new DefaultExchange(context);
		Message bodyIn = exchangeInput.getIn();
		bodyIn.setBody(BASE_REQUEST);
		resultEndpoint.setExpectedCount(1);
		template.send(REQUEST_CHANNEL, exchangeInput);
		resultEndpoint.assertIsSatisfied();
	}

}
