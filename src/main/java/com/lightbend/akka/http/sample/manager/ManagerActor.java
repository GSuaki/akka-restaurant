package com.lightbend.akka.http.sample.manager;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import akka.util.Timeout;
import com.lightbend.akka.http.sample.customer.Customer;
import com.lightbend.akka.http.sample.customer.Customer.Status;
import com.lightbend.akka.http.sample.customer.CustomerActor;
import com.lightbend.akka.http.sample.customer.CustomerMessages.CreateCustomer;
import com.lightbend.akka.http.sample.customer.CustomerMessages.CustomerCreated;
import com.lightbend.akka.http.sample.customer.CustomerMessages.GetCustomer;
import com.lightbend.akka.http.sample.customer.CustomerMessages.UpdateStatus;
import com.lightbend.akka.http.sample.customer.CustomerResume;
import com.lightbend.akka.http.sample.order.Order;
import com.lightbend.akka.http.sample.order.OrderActor;
import com.lightbend.akka.http.sample.order.OrderMessages;
import com.lightbend.akka.http.sample.order.OrderMessages.GetOrder;
import com.lightbend.akka.http.sample.table.Table;
import com.lightbend.akka.http.sample.table.TableActor;
import com.lightbend.akka.http.sample.table.TableMessages;
import com.lightbend.akka.http.sample.table.TableMessages.GetTable;
import com.lightbend.akka.http.sample.table.TableMessages.TableOpened;
import com.lightbend.akka.http.sample.table.TableMessages.TableUnavailable;
import io.vavr.control.Option;
import scala.concurrent.duration.Duration;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionStage;

import static akka.pattern.PatternsCS.ask;
import static java.util.concurrent.TimeUnit.MINUTES;

public class ManagerActor extends AbstractActor implements ManagerMessages {

  private final LoggingAdapter log = Logging.getLogger(getContext().getSystem(), this);

  private ActorRef request;

  private final ActorRef orders;
  private final ActorRef tables;
  private final ActorRef customers;

  public ManagerActor(final String name) {
    this.orders = getContext().actorOf(OrderActor.props(), "orders-for-" + name);
    this.tables = getContext().actorOf(TableActor.props(), "tables-for-" + name);
    this.customers = getContext().actorOf(CustomerActor.props(), "customers-for-" + name);
  }

  @Override
  public void preStart() { log.info("Starting manager actor"); }

  @Override
  public void postStop() {
    log.info("Manager actor stopped");
  }

  @Override
  @SuppressWarnings("unchecked")
  public Receive createReceive() {
    return receiveBuilder()
        .match(NewCustomer.class, input -> {
          log.info("New customer arrived: {}", input.getUser());
          this.request = sender();
          this.customers.tell(new CreateCustomer(input.getUser()), self());
        })
        .match(CustomerCreated.class, res -> {
          log.info("Customer registered as: {}", res.getId());
          this.tables.tell(new TableMessages.OpenTable(res.getId()), self());
        })
        .match(TableOpened.class, res -> {
          final UUID customerId = res.getCustomer();
          log.info("Table opened for customers: {}", customerId);

          this.orders.tell(new OrderMessages.NewOrder(customerId), getSelf());
          this.customers.tell(new UpdateStatus(customerId, Status.TABLE_ASSIGNED), self());
          this.request.tell(new CustomerCreated(customerId), getSelf());

          context().stop(self());
        })
        .match(TableUnavailable.class, res -> {
          log.info("Table unavailable for customers: {}", res);
          final UUID customerId = res.getCustomer();

          this.customers.tell(new UpdateStatus(customerId, Status.TABLE_UNAVAILABLE), self());
          this.request.tell(new TableUnavailable(customerId), self());

          context().stop(self());
        })
        .match(GetCustomerResume.class, user -> {
          this.request = sender();
          final UUID id = user.getId();

          final CompletionStage<Option<Customer>> customer = ask(customers, new GetCustomer(id), new Timeout(Duration.create(2, MINUTES)))
              .thenApply(res -> (Option<Customer>) res);

          final CompletionStage<Option<Order>> order = ask(orders, new GetOrder(id), new Timeout(Duration.create(2, MINUTES)))
              .thenApply(res -> (Option<Order>) res);

          final CompletionStage<Option<Table>> table = ask(tables, new GetTable(id), new Timeout(Duration.create(2, MINUTES)))
              .thenApply(res -> (Option<Table>) res);

          customer
              .thenCombine(order, (_customer, _order) -> {
                return _customer
                    .map(CustomerResume::of)
                    .map(resume -> _order.map(resume::withOrder)
                        .getOrElse(resume));
              })
              .thenCombine(table, (_resume, _table) -> {
                return _resume
                    .map(resume -> _table.map(resume::withTable)
                        .getOrElse(resume));
              })
              .thenAccept(res -> {
                res
                    .peek(resume -> this.request.tell(Optional.of(resume), self()))
                    .onEmpty(() -> this.request.tell(Optional.empty(), self()));

                context().stop(self());
              });

//          Option
//              .of(Customers.INSTANCE.get(id))
//              .map(CustomerResume::of)
//              .map(resume -> Orders.INSTANCE.getByCustomer(id)
//                  .map(resume::withOrder)
//                  .getOrElse(resume))
//              .map(resume -> Tables.INSTANCE.getByCustomer(id).
//                  map(resume::withTable)
//                  .getOrElse(resume))
//              .peek(resume -> getSender().tell(Optional.of(resume), self()))
//              .onEmpty(() -> getSender().tell(Optional.empty(), self()));
        })
        .matchAny(o -> log.info("ManagerActor received unknown message: {}", o))
        .build();
  }

  public static Props props(final String customer) {
    return Props.create(ManagerActor.class, customer);
  }
}
