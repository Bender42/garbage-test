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

import com.example.garbage.IWallet;
import com.example.garbage.MainActivity;
import com.example.garbage.R;
import com.example.garbage.wallet.Wallet;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EditWalletOperationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private WalletOperation walletOperation = new WalletOperation();
    private IWallet fromWallet;
    private Wallet toWallet;

    private EditText etWalletOperationName;
    private EditText etWalletOperationAmount;
    private TextView tvWalletOperationDate;
    private TextView tvWalletOperationTime;

    private DatePickerDialog walletOperationDatePickedDialog;
    private TimePickerDialog walletOperationTimePickedDialog;

    private Calendar calendar;

    private SimpleDateFormat dateFormat;
    private SimpleDateFormat timeFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet_operation);

        dateFormat = new SimpleDateFormat(getString(R.string.date_label_format), Locale.getDefault());
        timeFormat = new SimpleDateFormat(getString(R.string.time_label_format), Locale.getDefault());

        initFromToWallets();

        TextView tvFromWalletName = (TextView) findViewById(R.id.tv_edit_wallet_oper_from_wallet_name);
        TextView tvToWalletName = (TextView) findViewById(R.id.tv_edit_wallet_oper_to_wallet_name);
        TextView tvToWalletCurrency = (TextView) findViewById(R.id.tv_edit_wallet_oper_to_wallet_currency);
        etWalletOperationName = (EditText) findViewById(R.id.et_edit_wallet_oper_name);
        etWalletOperationAmount = (EditText) findViewById(R.id.et_edit_wallet_oper_amount);
        tvWalletOperationDate = (TextView) findViewById(R.id.tv_edit_wallet_oper_date);
        tvWalletOperationTime = (TextView) findViewById(R.id.tv_edit_wallet_oper_time);

        tvFromWalletName.setText(fromWallet.getName());
        tvToWalletName.setText(toWallet.getName());
        tvToWalletCurrency.setText(toWallet.getCurrency());
        etWalletOperationName.setText(walletOperation.getName());
        etWalletOperationAmount.setText(String.format("%s", walletOperation.getAmount()));

        Button bAddWalletOperation = (Button) findViewById(R.id.b_edit_wallet_oper);
        bAddWalletOperation.setOnClickListener(getWalletOperationOnClickListener(this));

        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(walletOperation.getTime());

        walletOperationDatePickedDialog = new DatePickerDialog(
                this, this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );
        tvWalletOperationDate.setText(dateFormat.format(calendar.getTime()));
        tvWalletOperationDate.setOnClickListener(getSelectDateOnClickListener());

        walletOperationTimePickedDialog = new TimePickerDialog(
                this, this,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
        );
        tvWalletOperationTime.setText(timeFormat.format(calendar.getTime()));
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
                    walletOperation.setFromWalletId(fromWallet.getId());
                }
                walletOperation.setToWalletId(toWallet.getId());
                walletOperation.setAmount(etWalletOperationAmount.getText().toString());
                walletOperation.setName(etWalletOperationName.getText().toString());
                walletOperation.setTime(calendar.getTimeInMillis());
                if (walletOperation.isComplete()) {
                    if (!fromWallet.isIncomeItem() && (walletOperation.getAmount().compareTo(fromWallet.getAmount()) == 1)) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.not_enough_amount_on_wallet), Toast.LENGTH_LONG).show();
                    } else {
                        /*if (walletOperation.post(context, fromWallet, toWallet)) {
                            startActivity(new Intent(context, MainActivity.class));
                        }*/
                        startActivity(new Intent(context, MainActivity.class));
                    }
                } else {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.input_not_all_required_fields), Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void initFromToWallets() {
        Intent intent = getIntent();
        walletOperation = (WalletOperation) intent.getSerializableExtra("walletOperation");
        fromWallet = new Wallet(walletOperation.getFromWalletId(), this);
        toWallet = new Wallet(walletOperation.getToWalletId(), this);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        calendar.set(year, month, dayOfMonth);
        tvWalletOperationDate.setText(dateFormat.format(calendar.getTime()));
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
        tvWalletOperationTime.setText(timeFormat.format(calendar.getTime()));
    }
}