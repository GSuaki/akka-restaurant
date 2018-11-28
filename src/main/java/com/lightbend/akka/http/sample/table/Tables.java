package com.lightbend.akka.http.sample.table;

import io.vavr.collection.Stream;
import io.vavr.control.Option;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public enum Tables {
  INSTANCE;

  private static final int LIMIT = 2;

  private final Map<UUID, Table> tables;

  Tables() {
    tables = new ConcurrentHashMap<>();
  }

  public Table get(final UUID table) {
    return tables.get(table);
  }

  public UUID assign(final UUID user) {
    if (LIMIT == tables.size()) {
      throw new IllegalStateException("No tables available");
    }

    final UUID uuid = UUID.randomUUID();
    tables.put(uuid, Table.of(user));
    return uuid;
  }

  public Option<Table> getByCustomer(final UUID id) {
    return Stream.ofAll(this.tables.values())
        .filter(order -> order.getUser().equals(id))
        .headOption();
  }
}
