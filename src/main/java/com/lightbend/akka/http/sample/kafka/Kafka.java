package com.lightbend.akka.http.sample.kafka;

import akka.Done;
import akka.kafka.javadsl.Consumer;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.Source;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.util.concurrent.CompletionStage;

public interface Kafka {
  Sink<ProducerRecord<String, JsonNode>, CompletionStage<Done>> sink();

  Source<ConsumerRecord<String, JsonNode>, Consumer.Control> source(String topic);
}
