package com.lightbend.akka.http.sample.manager;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

public interface ManagerMessages {

  @Value
  class NewCustomer implements Serializable {
    private final String user;
  }

  @Value
  class GetCustomerResume implements Serializable {
    private final UUID id;
  }

  @Value
  class CloseCustomer implements Serializable {
    private final UUID id;
  }

  @Value
  class CustomerClosed implements Serializable {
    private final UUID id;
  }
}
