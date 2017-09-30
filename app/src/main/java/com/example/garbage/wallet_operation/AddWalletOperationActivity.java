package com.example.garbage.wallet_operation;

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
import com.example.garbage.wallet.Wallet;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class AddWalletOperationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private WalletOperation walletOperation = new WalletOperation();
    Wallet fromWallet;
    Wallet toWallet;

    private EditText etWalletOperationName;
    private EditText etWalletOperationAmount;
    private TextView tvWalletOperationDate;
    private TextView tvWalletOperationTime;

    private DatePickerDialog walletOperationDatePickedDialog;
    private TimePickerDialog walletOperationTimePickedDialog;

    Long walletOperationDateTime;
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet_operation);

        initFromToWallets();

        TextView tvFromWalletName = (TextView) findViewById(R.id.tv_wallet_oper_from_wallet_name);
        TextView tvToWalletName = (TextView) findViewById(R.id.tv_wallet_oper_to_wallet_name);
        TextView tvToWalletCurrency = (TextView) findViewById(R.id.tv_wallet_oper_to_wallet_currency);
        etWalletOperationName = (EditText) findViewById(R.id.et_wallet_oper_name);
        etWalletOperationAmount = (EditText) findViewById(R.id.et_wallet_oper_amount);
        tvWalletOperationDate = (TextView) findViewById(R.id.tv_wallet_oper_date);
        tvWalletOperationTime = (TextView) findViewById(R.id.tv_wallet_oper_time);

        if (fromWallet != null) {
            tvFromWalletName.setText(fromWallet.getName());
        }
        tvToWalletName.setText(toWallet.getName());
        tvToWalletCurrency.setText(toWallet.getCurrency());

        Button bAddWalletOperation = (Button) findViewById(R.id.b_add_wallet_oper);
        bAddWalletOperation.setOnClickListener(getWalletOperationOnClickListener(this));

        calendar = Calendar.getInstance();
        walletOperationDateTime = calendar.getTimeInMillis();

        walletOperationDatePickedDialog = new DatePickerDialog(
                this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        SimpleDateFormat formatDate = new SimpleDateFormat(getString(R.string.date_label_format));
        tvWalletOperationDate.setText(formatDate.format(calendar.getTime()));
        tvWalletOperationDate.setOnClickListener(getSelectDateOnClickListener());

        walletOperationTimePickedDialog = new TimePickerDialog(
                this, this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        SimpleDateFormat formatTime = new SimpleDateFormat(getString(R.string.time_label_format));
        tvWalletOperationTime.setText(formatTime.format(calendar.getTime()));
        tvWalletOperationTime.setOnClickListener(getSelectTimeOnClickListener());
    }

    private View.OnClickListener getSelectDateOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletOperationDatePickedDialog.show();
            }
        };
    }

    private View.OnClickListener getSelectTimeOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                walletOperationTimePickedDialog.show();
            }
        };
    }

    private View.OnClickListener getWalletOperationOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fromWallet != null) {
                    walletOperation.setFromWallet(fromWallet.getId());
                }
                walletOperation.setToWallet(toWallet.getId());
                walletOperation.setAmount(etWalletOperationAmount.getText().toString());
                walletOperation.setName(etWalletOperationName.getText().toString());
                walletOperation.setTime(walletOperationDateTime);
                if (walletOperation.isComplete()) {
                    if (fromWallet != null && walletOperation.getAmount().compareTo(fromWallet.getAmount()) == 1) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.not_enough_amount_on_wallet), Toast.LENGTH_LONG).show();
                    } else {
                        if (walletOperation.post(context, fromWallet, toWallet)) {
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

    private void initFromToWallets() {
        Intent intent = getIntent();
        fromWallet = (Wallet) intent.getSerializableExtra("fromWallet");
        toWallet = (Wallet) intent.getSerializableExtra("toWallet");
        if (toWallet == null) {
            //TODO Не работает, приложение падает
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        SimpleDateFormat format = new SimpleDateFormat(getString(R.string.date_label_format));
        walletOperationDateTime = calendar.getTimeInMillis();
        tvWalletOperationDate.setText(format.format(calendar.getTime()));
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
        SimpleDateFormat formatTime = new SimpleDateFormat(getString(R.string.time_label_format));
        walletOperationDateTime = calendar.getTimeInMillis();
        tvWalletOperationTime.setText(formatTime.format(calendar.getTime()));
    }
}
