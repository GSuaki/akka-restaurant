package com.lightbend.akka.http.sample.table;

import lombok.Value;

import java.io.Serializable;
import java.util.UUID;

public interface TableMessages {

  @Value
  class GetTable implements Serializable {
    private final UUID customer;
  }

  @Value
  class OpenTable implements Serializable {
    private final UUID customer;
  }

  @Value
  class TableOpened implements Serializable {
    private final UUID table;
    private final UUID customer;
  }

  @Value
  class TableUnavailable implements Serializable {
    private final UUID customer;

    public String getMessage() {
      return "No table available for you right now. I'm sorry!";
    }
  }

  @Value
  class CloseTable implements Serializable {
    private final UUID table;
  }

  @Value
  class TableClosed implements Serializable {
    private final UUID table;
  }
}
