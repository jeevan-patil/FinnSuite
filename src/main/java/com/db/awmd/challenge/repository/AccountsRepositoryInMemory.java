package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

@Repository
@Slf4j
public class AccountsRepositoryInMemory implements AccountsRepository {

  private final Map<String, Account> accounts = new ConcurrentHashMap<>();

  @Override
  public void createAccount(Account account) throws DuplicateAccountIdException {
    Account previousAccount = accounts.putIfAbsent(account.getAccountId(), account);
    if (previousAccount != null) {
      throw new DuplicateAccountIdException(
          "Account id " + account.getAccountId() + " already exists!");
    }
  }

  @Override
  public Account getAccount(String accountId) {
    return accounts.get(accountId);
  }

  @Override
  public void clearAccounts() {
    accounts.clear();
  }

  /**
   * Method which updates records in the database while money transfer.
   *
   * @param fromAccount Money being transferred from account.
   * @param toAccount Money being transferred to account.
   * @param amount Amount being transferred between accounts.
   */
  @Override
  public void transferBetweenAccounts(final Account fromAccount, final Account toAccount,
      final BigDecimal amount) {
    fromAccount.debit(amount);
    toAccount.credit(amount);
    accounts.put(fromAccount.getAccountId(), fromAccount);
    accounts.put(toAccount.getAccountId(), toAccount);
    log.info("Money has been transferred between accounts.");
  }
}
