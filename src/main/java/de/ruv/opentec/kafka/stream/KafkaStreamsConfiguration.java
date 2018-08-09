package de.ruv.opentec.kafka.stream;

import de.ruv.opentec.kafka.model.Address;
import de.ruv.opentec.kafka.model.Name;
import de.ruv.opentec.kafka.model.Partner;
import de.ruv.opentec.kafka.model.PartnerCreated;
import de.ruv.opentec.kafka.repository.PartnerRepository;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import io.confluent.kafka.serializers.KafkaAvroDeserializerConfig;
import io.confluent.kafka.streams.serdes.avro.SpecificAvroSerde;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.requests.IsolationLevel;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.*;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

import java.util.Locale;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Configuration
public class KafkaStreamsConfiguration {

    @Value(value = "${kafka.schemaregistry.address}")
    private String registryAddress;

    @Value(value = "${kafka.bootstrap.address}")
    private String bootstrapAddress;

    @Value("${kafka.message.topic.created}")
    private String topic;

    @Autowired
    PartnerRepository partnerRepository;

    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
    public StreamsConfig kStreamsConfigs() {
        final Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG,
                "PartnerAggregationStream");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
                registryAddress);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, SpecificAvroSerde.class);
        props.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
        props.put(KafkaAvroDeserializerConfig.SPECIFIC_AVRO_READER_CONFIG, true);
        props.put(ConsumerConfig.ISOLATION_LEVEL_CONFIG, IsolationLevel.READ_COMMITTED.toString().toLowerCase(Locale.ROOT));
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return new StreamsConfig(props);
    }

    @Bean
    public KStream<Long, PartnerCreated> kStream(StreamsBuilder kStreamBuilder) {
        KStream<Long, PartnerCreated> stream = kStreamBuilder.stream(topic);

        KTable<Windowed<Long>, PartnerCreated> aggregated = stream
                .groupByKey()
                .windowedBy(TimeWindows.of(TimeUnit.SECONDS.toMillis(1)))
                .reduce(
                        (aggValue, newValue) -> {
                            if(aggValue.getAddress() == null && newValue.getAddress() != null) {
                                aggValue.setAddress(newValue.getAddress());
                            }
                            if(aggValue.getName() == null && newValue.getName() != null) {
                                aggValue.setName(newValue.getName());
                            }
                            savePartner(aggValue);
                            return aggValue;
                        }
                );

        aggregated
                .toStream()
                .map((windowedKey, partner) -> new KeyValue<>(windowedKey.key(), partner))
                .to("partnerAggregated");
        return stream;
    }

    private void savePartner(PartnerCreated partnerCreated) {
        Partner partner = new Partner();
        partner.setId(partnerCreated.getId());
        partner.setFirstname(partnerCreated.getName().getFirstname());
        partner.setLastname(partnerCreated.getName().getLastname());
        partner.setStreet(partnerCreated.getAddress().getStreet());
        partner.setCity(partnerCreated.getAddress().getCity());
        partnerRepository.save(partner);
    }

}