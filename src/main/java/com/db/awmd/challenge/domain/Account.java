package com.db.awmd.challenge.domain;

import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.NotEmpty;

@Data
public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
      @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }

  /**
   * Method used to debit amount from the account.
   *
   * @param amount Amount being debited.
   */
  public void debit(BigDecimal amount) {
    if (balance.compareTo(amount) < 0) {
      throw new InsufficientBalanceException(
          "Account " + accountId + " does not have sufficient amount to debit from.");
    }

    this.balance = this.balance.subtract(amount);
  }

  /**
   * Method used to credit amount from the account.
   *
   * @param amount Amount being credited.
   */
  public void credit(BigDecimal amount) {
    this.balance = this.balance.add(amount);
  }
}
