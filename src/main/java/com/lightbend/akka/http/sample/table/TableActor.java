package com.lightbend.akka.http.sample.table;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.vavr.control.Try;

import java.io.Serializable;
import java.util.UUID;

public class TableActor extends AbstractActor implements TableMessages {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  public static Props props() {
    return Props.create(TableActor.class);
  }

  @Override
  public void preStart() {
    log.info("Starting table actor");
  }

  @Override
  public void postStop() { log.info("Table actor stopped"); }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(OpenTable.class, user -> {
          final UUID customer = user.getCustomer();
          log.info("Opening table for customer: {}", customer);

          final Serializable response = Try
              .of(() -> Tables.INSTANCE.assign(customer))
              .map(id -> (Serializable) new TableOpened(id, customer))
              .getOrElse(() -> new TableUnavailable(customer));

          sender().tell(response, self());
        })
        .match(GetTable.class, msg -> sender().tell(Tables.INSTANCE.getByCustomer(msg.getCustomer()), self()))
        .matchAny(o -> log.info("TableActor received unknown message: {}", o))
        .build();
  }
}
