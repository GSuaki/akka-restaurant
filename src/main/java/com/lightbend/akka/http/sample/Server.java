package com.lightbend.akka.http.sample;

import akka.NotUsed;
import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.Http;
import akka.http.javadsl.ServerBinding;
import akka.http.javadsl.model.HttpRequest;
import akka.http.javadsl.model.HttpResponse;
import akka.http.javadsl.server.Route;
import akka.stream.ActorMaterializer;
import akka.stream.javadsl.Flow;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.lightbend.akka.http.sample.config.DevModule;
import com.lightbend.akka.http.sample.manager.ManagerRoutes;

import java.util.concurrent.CompletionStage;

public class Server {

  public static void main(final String[] args) {
    startServer();
  }

  public static void startServer() {
    final Injector injector = Guice.createInjector(new DevModule());

    final ActorSystem system = injector.getInstance(ActorSystem.class);
    final ActorMaterializer materializer = injector.getInstance(ActorMaterializer.class);

    final Route managerRoutes = new ManagerRoutes(system).routes();

    final Router router = injector.getInstance(Router.class);

    final Http http = Http.get(system);
    final ConnectHttp connect = injector.getInstance(ConnectHttp.class);

    final Flow<HttpRequest, HttpResponse, NotUsed> routeFlow = router.routes(managerRoutes).flow(system, materializer);

    final CompletionStage<ServerBinding> binding = http.bindAndHandle(routeFlow, connect, materializer)
        .whenComplete((hs, t) -> System.out.println("Server is running"));

    Runtime.getRuntime().addShutdownHook(new Thread(() -> {
      binding
          .thenCompose(ServerBinding::unbind)
          .thenAccept(unbound -> system.terminate());
    }));
  }
}


