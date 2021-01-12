package com.singtel.nsb.mobile;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.test.context.junit4.SpringRunner;

import com.singtel.nsb.mobile.sync.config.CompletableFutureReplyingKafkaOperations;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NsbHttpListenerApplicationTest {

	static {
		System.setProperty("JAEGER_SERVICE_NAME", NsbHttpListenerApplicationTest.class.getName());
	}

	@Autowired
	CompletableFutureReplyingKafkaOperations<String, String, String> syncKafkaTemplate;
	@Autowired
	private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;
	@ClassRule
	public static EmbeddedKafkaRule embeddedKafkaRule = new EmbeddedKafkaRule(1, true, "NormalHttpSubProfile5gActTransformTopic", "SvcActConfigReplyTopic");

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
	
	@Test
	public void testMain() {
		NsbHttpListenerApplication.main(new String[] {});
		assertTrue(true);
	}

}
