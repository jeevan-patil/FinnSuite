package com.db.awmd.challenge.domain;

import java.math.BigDecimal;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Class holds information about money transfer between accounts.
 *
 * @author jeevan
 */
@Data
public class TransferRequest {

  @NotNull
  private String accountFromId;

  @NotNull
  private String accountToId;

  @NotNull
  @Min(value = 1, message = "Transfer amount must be greater than zero.")
  private BigDecimal amount;
}
