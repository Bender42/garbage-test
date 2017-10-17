package com.example.garbage.wallet_operation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.garbage.IWallet;
import com.example.garbage.MainActivity;
import com.example.garbage.R;
import com.example.garbage.wallet.SelectWalletActivity;
import com.example.garbage.wallet.WalletsDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

public class EditWalletOperationActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    public static int SELECT_FROM_WALLET_ACTIVITY = 1;
    public static int SELECT_TO_WALLET_ACTIVITY = 2;

    private SparseArray<IWallet> wallets = new SparseArray<>();
    private WalletOperation walletOperation = new WalletOperation();
    private WalletOperation resultWalletOperation = new WalletOperation();

    private TextView tvFromWalletName;
    private TextView tvToWalletName;
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

        WalletsDao walletsDao = new WalletsDao(this);
        wallets = walletsDao.getAllWallets();

        tvFromWalletName = findViewById(R.id.tv_edit_wallet_oper_from_wallet_name);
        tvToWalletName = findViewById(R.id.tv_edit_wallet_oper_to_wallet_name);
        TextView tvToWalletCurrency = findViewById(R.id.tv_edit_wallet_oper_to_wallet_currency);
        etWalletOperationName = findViewById(R.id.et_edit_wallet_oper_name);
        etWalletOperationAmount = findViewById(R.id.et_edit_wallet_oper_amount);
        tvWalletOperationDate = findViewById(R.id.tv_edit_wallet_oper_date);
        tvWalletOperationTime = findViewById(R.id.tv_edit_wallet_oper_time);

        tvFromWalletName.setText(wallets.get(walletOperation.getFromWalletId()).getName());
        tvToWalletName.setText(wallets.get(walletOperation.getToWalletId()).getName());
        tvToWalletCurrency.setText(wallets.get(walletOperation.getToWalletId()).getCurrency());
        etWalletOperationName.setText(walletOperation.getName());
        etWalletOperationAmount.setText(String.format("%s", walletOperation.getAmount()));

        ImageButton ibFromWalletEdit = findViewById(R.id.ib_edit_wallet_oper_from_wallet_edit);
        ibFromWalletEdit.setOnClickListener(getEditFromWalletOnClickListener());

        ImageButton ibToWalletEdit = findViewById(R.id.ib_edit_wallet_oper_to_wallet_edit);
        ibToWalletEdit.setOnClickListener(getEditToWalletOnClickListener());

        Button bAddWalletOperation = findViewById(R.id.b_edit_wallet_oper);
        bAddWalletOperation.setOnClickListener(getUpdateWalletOperationOnClickListener(this));

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (SELECT_FROM_WALLET_ACTIVITY == requestCode && RESULT_OK == resultCode) {
            int fromWalletId = data.getIntExtra("walletId", walletOperation.getFromWalletId());
            resultWalletOperation.setFromWalletId(fromWalletId);
            tvFromWalletName.setText(wallets.get(fromWalletId).getName());
        } else if (SELECT_TO_WALLET_ACTIVITY == requestCode && RESULT_OK == resultCode) {
            int toWalletId = data.getIntExtra("walletId", walletOperation.getToWalletId());
            resultWalletOperation.setToWalletId(toWalletId);
            tvToWalletName.setText(wallets.get(toWalletId).getName());
        }
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

    private View.OnClickListener getEditFromWalletOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO добавить активность с выбором статей дохода
                Intent intent = new Intent(v.getContext(), SelectWalletActivity.class);
                startActivityForResult(intent, SELECT_FROM_WALLET_ACTIVITY);
            }
        };
    }

    private View.OnClickListener getEditToWalletOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), SelectWalletActivity.class);
                startActivityForResult(intent, SELECT_TO_WALLET_ACTIVITY);
            }
        };
    }


    private View.OnClickListener getUpdateWalletOperationOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resultWalletOperation.setAmount(etWalletOperationAmount.getText().toString());
                resultWalletOperation.setName(etWalletOperationName.getText().toString());
                resultWalletOperation.setTime(calendar.getTimeInMillis());
                if (resultWalletOperation.isComplete()) {
                    if (!wallets.get(resultWalletOperation.getFromWalletId()).isIncomeItem() &&
                            (resultWalletOperation.getAmount().compareTo(wallets.get(resultWalletOperation.getFromWalletId()).getAmount()) == 1)) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.not_enough_amount_on_wallet), Toast.LENGTH_SHORT).show();
                    } else if (!Objects.equals(
                            wallets.get(resultWalletOperation.getFromWalletId()).getCurrency(),
                            wallets.get(resultWalletOperation.getToWalletId()).getCurrency())) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.not_equal_currencies), Toast.LENGTH_SHORT).show();
                    } else if (!Objects.equals(
                            wallets.get(walletOperation.getToWalletId()).getCurrency(),
                            wallets.get(resultWalletOperation.getToWalletId()).getCurrency())) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.cant_change_currencies), Toast.LENGTH_SHORT).show();
                    } else {
                        if (walletOperation.update(context, resultWalletOperation, wallets)) {
                            startActivity(new Intent(v.getContext(), MainActivity.class));
                        }
                    }
                } else {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.input_not_all_required_fields), Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void initFromToWallets() {
        Intent intent = getIntent();
        walletOperation = (WalletOperation) intent.getSerializableExtra("walletOperation");
        resultWalletOperation.update(walletOperation);
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