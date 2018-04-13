package com.db.awmd.challenge.exception;

/**
 * @author jeevan
 */
public class AccountNotFoundException extends RuntimeException {

  public AccountNotFoundException(String message) {
    super(message);
  }
}
