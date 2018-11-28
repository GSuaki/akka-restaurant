package com.lightbend.akka.http.sample;

import akka.actor.ActorSystem;
import akka.http.javadsl.ConnectHttp;
import akka.http.javadsl.marshallers.jackson.Jackson;
import akka.http.javadsl.marshalling.Marshaller;
import akka.stream.ActorMaterializer;
import com.google.inject.AbstractModule;
import com.lightbend.akka.http.sample.kafka.Kafka;
import com.lightbend.akka.http.sample.kafka.KafkaImpl;
import com.lightbend.akka.http.sample.utils.Json;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public abstract class Module extends AbstractModule {

  private final ActorSystem system = ActorSystem.create("restaurant-server");

  @Override
  protected void configure() {
    final Config config = ConfigFactory.load();

    this.bind(Kafka.class).toInstance(new KafkaImpl(system));

    initializeConfig(config);
    initializeConnectHttp(config);
    initializeActorSystem();
    initializeActorMaterializer();
    initializeMarshaller();
  }

  private void initializeConfig(final Config config) {
    this.bind(Config.class).toInstance(config);
  }

  private void initializeConnectHttp(final Config config) {
    this.bind(ConnectHttp.class)
        .toInstance(ConnectHttp.toHost(config.getString("server.host"), config.getInt("server.port")));
  }

  private void initializeActorSystem() {
    this.bind(ActorSystem.class).toInstance(system);
  }

  private void initializeActorMaterializer() {
    this.bind(ActorMaterializer.class).toInstance(ActorMaterializer.create(system));
  }

  private void initializeMarshaller() {
    this.bind(Marshaller.class).toInstance(Jackson.marshaller(Json.mapper));
  }
}
