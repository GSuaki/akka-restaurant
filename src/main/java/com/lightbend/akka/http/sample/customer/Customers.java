package com.lightbend.akka.http.sample.customer;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum Customers {
  INSTANCE;

  private final Map<UUID, Customer> customers;

  Customers() {
    this.customers = new ConcurrentHashMap<>();
  }

  public final Customer create(final String user) {
    final Customer customer = Customer.of(user);
    this.customers.putIfAbsent(customer.getId(), customer);
    return customer;
  }

  public Customer save(final Customer customer) {
    this.customers.put(customer.getId(), customer);
    return customer;
  }

  public Customer get(final UUID customer) {
    return this.customers.get(customer);
  }
}
