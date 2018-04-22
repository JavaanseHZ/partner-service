package de.ruv.opentec.kafka.producer;

import de.ruv.opentec.kafka.model.Partner;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.config.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.KafkaEmbedded;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.springframework.kafka.test.assertj.KafkaConditions.key;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext
@EmbeddedKafka(topics = { "${kafka.message.topic}" }, brokerProperties = { "listeners=PLAINTEXT://${kafka.bootstrap.address}"} )
public class PartnerSavedKafkaProducerTest {

    @Value("${kafka.message.topic}")
    private String topic;

    @Autowired
    private PartnerSavedKafkaProducer partnerSavedKafkaProducer;

    @Autowired
    private KafkaEmbedded embeddedKafka;

    private KafkaMessageListenerContainer<Long, Partner> container;

    private BlockingQueue<ConsumerRecord<Long, Partner>> records;

    @Before
    public void setUp() throws Exception {
        records = new LinkedBlockingQueue<>();

        container = new KafkaMessageListenerContainer<>(
                consumerFactory(),
                new ContainerProperties(topic));

        container.setupMessageListener((MessageListener<Long, Partner>) record -> {
            records.add(record);
        });
        container.start();

        ContainerTestUtils.waitForAssignment(container, embeddedKafka.getPartitionsPerTopic());
    }

    @After
    public void tearDown() {
        container.stop();
    }

    @Test
    public void canSendPartner() throws Exception {
        Partner maurizio =
                new Partner();
        maurizio.setId(1);
        maurizio.setVorname("Maurizio");
        maurizio.setNachname("Gaudino");
        Long key = 1L;

        partnerSavedKafkaProducer.sendEvent(key, maurizio);

        ConsumerRecord<Long, Partner> received = records.poll(10, TimeUnit.SECONDS);
        assertThat(received).has(key(key));
        assertThat(received.value().getVorname(), is(maurizio.getVorname()));
    }

    private ConsumerFactory<Long, Partner> consumerFactory() {
        return new DefaultKafkaConsumerFactory<>(kafkaConsumerConfig(), new LongDeserializer(), new JsonDeserializer<Partner>(Partner.class));
    }

    private Map<String, Object> kafkaConsumerConfig() {
        Map<String, Object> props = KafkaTestUtils.consumerProps("test", "false", embeddedKafka);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return props;
    }

}
