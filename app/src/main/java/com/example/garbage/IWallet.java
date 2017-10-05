package com.example.garbage;

import android.content.Context;

import java.math.BigDecimal;

public interface IWallet {

    Integer getId();

    BigDecimal getAmount();

    String getName();

    String getCurrency();

    boolean isComplete();

    boolean post(Context context);

    Boolean isIncomeItem();
}
