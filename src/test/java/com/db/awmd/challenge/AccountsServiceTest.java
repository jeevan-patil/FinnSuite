package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsServiceTest {

  @Autowired
  private AccountsService accountsService;

  @Test
  public void addAccount() throws Exception {
    Account account = new Account("Id-123");
    account.setBalance(new BigDecimal(1000));
    this.accountsService.createAccount(account);

    assertThat(this.accountsService.getAccount("Id-123")).isEqualTo(account);
  }

  @Test
  public void addAccount_failsOnDuplicateId() throws Exception {
    String uniqueId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueId);
    this.accountsService.createAccount(account);

    try {
      this.accountsService.createAccount(account);
      fail("Should have failed when adding duplicate account");
    } catch (DuplicateAccountIdException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account id " + uniqueId + " already exists!");
    }

  }

  @Test
  public void transferAmount() throws Exception {
    createAccount("AC1", new BigDecimal(4000));
    createAccount("AC2", new BigDecimal(3000));

    TransferRequest transferRequest = new TransferRequest();
    transferRequest.setAccountFromId("AC2");
    transferRequest.setAccountToId("AC1");
    transferRequest.setAmount(new BigDecimal(200));

    this.accountsService.transferAmount(transferRequest);
    assertThat(this.accountsService.getAccount("AC1").getBalance())
        .isEqualByComparingTo(new BigDecimal(4200));

    assertThat(this.accountsService.getAccount("AC2").getBalance())
        .isEqualByComparingTo(new BigDecimal(2800));
  }



  private void createAccount(String id, BigDecimal balance) {
    Account account = new Account(id, balance);
    this.accountsService.createAccount(account);
  }
}
