package com.singtel.nsb.mobile.validator;

import static org.junit.Assert.assertTrue;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultExchange;
import org.apache.camel.test.spring.CamelSpringBootRunner;
import org.apache.commons.lang3.StringUtils;
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

import com.singtel.nsb.framework.exception.NsbException;
import com.singtel.nsb.framework.exception.NsbExceptionBuilder;
import com.singtel.nsb.framework.exception.base.NsbErrorCode;
import com.singtel.nsb.framework.exception.base.NsbErrorType;
import com.singtel.nsb.mobile.sync.config.CompletableFutureReplyingKafkaOperations;
import com.singtel.nsb.mobile.util.ErrorUtil;

@RunWith(CamelSpringBootRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class PatchRequestValidatorTest {

	private static final String BASE_REQUEST = "{\"requestId\": \"patch_20200310_local_010\",\"description\":\"description\",\"name\": \"mobileActivate\",\"serviceType\":\"serviceType\",\"serviceCharacteristic\": [{\"name\": \"profileId\",\"value\": \"HSS-Profile\"},{\"name\": \"odb\",\"value\": \"ABC\"},{\"name\": \"apnContext\",\"value\": \"1\"},{\"name\": \"apnDefaultContext\",\"value\": \"1\"},{\"name\": \"apnTrafficMappingContext\",\"value\": \"2:631,3:632\"}],\"state\":\"state\"}";
	static {
		System.setProperty("JAEGER_SERVICE_NAME", PatchRequestValidatorTest.class.getName());
	}

	@Autowired
	ProducerTemplate template;

	@Autowired
	CamelContext context;

	@Autowired
	Environment env;

	@Autowired
	private PatchRequestValidator patchRequestValidator;

	@EndpointInject(uri = "mock:patchRequestValidator")
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
	public void init() {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void given_Patch_Request_When_Its_Valid_Then_Process() throws Exception {
		template = context.createProducerTemplate();
		template.getCamelContext().addRoutes(new RouteBuilder() {
			@Override
			public void configure() throws Exception {
				from("direct:patchRequestValidator").process(patchRequestValidator).to("mock:patchRequestValidator");
			}
		});
		Exchange exchangeInput = new DefaultExchange(context);
		Message bodyIn = exchangeInput.getIn();
		bodyIn.setBody(BASE_REQUEST);
		resultEndpoint.setExpectedCount(1);
		template.send("direct:patchRequestValidator", exchangeInput);
		resultEndpoint.assertIsSatisfied();
	}

	@Test
	public void givenNsbException_Then_ConstructErrorResponse() throws Exception {
		template = context.createProducerTemplate();
		Exchange exchangeInput = new DefaultExchange(context);
		NsbException nsbException = new NsbExceptionBuilder("PATCH", NsbErrorType.BUSINESS, "uuid", "transactionId")
				.causeByNsbDefinedError(NsbErrorCode.INVALID_REQUEST_PAYLOAD, "errorMessage")
				.withHttpResponse(400, "errorMessage").buildException();
		exchangeInput.setProperty(Exchange.EXCEPTION_CAUGHT, nsbException);
		String error = ErrorUtil.constructError(exchangeInput);
		assertTrue(StringUtils.isNotEmpty(error));
	}

}
