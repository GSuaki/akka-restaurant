package com.lightbend.akka.http.sample.kafka;

import akka.Done;
import akka.actor.ActorSystem;
import akka.kafka.ConsumerSettings;
import akka.kafka.ProducerSettings;
import akka.kafka.Subscriptions;
import akka.kafka.javadsl.Consumer;
import akka.kafka.javadsl.Producer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.UUID;
import java.util.concurrent.CompletionStage;

@Singleton
@RequiredArgsConstructor(onConstructor = @__(@Inject))
public class KafkaImpl implements Kafka {

  private final ActorSystem system;
  private final String groupId = UUID.randomUUID().toString();

  private ProducerSettings<String, JsonNode> producerSettings() {
    return ProducerSettings
        .create(system, new StringSerializer(), KafkaHelper.jsSerializer)
        .withBootstrapServers("localhost:9092");
  }

  private ConsumerSettings<String, JsonNode> consumerSettings() {
    return ConsumerSettings
        .create(system, new StringDeserializer(), KafkaHelper.jsDeserializer)
        .withBootstrapServers("localhost:9092")
        .withGroupId(groupId);
  }

  @Override
  public Sink<ProducerRecord<String, JsonNode>, CompletionStage<Done>> sink() {
    return Producer.plainSink(producerSettings());
  }

  @Override
  public Source<ConsumerRecord<String, JsonNode>, Consumer.Control> source(String topic) {
    return Consumer.plainSource(consumerSettings(), Subscriptions.topics(topic));
  }
}
