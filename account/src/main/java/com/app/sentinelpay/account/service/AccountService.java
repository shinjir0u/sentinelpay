package com.app.sentinelpay.account.service;

import java.math.BigDecimal;

public interface AccountService {

    BigDecimal transfer(String senderAccountNumber, String receiverAccountNumber, BigDecimal amount);

}
