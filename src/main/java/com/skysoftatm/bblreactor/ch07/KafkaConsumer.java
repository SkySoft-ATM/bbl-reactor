package com.skysoftatm.bblreactor.ch07;

import com.google.protobuf.InvalidProtocolBufferException;
import com.skysoftatm.bblreactor.protobuf.types.Tweet;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.ByteArrayDeserializer;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.KafkaReceiver;
import reactor.kafka.receiver.ReceiverOffset;
import reactor.kafka.receiver.ReceiverOptions;
import reactor.kafka.receiver.ReceiverRecord;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.skysoftatm.bblreactor.ch07.KafkaConfig.BOOTSTRAP_SERVERS;
import static com.skysoftatm.bblreactor.ch07.KafkaConfig.TOPIC;

public class KafkaConsumer {
    private static final Logger log = LoggerFactory.getLogger(KafkaConsumer.class.getName());

    private final ReceiverOptions<Long, byte[]> receiverOptions;

    private KafkaConsumer(String bootstrapServers) {

        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.CLIENT_ID_CONFIG, "sample-consumer");
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "sample-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, LongDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ByteArrayDeserializer.class);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        receiverOptions = ReceiverOptions.create(props);
    }

    public static void main(String[] args) {
        KafkaConsumer consumer = new KafkaConsumer(BOOTSTRAP_SERVERS);
        Flux<ReceiverRecord<Long, byte[]>> msgFlux = consumer.consumeMessages(TOPIC);
        msgFlux
                .map(KafkaConsumer::toTweet)
                .doOnNext(KafkaConsumer::consumeTweet)
                .blockLast();
    }

    private static Pair<ReceiverOffset, Tweet> toTweet(ReceiverRecord<Long, byte[]> record) {
        try {
            return Pair.of(record.receiverOffset(), Tweet.parseFrom(record.value()));
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException(e);
        }
    }

    private static void consumeTweet(Pair<ReceiverOffset, Tweet> pair) {
        Tweet tweet = pair.getRight();
        long timeDiff = System.currentTimeMillis() - tweet.getTimestamp();
        System.out.println(tweet.getPayload() + " Processed in " + timeDiff + " ms");
        pair.getLeft().acknowledge();
    }

    private Flux<ReceiverRecord<Long, byte[]>> consumeMessages(String topic) {

        ReceiverOptions<Long, byte[]> options = receiverOptions.subscription(Collections.singleton(topic))
                .addAssignListener(partitions -> log.debug("onPartitionsAssigned {}", partitions))
                .addRevokeListener(partitions -> log.debug("onPartitionsRevoked {}", partitions));
        return KafkaReceiver.create(options).receive();
    }
}
