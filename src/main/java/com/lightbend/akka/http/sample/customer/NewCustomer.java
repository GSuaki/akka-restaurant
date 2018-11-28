package com.lightbend.akka.http.sample.customer;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Value;

@Value
public final class NewCustomer {
  private final String name;

  @JsonCreator
  public NewCustomer(@JsonProperty("name") final String name) {
    this.name = name;
  }
}
