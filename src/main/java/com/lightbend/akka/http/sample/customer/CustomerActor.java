package com.lightbend.akka.http.sample.customer;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import io.vavr.control.Option;

public class CustomerActor extends AbstractActor implements CustomerMessages {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(CreateCustomer.class, input -> {
          log.info("Creating customer for user: {}", input.getUser());
          final Customer customer = Customers.INSTANCE.create(input.getUser());

          getSender()
              .forward(new CustomerCreated(customer.getId()), getContext());
        })
        .match(UpdateStatus.class, input -> {
          log.info("Updating to status {} for customer: {}", input.getStatus(), input.getId());
          Customers.INSTANCE.save(Customers.INSTANCE.get(input.getId()).withStatus(input.getStatus()));
        })
        .match(GetCustomer.class, input -> sender().tell(Option.of(Customers.INSTANCE.get(input.getId())), self()))
        .build();
  }

  public static Props props() {
    return Props.create(CustomerActor.class);
  }
}
