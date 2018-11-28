package com.lightbend.akka.http.sample.customer;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

public interface CustomerMessages {

  @Value
  class CreateCustomer implements Serializable {
    private final String user;
  }

  @Value
  class CustomerCreated implements Serializable {
    private final UUID id;

    public String getLink() {
      return "http://localhost:8080/managers/".concat(id.toString());
    }
  }

  @Value
  class GetCustomer implements Serializable {
    private final UUID id;
  }

  @Value
  class UpdateStatus implements Serializable {
    private final UUID id;
    private final Customer.Status status;
  }
}
