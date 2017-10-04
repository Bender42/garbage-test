package com.example.garbage;

import android.content.Context;

import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.wallet.Wallet;

import java.math.BigDecimal;
import java.util.Map;

public interface IWalletOperation {

    Integer getId();

    String getName();

    BigDecimal getAmount();

    Long getTime();

    boolean delete(Context context, Map<Integer, Wallet> wallets);

    boolean isAddingAmount(Wallet currentWallet);

    String getDescription(Wallet currentWallet, Map<Integer, Wallet> wallets, Map<Integer, Expenditure> expenditures);
}
