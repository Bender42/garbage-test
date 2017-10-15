package com.example.garbage;

import android.content.Context;
import android.util.SparseArray;

import com.example.garbage.expenditure.Expenditure;

import java.math.BigDecimal;

public interface IWalletOperation {

    Integer getId();

    String getName();

    BigDecimal getAmount();

    Long getTime();

    boolean delete(Context context, SparseArray<IWallet> wallets);

    boolean isAddingAmount(IWallet currentWallet);

    String getDescription(IWallet currentWallet, SparseArray<IWallet> wallets, SparseArray<Expenditure> expenditures);
}
