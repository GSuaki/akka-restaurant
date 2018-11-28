package com.lightbend.akka.http.sample.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.lightbend.akka.http.sample.utils.Json;
import lombok.SneakyThrows;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.Serializer;

import java.util.Map;

public final class KafkaHelper {

  public static JsonValueSerializer jsSerializer = new JsonValueSerializer();
  public static JsonValueDeserializer jsDeserializer = new JsonValueDeserializer();

  public static class JsonValueSerializer implements Serializer<JsonNode> {

    @Override
    public void configure(final Map<String, ?> configs, boolean isKey) {

    }

    @Override
    @SneakyThrows
    public byte[] serialize(String topic, JsonNode data) {
      return data.toString().getBytes();
    }

    @Override
    public void close() {

    }
  }

  public static class JsonValueDeserializer implements Deserializer<JsonNode> {

    @Override
    public void configure(Map<String, ?> configs, boolean isKey) {

    }

    @Override
    @SneakyThrows
    public JsonNode deserialize(String topic, byte[] data) {
      return Json.parse(data);
    }

    @Override
    public void close() {

    }
  }
}
