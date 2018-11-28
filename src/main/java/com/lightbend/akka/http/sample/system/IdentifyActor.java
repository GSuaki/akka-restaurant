package com.lightbend.akka.http.sample.system;

import akka.actor.AbstractActor;
import akka.actor.ActorIdentity;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;

public class IdentifyActor extends AbstractActor {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  @Override
  public Receive createReceive() {
    return receiveBuilder()
        .match(ActorIdentity.class, res -> log.info("Identified {}", res))
        .build();
  }

  public static Props props() {
    return Props.create(IdentifyActor.class);
  }
}
