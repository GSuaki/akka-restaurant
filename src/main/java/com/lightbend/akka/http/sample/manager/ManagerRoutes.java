package com.lightbend.akka.http.sample.manager;

import akka.actor.*;
import akka.dispatch.ExecutionContexts;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.model.StatusCodes;
import akka.http.javadsl.server.AllDirectives;
import akka.http.javadsl.server.PathMatchers;
import akka.http.javadsl.server.Route;
import akka.pattern.PatternsCS;
import akka.util.Timeout;
import com.lightbend.akka.http.sample.customer.CustomerMessages;
import com.lightbend.akka.http.sample.customer.CustomerResume;
import com.lightbend.akka.http.sample.customer.NewCustomer;
import com.lightbend.akka.http.sample.manager.ManagerMessages.CustomerClosed;
import com.lightbend.akka.http.sample.system.IdentifyActor;
import scala.concurrent.duration.Duration;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.TimeUnit;

import static io.vavr.API.*;
import static io.vavr.Predicates.instanceOf;
import static java.time.Duration.ofMinutes;

public class ManagerRoutes extends AllDirectives {

  private static final Timeout timeout = new Timeout(Duration.create(150, TimeUnit.SECONDS));

  private final LoggingAdapter log;
  //  private final ActorRef manager;
  private final ActorSystem system;

  public ManagerRoutes(final ActorSystem system) {
    this.log = Logging.getLogger(system, this);
    this.system = system;
//    this.manager = system.actorOf(ManagerActor.props());
  }

  public Route routes() {
    return route(pathPrefix("managers", () ->
        route(
            post(),
            path(PathMatchers.uuidSegment(), id -> route(
                get(id),
                delete(id)
            )),
            path("actors", () -> route(
                listActors()
            ))
        )
    ));
  }

  @SuppressWarnings("unchecked")
  private Route get(final UUID customer) {
    return get(() -> {
      final ActorRef manager = system.actorOf(ManagerActor.props("get-".concat(customer.toString())));

      final CompletionStage<Optional<CustomerResume>> maybeOrder = PatternsCS
          .ask(manager, new ManagerMessages.GetCustomerResume(customer), timeout)
          .thenApply(obj -> (Optional<CustomerResume>) obj);

      return onSuccess(() -> maybeOrder, performed -> {
        if (performed.isPresent())
          return complete(StatusCodes.OK, performed.get(), Jackson.marshaller());
        else
          return complete(StatusCodes.NOT_FOUND, "Resource not found");
      });
    });
  }

  private Route listActors() {
    return get(() -> {
      final ActorRef actorRef = system.actorOf(IdentifyActor.props());
      final ActorSelection selection = system.actorSelection("/user/*");

      selection.tell(new Identify("list"), actorRef);

      scheduleTask(() -> actorRef.tell(PoisonPill.getInstance(), ActorRef.noSender()));

      return complete(StatusCodes.OK, "OK", Jackson.marshaller());
    });
  }

  private Route delete(final UUID customer) {
    return
        delete(() -> {
          final ActorRef manager = system.actorOf(ManagerActor.props("delete-".concat(customer.toString())));

          CompletionStage<CustomerClosed> deleteAsk = PatternsCS
              .ask(manager, new ManagerMessages.CloseCustomer(customer), timeout)
              .thenApply(obj -> (CustomerClosed) obj);

          return onSuccess(() -> deleteAsk, performed -> {
            log.info("Deleted order [{}]: {}", customer, performed.getId());
            return complete(StatusCodes.OK, performed, Jackson.marshaller());
          });
        });
  }

  private Route post() {
    return pathEnd(() ->
        route(
            post(() ->
                entity(
                    Jackson.unmarshaller(NewCustomer.class),
                    input -> {
                      final ActorRef manager = system.actorOf(ManagerActor.props(input.getName()), "manager-for-".concat(input.getName()));

                      CompletionStage<Serializable> creationAsk = PatternsCS
                          .ask(manager, new ManagerMessages.NewCustomer(input.getName()), timeout)
                          .thenApply(obj -> (Serializable) obj);

                      return onSuccess(() -> creationAsk, performed -> {
                        return Match(performed).of(
                            Case($(instanceOf(CustomerMessages.CustomerCreated.class)), res -> {
                              log.info("Created resource [{}]: {}", input.getName(), res);
                              return complete(StatusCodes.CREATED, res, Jackson.marshaller());
                            }),
                            Case($(), res -> {
                              log.info("Precondition failed [{}]: {}", input.getName(), res);
                              return complete(StatusCodes.PRECONDITION_FAILED, res, Jackson.marshaller());
                            })
                        );
                      });
                    }
                ))
        )
    );
  }

  private void scheduleTask(final Runnable runnable) {
    system
        .scheduler()
        .scheduleOnce(
            ofMinutes(1),
            runnable,
            ExecutionContexts.global()
        );
  }
}
