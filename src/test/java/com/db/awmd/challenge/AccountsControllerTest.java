package com.db.awmd.challenge;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.exception.InsufficientBalanceException;
import com.db.awmd.challenge.service.AccountsService;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class AccountsControllerTest {

  private MockMvc mockMvc;

  @Autowired
  private AccountsService accountsService;

  @Autowired
  private WebApplicationContext webApplicationContext;

  @Before
  public void prepareMockMvc() {
    this.mockMvc = webAppContextSetup(this.webApplicationContext).build();

    // Reset the existing accounts before each test.
    accountsService.getAccountsRepository().clearAccounts();
  }

  @Test
  public void createAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    Account account = accountsService.getAccount("Id-123");
    assertThat(account.getAccountId()).isEqualTo("Id-123");
    assertThat(account.getBalance()).isEqualByComparingTo("1000");
  }

  @Test
  public void createDuplicateAccount() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isCreated());

    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-123\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-123\"}")).andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNoBody() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountNegativeBalance() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"Id-123\",\"balance\":-1000}"))
        .andExpect(status().isBadRequest());
  }

  @Test
  public void createAccountEmptyAccountId() throws Exception {
    this.mockMvc.perform(post("/v1/accounts").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountId\":\"\",\"balance\":1000}")).andExpect(status().isBadRequest());
  }

  @Test
  public void getAccount() throws Exception {
    String uniqueAccountId = "Id-" + System.currentTimeMillis();
    Account account = new Account(uniqueAccountId, new BigDecimal("123.45"));
    this.accountsService.createAccount(account);
    this.mockMvc.perform(get("/v1/accounts/" + uniqueAccountId))
        .andExpect(status().isOk())
        .andExpect(
            content().string("{\"accountId\":\"" + uniqueAccountId + "\",\"balance\":123.45}"));
  }

  /**
   * Positive test for amount transfer between accounts.
   */
  @Test
  public void transferAmount() throws Exception {
    createAccount("AC1", new BigDecimal(4000));
    createAccount("AC2", new BigDecimal(3000));

    this.mockMvc.perform(post("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountFromId\":\"AC2\",\"accountToId\":\"AC1\",\"amount\":200}"))
        .andExpect(status().isOk());
  }

  /**
   * Negative test where amount being transferred is invalid.
   */
  @Test
  public void transferAmountInvalidAmount() throws Exception {
    createAccount("AC1", new BigDecimal(4000));
    createAccount("AC2", new BigDecimal(3000));

    this.mockMvc.perform(post("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
        .content("{\"accountFromId\":\"AC2\",\"accountToId\":\"AC1\",\"amount\":0}"))
        .andExpect(status().isBadRequest());
  }

  /**
   * Negative test where one of the accounts does not exist.
   */
  //@Test
  public void transferAmountAccountNotPresent() throws Exception {
    createAccount("AC1", new BigDecimal(4000));

    try {
      this.mockMvc.perform(post("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
          .content("{\"accountFromId\":\"AC2\",\"accountToId\":\"AC1\",\"amount\":200}"));
      fail("Should have failed while money transfer.");
    } catch (AccountNotFoundException ex) {
      assertThat(ex.getMessage()).isEqualTo("Account with id AC2 not found.");
    }
  }

  /**
   * Transfer amount test where an account has insufficient balance.
   */
  //@Test
  public void transferAmountInsufficientBalance() throws Exception {
    createAccount("AC1", new BigDecimal(4000));
    createAccount("AC2", new BigDecimal(3000));

    try {
      this.mockMvc.perform(post("/v1/accounts/transfer").contentType(MediaType.APPLICATION_JSON)
          .content("{\"accountFromId\":\"AC2\",\"accountToId\":\"AC1\",\"amount\":3200}"));
      fail("Should have failed while money transfer due to insufficient balance in the account.");
    } catch (InsufficientBalanceException ex) {
      assertThat(ex.getMessage())
          .isEqualTo("Account AC2 does not have sufficient amount to debit from.");
    }
  }

  private void createAccount(String id, BigDecimal balance) {
    Account account = new Account(id, balance);
    this.accountsService.createAccount(account);
  }
}
