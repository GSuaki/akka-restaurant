package com.lightbend.akka.http.sample.order;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

import java.util.UUID;

public class OrderActor extends AbstractActor implements OrderMessages {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  public static Props props() {
    return Props.create(OrderActor.class);
  }

  @Override
  public void preStart() {
    log.info("Starting order actor");
  }

  @Override
  public void postStop() {
    log.info("Order actor stopped");
  }

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(NewOrder.class, user -> {
          final UUID customer = user.getCustomer();
          log.info("Creating order for customer: {}", customer);
          Orders.INSTANCE.create(customer);
        })
        .match(GetOrder.class, user -> sender().tell(Orders.INSTANCE.getByCustomer(user.getCustomer()), self()))
        .matchAny(o -> log.info("OrderActor received unknown message: {}", o))
        .build();
  }
}
