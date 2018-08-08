package de.ruv.opentec.kafka.producer;

import de.ruv.opentec.kafka.model.PartnerCreated;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class PartnerCreatedAvroKafkaProducerConfig {

    @Value(value = "${kafka.bootstrap.address}")
    private String bootstrapAddress;

    @Value(value = "${kafka.schemaregistry.address}")
    private String registryAddress;

    @Bean
    public ProducerFactory<Long, PartnerCreated> partnerCreatedProducerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class);
        props.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                KafkaAvroSerializer.class);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG, registryAddress);
        DefaultKafkaProducerFactory<Long, PartnerCreated> producerFactory = new DefaultKafkaProducerFactory<>(props);
        producerFactory.setTransactionIdPrefix("partnerCreated-");
        return producerFactory;
    }


    @Bean
    public KafkaTemplate<Long, PartnerCreated> kafkaTemplatePartnerCreated() {
        return new KafkaTemplate<>(partnerCreatedProducerFactory());
    }
}