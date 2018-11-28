package com.lightbend.akka.http.sample.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;
import io.vavr.jackson.datatype.VavrModule;
import lombok.SneakyThrows;

public final class Json {

  private static final ObjectMapper _mapper = new ObjectMapper()
      .configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false)
      .registerModule(new VavrModule())
      .registerModule(new Jdk8Module())
      .registerModule(new JavaTimeModule())
      .registerModule(new ParameterNamesModule())
      .setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

  public static final ObjectMapper mapper = _mapper.copy();

  @SneakyThrows
  public static JsonNode parse(final byte[] bts) {
    return _mapper.readValue(bts, JsonNode.class);
  }

  @SneakyThrows
  public static byte[] writeValueAsBytes(final Object node) {
    return _mapper.writeValueAsBytes(node);
  }
}
