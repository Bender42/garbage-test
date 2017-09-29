package com.example.garbage.cost_item;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.garbage.R;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.wallet.Wallet;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddCostItemActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private CostItem costItem = new CostItem();
    Wallet selectedWallet;
    Expenditure selectedExpenditure;

    private EditText etCostItemName;
    private EditText etCostItemAmount;
    private TextView tvCostItemDate;
    private TextView tvCostItemTime;

    private DatePickerDialog costItemDatePickedDialog;
    private TimePickerDialog costItemTimePickedDialog;

    Long costItemDateTime;
    Calendar calendar;

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
        tvCostItemTime = (TextView) findViewById(R.id.tv_cost_item_time);

        tvWalletName.setText(selectedWallet.getName());
        tvWalletCurrency.setText(selectedWallet.getCurrency());
        tvExpenditureName.setText(selectedExpenditure.getName());

        Button bAddCostItem = (Button) findViewById(R.id.b_add_cost_item);
        bAddCostItem.setOnClickListener(getAddCostItemOnClickListener(this));

        calendar = Calendar.getInstance();
        costItemDateTime = calendar.getTimeInMillis();

        costItemDatePickedDialog = new DatePickerDialog(
                this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        SimpleDateFormat formatDate = new SimpleDateFormat(getResources().getString(R.string.date_label_format));
        tvCostItemDate.setText(formatDate.format(calendar.getTime()));
        tvCostItemDate.setOnClickListener(getSelectDateOnClickListener());

        costItemTimePickedDialog = new TimePickerDialog(
                this, this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        SimpleDateFormat formatTime = new SimpleDateFormat(getResources().getString(R.string.time_label_format));
        tvCostItemTime.setText(formatTime.format(calendar.getTime()));
        tvCostItemTime.setOnClickListener(getSelectTimeOnClickListener());
    }

    private View.OnClickListener getSelectDateOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                costItemDatePickedDialog.show();
            }
        };
    }

    private View.OnClickListener getSelectTimeOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                costItemTimePickedDialog.show();
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
                costItem.setTime(costItemDateTime);
                costItem.setAmount(etCostItemAmount.getText().toString());
                if (costItem.isComplete()) {
                    if (costItem.getAmount().compareTo(selectedWallet.getAmount()) == 1) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.not_enough_amount_on_wallet), Toast.LENGTH_LONG).show();
                    } else {
                        if (costItem.post(context, selectedWallet)) {
                            setResult(RESULT_OK, new Intent());
                            finish();
                        }
                    }
                } else {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.input_not_all_required_fields), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void initWalletAndExpenditure() {
        Intent intent = getIntent();
        selectedWallet = (Wallet) intent.getSerializableExtra("wallet");
        selectedExpenditure = (Expenditure) intent.getSerializableExtra("expenditure");
        if (selectedWallet == null || selectedExpenditure == null) {
            //TODO Не работает, приложение падает
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat(getResources().getString(R.string.date_label_format));
        costItemDateTime = calendar.getTimeInMillis();
        tvCostItemDate.setText(format.format(calendar.getTime()));
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        calendar.set(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH),
                hourOfDay,
                minute
        );
        SimpleDateFormat formatTime = new SimpleDateFormat(getResources().getString(R.string.time_label_format));
        costItemDateTime = calendar.getTimeInMillis();
        tvCostItemTime.setText(formatTime.format(calendar.getTime()));
    }
}