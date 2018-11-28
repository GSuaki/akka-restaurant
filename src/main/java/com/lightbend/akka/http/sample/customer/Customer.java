package com.lightbend.akka.http.sample.customer;

import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.UUID;

import static com.lightbend.akka.http.sample.customer.Customer.Status.WAITING_TABLE;

@Value
@Wither
@Builder
public final class Customer {
  private final UUID id;
  private final String user;
  private final Status status;

  public final static Customer of(final String user) {
    return Customer.builder()
        .id(UUID.randomUUID())
        .user(user)
        .status(WAITING_TABLE)
        .build();
  }

  public enum Status {
    WAITING_TABLE, TABLE_ASSIGNED, TABLE_UNAVAILABLE
  }
}