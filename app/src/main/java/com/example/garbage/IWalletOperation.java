package com.example.garbage;

import android.content.Context;

import com.example.garbage.expenditure.Expenditure;

import java.math.BigDecimal;
import java.util.Map;

public interface IWalletOperation {

    Integer getId();

    String getName();

    BigDecimal getAmount();

    Long getTime();

    boolean delete(Context context, Map<Integer, IWallet> wallets);

    boolean isAddingAmount(IWallet currentWallet);

    String getDescription(IWallet currentWallet, Map<Integer, IWallet> wallets, Map<Integer, Expenditure> expenditures);
}
