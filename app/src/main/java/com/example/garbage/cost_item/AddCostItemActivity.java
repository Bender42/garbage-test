package com.example.garbage.cost_item;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.garbage.R;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.wallet.Wallet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddCostItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    private CostItem costItem = new CostItem();

    private EditText etCostItemName;
    private EditText etCostItemAmount;
    private TextView tvCostItemDate;

    private DatePickerDialog costItemDatePickedDialog;

    Wallet selectedWallet;
    Expenditure selectedExpenditure;
    Long costItemDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cost_item);

        initWalletAndExpenditure();

        TextView tvWalletName = (TextView) findViewById(R.id.tv_cost_item_wallet_name);
        TextView tvWalletCurrency = (TextView) findViewById(R.id.tv_cost_item_wallet_currency);
        TextView tvExpenditureName = (TextView) findViewById(R.id.tv_cost_item_expenditure_name);
        etCostItemName = (EditText) findViewById(R.id.et_cost_item_name);
        etCostItemAmount = (EditText) findViewById(R.id.et_cost_item_amount);
        tvCostItemDate = (TextView) findViewById(R.id.tv_cost_item_date);

        tvWalletName.setText(selectedWallet.getName());
        tvWalletCurrency.setText(selectedWallet.getCurrency());
        tvExpenditureName.setText(selectedExpenditure.getName());

        Button bAddCostItem = (Button) findViewById(R.id.b_add_cost_item);
        bAddCostItem.setOnClickListener(getAddCostItemOnClickListener(this));

        ImageButton ibCostItemDate = (ImageButton) findViewById(R.id.ib_cost_item_date);
        ibCostItemDate.setOnClickListener(getSelectDateOnClickListener());

        Calendar calendar = Calendar.getInstance();
        costItemDate = calendar.getTimeInMillis();
        costItemDatePickedDialog = new DatePickerDialog(
                this,
                AddCostItemActivity.this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyyг.");
        tvCostItemDate.setText(format.format(calendar.getTime()));
    }

    private View.OnClickListener getSelectDateOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                costItemDatePickedDialog.show();
            }
        };
    }

    private View.OnClickListener getAddCostItemOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                costItem.setName(etCostItemName.getText().toString());
                costItem.setWallet(selectedWallet.getId());
                costItem.setExpenditure(selectedExpenditure.getId());
                costItem.setTime(costItemDate);
                costItem.setAmount(etCostItemAmount.getText().toString());
                if (costItem.isComplete()) {
                    if (costItem.getAmount().compareTo(selectedWallet.getAmount()) == 1) {
                        Toast.makeText(v.getContext(), "Сумма списания не может быть больше остатка на кошельке", Toast.LENGTH_LONG).show();
                    } else {
                        //TODO Добавить потоки
                        if (costItem.post(context)) {
                            selectedWallet.setAmount(selectedWallet.getAmount().subtract(costItem.getAmount()));
                            if (selectedWallet.update(context) == 1) {
                                setResult(RESULT_OK, new Intent());
                                finish();
                            }
                        }
                    }
                }
            }
        };
    }

    private void initWalletAndExpenditure() {
        Intent intent = getIntent();
        selectedWallet = (Wallet) intent.getSerializableExtra("wallet");
        selectedExpenditure = (Expenditure) intent.getSerializableExtra("expenditure");
        if (selectedWallet.getId() == 0 || selectedWallet.getName() == null ||
                selectedExpenditure.getId() == 0 || selectedExpenditure.getName() == null) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyyг.");
        costItemDate = calendar.getTimeInMillis();
        tvCostItemDate.setText(format.format(calendar.getTime()));
    }
}