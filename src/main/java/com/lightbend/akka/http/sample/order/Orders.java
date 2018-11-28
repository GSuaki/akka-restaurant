package com.lightbend.akka.http.sample.order;

import io.vavr.collection.Stream;
import io.vavr.control.Option;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum Orders {
  INSTANCE;

  private final Map<UUID, Order> orders;

  Orders() {
    this.orders = new ConcurrentHashMap<>();
  }

  public final Order create(final UUID user) {
    final Order customer = Order.of(user);
    this.orders.put(customer.getId(), customer);
    return customer;
  }

  public Order save(final Order customer) {
    this.orders.put(customer.getId(), customer);
    return customer;
  }

  public Option<Order> getByCustomer(final UUID id) {
    return Stream.ofAll(this.orders.values())
        .filter(order -> order.getUser().equals(id))
        .headOption();
  }
}
