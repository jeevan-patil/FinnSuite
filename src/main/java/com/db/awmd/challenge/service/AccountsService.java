package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.domain.TransferRequest;
import com.db.awmd.challenge.domain.TransferResponse;
import com.db.awmd.challenge.exception.AccountNotFoundException;
import com.db.awmd.challenge.repository.AccountsRepository;
import java.math.BigDecimal;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AccountsService {

  @Getter
  private final AccountsRepository accountsRepository;

  @Autowired
  private NotificationService notificationService;

  @Autowired
  public AccountsService(AccountsRepository accountsRepository) {
    this.accountsRepository = accountsRepository;
  }

  public void createAccount(Account account) {
    this.accountsRepository.createAccount(account);
  }

  public Account getAccount(String accountId) {
    return this.accountsRepository.getAccount(accountId);
  }

  /**
   * Method is used for money transfer. Money is transferred from one account to another.
   *
   * @param transferRequest Transfer request contains both account details and amount to transfer.
   * @return {@code TransferResponse} An object which is returned by API with status and message.
   */
  public TransferResponse transferAmount(final TransferRequest transferRequest) {
    Account transferFromAccount = accountsRepository.getAccount(transferRequest.getAccountFromId());
    Account transferToAccount = accountsRepository.getAccount(transferRequest.getAccountToId());

    // check if the accounts are valid or present
    checkAccountExistence(transferRequest, transferFromAccount, transferToAccount);

    final BigDecimal transferAmount = transferRequest.getAmount();
    accountsRepository
        .transferBetweenAccounts(transferFromAccount, transferToAccount, transferAmount);
    notificationService.notifyAboutTransfer(transferFromAccount,
        "Your account has been debited with " + transferAmount);
    notificationService.notifyAboutTransfer(transferToAccount,
        "Your account has been credited with " + transferAmount);

    final String message =
        transferAmount + " amount has been transferred from account " + transferFromAccount
            .getAccountId() + " to " + transferToAccount.getAccountId();
    log.info(message);

    return new TransferResponse(true, message);
  }

  private void checkAccountExistence(TransferRequest transferRequest, Account transferFromAccount,
      Account transferToAccount) {
    if (transferFromAccount == null) {
      final String error = "Account with id " + transferRequest.getAccountFromId() + " not found.";
      log.error(error);
      throw new AccountNotFoundException(error);
    }

    if (transferToAccount == null) {
      final String error = "Account with id " + transferRequest.getAccountToId() + " not found.";
      log.error(error);
      throw new AccountNotFoundException(error);
    }
  }
}
