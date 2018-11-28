package com.lightbend.akka.http.sample.utils.bind.javatime;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.lightbend.akka.http.sample.utils.DateUtils.getDateTimePattern;

public class LocalDateJsonDeserializer extends JsonDeserializer<LocalDate> {

  @Override
  public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt) {

    LocalDate value = null;

    try {
      value = LocalDate.parse(jp.getText(), DateTimeFormatter.ofPattern(getDateTimePattern(jp.getText())));
    } catch (Exception e) {
      e.printStackTrace();
    }

    return value;
  }

}
