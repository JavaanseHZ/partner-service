package de.ruv.opentec.kafka.stream;

import de.ruv.opentec.kafka.model.PartnerCreated;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDe;
import io.confluent.kafka.serializers.AbstractKafkaAvroSerDeConfig;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Printed;
import org.apache.kafka.streams.kstream.TimeWindows;
import org.apache.kafka.streams.processor.WallclockTimestampExtractor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafkaStreams;
import org.springframework.kafka.annotation.KafkaStreamsDefaultConfiguration;

import java.util.Properties;

@Configuration
public class KafkaStreamsConfiguration {

    @Value(value = "${kafka.schemaregistry.address}")
    private String registryAddress;

    @Value(value = "${kafka.bootstrap.address}")
    private String bootstrapAddress;

    @Value("${kafka.message.topic.created}")
    private String topic;

//    @Bean(name = KafkaStreamsDefaultConfiguration.DEFAULT_STREAMS_CONFIG_BEAN_NAME)
//    public Properties kStreamsConfigs() {
//        final Properties streamsConfiguration = new Properties();
//        streamsConfiguration.put(StreamsConfig.APPLICATION_ID_CONFIG,
//                "testStream");
//        streamsConfiguration.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
//        streamsConfiguration.put(AbstractKafkaAvroSerDeConfig.SCHEMA_REGISTRY_URL_CONFIG,
//                registryAddress);
//        streamsConfiguration.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.Long().getClass().getName());
//        streamsConfiguration.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, PartnerCreated.class);
//        streamsConfiguration.put(StreamsConfig.DEFAULT_TIMESTAMP_EXTRACTOR_CLASS_CONFIG, WallclockTimestampExtractor.class.getName());
//        return streamsConfiguration;
//    }
//
//    @Bean
//    public KStream<Long, PartnerCreated> kStream(StreamsBuilder kStreamBuilder) {
//        KStream<Long, PartnerCreated> stream = kStreamBuilder.stream(topic);
//        stream.groupByKey().windowedBy(TimeWindows.of(1000)).count()
////                .filter(e-> e)
////                reduce((String value1, String value2) -> value1 + value2,
////                        TimeWindows.of(1000),
////                		"windowStore")
//                .toStream();
////                .map((windowedId, value) -> new KeyValue<>(windowedId.key(), value))
////                .filter((i, s) -> s.length() > 40)
////                .to("streamingTopic2");
//
//        stream.print(Printed.toSysOut());
//
//        return stream;
//    }

}