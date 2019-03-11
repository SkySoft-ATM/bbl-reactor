package com.skysoftatm.bblreactor.ch07;

import com.skysoftatm.bblreactor.protobuf.types.Tweet;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.LongSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.kafka.sender.KafkaSender;
import reactor.kafka.sender.SenderOptions;
import reactor.kafka.sender.SenderRecord;
import reactor.kafka.sender.SenderResult;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static com.skysoftatm.bblreactor.ch07.KafkaConfig.BOOTSTRAP_SERVERS;
import static com.skysoftatm.bblreactor.ch07.KafkaConfig.TOPIC;

class KafkaProducer {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaProducer.class.getName());

    private final KafkaSender<Long, byte[]> sender;

    KafkaProducer(String bootstrapServers) {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.CLIENT_ID_CONFIG, "sample-producer");
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);
        SenderOptions<Long, byte[]> senderOptions = SenderOptions.create(props);

        sender = KafkaSender.create(senderOptions);

    }

    public static void main(String[] args) {
        KafkaProducer producer = new KafkaProducer(BOOTSTRAP_SERVERS);
        Flux<Long> source = Flux.interval(Duration.ofMillis(10));
        Flux<SenderResult<Long>> resultFlux = producer.sendMessages(TOPIC, source);
        resultFlux
                .doOnError(e -> LOGGER.error("Send failed", e))
                .doOnNext(KafkaProducer::printSenderResult)
                .doOnComplete(producer::close)
                .blockLast();
    }

    private static void printSenderResult(SenderResult<Long> r) {
        RecordMetadata metadata = r.recordMetadata();
        System.out.printf("Message %d sent successfully, topic-partition=%s-%d offset=%d timestamp=%d\n",
                r.correlationMetadata(),
                metadata.topic(),
                metadata.partition(),
                metadata.offset(),
                metadata.timestamp());
    }

    private Flux<SenderResult<Long>> sendMessages(String topic, Flux<Long> source) {
        return sender.<Long>send(source
                .map(i -> SenderRecord.create(new ProducerRecord<>(topic, i, buildTweet(i)), i)));

    }

    private byte[] buildTweet(Long i) {
        return Tweet.newBuilder().setTimestamp(System.currentTimeMillis()).setPayload("Message_" + i).build().toByteArray();
    }

    public void close() {
        sender.close();
    }
}
