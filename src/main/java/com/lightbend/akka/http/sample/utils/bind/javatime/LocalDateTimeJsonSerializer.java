package com.lightbend.akka.http.sample.utils.bind.javatime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDateTime;

import static java.time.format.DateTimeFormatter.ofPattern;

public class LocalDateTimeJsonSerializer extends JsonSerializer<LocalDateTime> {

    @Override
    public void serialize(final LocalDateTime value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        jgen.writeString(ofPattern("dd/MM/yyyy HH:mm").format(value));
    }
}
