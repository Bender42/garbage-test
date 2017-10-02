package com.example.garbage;

import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.wallet.Wallet;

import java.math.BigDecimal;
import java.util.Map;

public interface IWalletOperation {

    Integer getId();

    String getName();

    BigDecimal getAmount();

    Long getTime();

    boolean isAddingAmount();

    String getDescription(Wallet currentWallet, Map<Integer, Wallet> wallets, Map<Integer, Expenditure> expenditures);
}
