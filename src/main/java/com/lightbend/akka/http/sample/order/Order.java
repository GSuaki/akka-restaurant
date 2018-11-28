package com.lightbend.akka.http.sample.order;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.lightbend.akka.http.sample.utils.bind.javatime.LocalDateTimeJsonSerializer;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Value
@Wither
@Builder
public final class Order implements Serializable {
  private final UUID id;
  private final UUID user;

  @JsonSerialize(using = LocalDateTimeJsonSerializer.class)
  private final LocalDateTime createdAt;

  public static Order of(final UUID userId) {
    return Order.builder()
        .id(UUID.randomUUID())
        .createdAt(LocalDateTime.now())
        .user(userId)
        .build();
  }
}
