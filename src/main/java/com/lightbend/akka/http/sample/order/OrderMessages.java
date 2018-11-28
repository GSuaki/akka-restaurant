package com.lightbend.akka.http.sample.order;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

public interface OrderMessages {

  class GetOrders implements Serializable {
  }

  @Value
  class NewOrder implements Serializable {
    private final UUID customer;
  }

  @Value
  class OrderCreated implements Serializable {
    private final UUID id;
    private final UUID customer;
  }

  @Value
  class GetOrder implements Serializable {
    private final UUID customer;
  }

  @Value
  class DeleteOrder implements Serializable {
    private final UUID customer;
  }
}
