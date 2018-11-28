package com.lightbend.akka.http.sample.utils.bind.javatime;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class LocalDateJsonSerializer extends JsonSerializer<LocalDate> {

    @Override
    public void serialize(final LocalDate value, final JsonGenerator jgen, final SerializerProvider provider) throws IOException {
        final DateTimeFormatter format = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        jgen.writeString(format.format(value));
    }
}
