package com.lightbend.akka.http.sample.utils.bind.javatime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.time.LocalDateTime;

import static com.lightbend.akka.http.sample.utils.DateUtils.getDateTimePattern;
import static java.time.format.DateTimeFormatter.ofPattern;

public class LocalDateTimeJsonDeserializer extends JsonDeserializer<LocalDateTime> {

  @Override
  public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

    LocalDateTime value = null;

    try {
      value = LocalDateTime.parse(jp.getText(), ofPattern(getDateTimePattern(jp.getText())));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return value;
  }
}
