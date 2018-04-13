package com.db.awmd.challenge.repository;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.exception.DuplicateAccountIdException;
import java.math.BigDecimal;

public interface AccountsRepository {

  void createAccount(Account account) throws DuplicateAccountIdException;

  Account getAccount(String accountId);

  void clearAccounts();

  /**
   * Method which updates records in the database while money transfer.
   *
   * @param fromAccount Money being transferred from account.
   * @param toAccount Money being transferred to account.
   * @param amount Amount being transferred between accounts.
   */
  void transferBetweenAccounts(Account fromAccount, Account toAccount, BigDecimal amount);
}
