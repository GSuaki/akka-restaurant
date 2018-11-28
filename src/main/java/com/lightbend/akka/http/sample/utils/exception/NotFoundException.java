package com.lightbend.akka.http.sample.utils.exception;

public class NotFoundException extends RuntimeException {

  public NotFoundException(final String message) {
    super(message);
  }
}
