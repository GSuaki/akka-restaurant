package com.lightbend.akka.http.sample.customer;

import com.lightbend.akka.http.sample.order.Order;
import com.lightbend.akka.http.sample.table.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import lombok.experimental.Wither;

import java.util.UUID;

@Value
@Wither
@Builder
@AllArgsConstructor
public final class CustomerResume {
  private final UUID id;
  private final Table table;
  private final Order order;
  private final String user;
  private final Customer.Status status;

  public CustomerResume(final Customer customer, final Order order, final Table table) {
    this.id = customer.getId();
    this.status = customer.getStatus();
    this.user = customer.getUser();
    this.table = table;
    this.order = order;
  }

  public static CustomerResume of(final Customer customer) {
    return CustomerResume.builder()
        .user(customer.getUser())
        .status(customer.getStatus())
        .id(customer.getId())
        .build();
  }
}