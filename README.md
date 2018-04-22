docker run -d \
    --net=host \
    --name=zookeeper \
    -e ZOOKEEPER_CLIENT_PORT=32181 \
    -e ZOOKEEPER_TICK_TIME=2000 \
    confluentinc/cp-zookeeper:4.1.0

docker run -d \
    --net=host \
    --name=kafka \
    -e KAFKA_ZOOKEEPER_CONNECT=localhost:32181 \
    -e KAFKA_ADVERTISED_LISTENERS=PLAINTEXT://localhost:29092 \
    -e KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR=1 \
    confluentinc/cp-kafka:4.1.0

docker run \
  --net=host \
  --rm confluentinc/cp-kafka:4.1.0 \
  kafka-topics --create --topic foo --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181

docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:4.1.0 \
  kafka-topics --describe --topic foo --zookeeper localhost:32181

docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:4.1.0 \
  bash -c "seq 42 | kafka-console-producer --request-required-acks 1 --broker-list localhost:29092 --topic foo && echo 'Produced 42 messages.'"

docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:4.1.0 \
  kafka-console-consumer --bootstrap-server localhost:29092 --topic foo --from-beginning --max-messages 42

docker run -d \
  --net=host \
  --name=schema-registry \
  -e SCHEMA_REGISTRY_KAFKASTORE_CONNECTION_URL=localhost:32181 \
  -e SCHEMA_REGISTRY_HOST_NAME=localhost \
  -e SCHEMA_REGISTRY_LISTENERS=http://localhost:8081 \
  confluentinc/cp-schema-registry:4.1.0

docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:4.1.0 \
  kafka-topics --create --topic quickstart-avro-offsets --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181

docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:4.1.0 \
  kafka-topics --create --topic quickstart-avro-config --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181

docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:4.1.0 \
  kafka-topics --create --topic quickstart-avro-status --partitions 1 --replication-factor 1 --if-not-exists --zookeeper localhost:32181

docker run -d \
  --name=kafka-connect-avro \
  --net=host \
  -e CONNECT_BOOTSTRAP_SERVERS=localhost:29092 \
  -e CONNECT_REST_PORT=28083 \
  -e CONNECT_GROUP_ID="quickstart-avro" \
  -e CONNECT_CONFIG_STORAGE_TOPIC="quickstart-avro-config" \
  -e CONNECT_OFFSET_STORAGE_TOPIC="quickstart-avro-offsets" \
  -e CONNECT_STATUS_STORAGE_TOPIC="quickstart-avro-status" \
  -e CONNECT_CONFIG_STORAGE_REPLICATION_FACTOR=1 \
  -e CONNECT_OFFSET_STORAGE_REPLICATION_FACTOR=1 \
  -e CONNECT_STATUS_STORAGE_REPLICATION_FACTOR=1 \
  -e CONNECT_KEY_CONVERTER="io.confluent.connect.avro.AvroConverter" \
  -e CONNECT_VALUE_CONVERTER="io.confluent.connect.avro.AvroConverter" \
  -e CONNECT_KEY_CONVERTER_SCHEMA_REGISTRY_URL="http://localhost:8081" \
  -e CONNECT_VALUE_CONVERTER_SCHEMA_REGISTRY_URL="http://localhost:8081" \
  -e CONNECT_INTERNAL_KEY_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_INTERNAL_VALUE_CONVERTER="org.apache.kafka.connect.json.JsonConverter" \
  -e CONNECT_REST_ADVERTISED_HOST_NAME="localhost" \
  -e CONNECT_LOG4J_ROOT_LOGLEVEL=DEBUG \
  -e CONNECT_LOG4J_LOGGERS=org.reflections=ERROR \
  -e CONNECT_PLUGIN_PATH=/usr/share/java,/etc/kafka-connect/jars \
  -v /tmp/quickstart/file:/tmp/quickstart \
  -v /tmp/quickstart/jars:/etc/kafka-connect/jars \
  confluentinc/cp-kafka-connect:4.1.0

docker run -d \
  --name=quickstart-mysql \
  --net=host \
  -e MYSQL_ROOT_PASSWORD=confluent \
  -e MYSQL_USER=confluent \
  -e MYSQL_PASSWORD=confluent \
  -e MYSQL_DATABASE=connect_test \
  mysql


docker exec -it quickstart-mysql bash

mysql -u confluent -pconfluent

CREATE DATABASE IF NOT EXISTS connect_test;
USE connect_test;

DROP TABLE IF EXISTS test;


CREATE TABLE IF NOT EXISTS test (
  id serial NOT NULL PRIMARY KEY,
  sparte varchar(100),
  betrag double,
  partnerid int,
  vorname varchar(100),
  nachname varchar (100),
  modified timestamp default CURRENT_TIMESTAMP NOT NULL,
  INDEX `modified_index` (`modified`)
);

INSERT INTO test (sparte, betrag, partnerid, vorname, nachname) VALUES ('Firmen', 120.2, 4, 'Harry', 'Hirsch');
INSERT INTO test (sparte, betrag, partnerid, vorname, nachname) VALUES ('Kranken', 433.14, 5, 'Marko', 'Marin');

exit;

export CONNECT_HOST=localhost

curl -X POST \
  -H "Content-Type: application/json" \
  --data '{ "name": "quickstart-jdbc-source", "config": { "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector", "tasks.max": 1, "connection.url": "jdbc:mysql://127.0.0.1:3306/connect_test?user=root&password=confluent", "mode": "incrementing", "incrementing.column.name": "id", "timestamp.column.name": "modified", "topic.prefix": "quickstart-jdbc-", "poll.interval.ms": 1000 } }' \
  http://$CONNECT_HOST:28083/connectors

curl -s -X GET http://$CONNECT_HOST:28083/connectors/quickstart-jdbc-source/status

docker run \
  --net=host \
  --rm \
  confluentinc/cp-kafka:4.1.0 \
  kafka-topics --list --zookeeper localhost:32181

docker run -d \
  --net=host \
  --name=kafka-rest \
  -e KAFKA_REST_ZOOKEEPER_CONNECT=localhost:32181 \
  -e KAFKA_REST_LISTENERS=http://localhost:8082 \
  -e KAFKA_REST_SCHEMA_REGISTRY_URL=http://localhost:8081 \
  -e KAFKA_REST_HOST_NAME=kafka-rest \
  confluentinc/cp-kafka-rest:4.1.0

docker run -d \
  --net=host \
  --name=control-center \
  --ulimit nofile=16384:16384 \
  -e CONTROL_CENTER_ZOOKEEPER_CONNECT=localhost:32181 \
  -e CONTROL_CENTER_BOOTSTRAP_SERVERS=localhost:29092 \
  -e CONTROL_CENTER_REPLICATION_FACTOR=1 \
  -e CONTROL_CENTER_CONNECT_CLUSTER=http://localhost:28082 \
  -v /mnt/control-center/data:/var/lib/confluent-control-center \
  confluentinc/cp-enterprise-control-center:4.1.0

docker run --net=host \
  	   --name=topics-ui \
           -e "KAFKA_REST_PROXY_URL=http://kafka-rest:8082" \
	   -e "PROXY=true" \
           landoop/kafka-topics-ui

https://github.com/Landoop/fast-data-dev

docker run --rm --net=host -e ADV_HOST=<IP> landoop/fast-data-dev