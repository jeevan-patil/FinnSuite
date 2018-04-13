package com.db.awmd.challenge.domain;

/**
 * Class used to store and pass certain information after money transfer operation.
 *
 * @author jeevan
 */
public class TransferResponse {

  private boolean status;
  private String message;

  public TransferResponse(boolean status, String message) {
    this.status = status;
    this.message = message;
  }

  public boolean isStatus() {
    return status;
  }

  public String getMessage() {
    return message;
  }
}
