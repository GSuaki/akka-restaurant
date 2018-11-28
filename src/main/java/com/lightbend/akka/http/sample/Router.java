package com.lightbend.akka.http.sample;

import akka.http.javadsl.server.Route;
import akka.http.javadsl.server.directives.RouteDirectives;

public final class Router extends RouteDirectives {

  public Route routes(final Route routes) {
    return route(
        routes
    );
  }
}
