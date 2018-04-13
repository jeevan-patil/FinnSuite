package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepositoryInMemory;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author jeevan
 */

@RunWith(SpringRunner.class)
@SpringBootTest
public class AccountsRepositoryInMemoryTest {

  @Autowired
  private AccountsRepositoryInMemory accountsRepositoryInMemory;

  @Before
  public void prepareMockMvc() {
    this.accountsRepositoryInMemory.clearAccounts();
  }

  @Test
  public void transferBetweenAccounts() throws Exception {
    Account accountTo = new Account("AC4", new BigDecimal(4000));
    Account accountFrom = new Account("AC3", new BigDecimal(3000));

    accountsRepositoryInMemory.createAccount(accountFrom);
    accountsRepositoryInMemory.createAccount(accountTo);

    this.accountsRepositoryInMemory
        .transferBetweenAccounts(accountFrom, accountTo, new BigDecimal(200));

    assertThat(this.accountsRepositoryInMemory.getAccount("AC3").getBalance())
        .isEqualByComparingTo(new BigDecimal(2800));

    assertThat(this.accountsRepositoryInMemory.getAccount("AC4").getBalance())
        .isEqualByComparingTo(new BigDecimal(4200));
  }
}
