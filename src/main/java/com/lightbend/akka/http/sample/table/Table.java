package com.lightbend.akka.http.sample.table;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lightbend.akka.http.sample.utils.bind.javatime.LocalDateTimeJsonSerializer;
import lombok.Builder;
import lombok.Value;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Builder
public final class Table implements Serializable {
  private final UUID id;
  private final UUID user;

  @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
  private final LocalDateTime assignedAt;

  public static Table of(final UUID userId) {
    return Table.builder()
        .id(UUID.randomUUID())
        .assignedAt(LocalDateTime.now())
        .user(userId)
        .build();
  }
}
