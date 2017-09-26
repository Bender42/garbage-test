package com.example.garbage.cost_item;

import java.math.BigDecimal;
import java.sql.Time;

public class CostItem {

    public static String COST_ITEM_TABLE_NAME = "cost_item";
    public static String ID_COLUMN_NAME = "id";
    public static String NAME_COLUMN_NAME = "name";
    public static String WALLET_COLUMN_NAME = "wallet";
    public static String EXPENDITURE_COLUMN_NAME = "expenditure";
    public static String TIME_COLUMN_NAME = "time";
    public static String AMOUNT_COLUMN_NAME = "amount";

    private Integer id;
    private String name;
    private Integer wallet;
    private Integer expenditure;
    private Time time;
    private BigDecimal amount;

    public CostItem() {
    }

    public boolean isComplete() {
        return amount != null && BigDecimal.ZERO.compareTo(amount) != 1;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getWallet() {
        return wallet;
    }

    public void setWallet(Integer wallet) {
        this.wallet = wallet;
    }

    public Integer getExpenditure() {
        return expenditure;
    }

    public void setExpenditure(Integer expenditure) {
        this.expenditure = expenditure;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setAmount(String amount) {
        this.amount = amount != null && amount.length() != 0 ? new BigDecimal(amount) : null;
    }
}