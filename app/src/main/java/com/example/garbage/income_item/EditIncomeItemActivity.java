package com.example.garbage.income_item;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garbage.IWallet;
import com.example.garbage.IWalletOperation;
import com.example.garbage.R;
import com.example.garbage.tools.GarbageTools;
import com.example.garbage.wallet.WalletsDao;
import com.example.garbage.wallet_operation.WalletOperation;
import com.example.garbage.wallet_operation.WalletOperationAdapter;
import com.example.garbage.wallet_operation.WalletOperationsDao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditIncomeItemActivity extends AppCompatActivity {

    private IncomeItem incomeItem;
    private SparseArray<IWallet> wallets = new SparseArray<>();
    private List<IWalletOperation> operations = new LinkedList<>();

    private WalletOperationsDao walletOperationsDao;

    private EditText etIncomeItemName;

    private RecyclerView rvWalletOperations;
    private RecyclerView.Adapter adapterWalletOperations;

    private TextView tvFromDate;
    private TextView tvToDate;
    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;
    private Calendar fromDateCalendar;
    private Calendar toDateCalendar;
    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_income_item);

        dateFormat = new SimpleDateFormat(getString(R.string.date_label_format), Locale.getDefault());

        initFromToDate();

        Intent intent = getIntent();
        int incomeItemId = intent.getIntExtra("incomeItemId", 0);
        incomeItem = new IncomeItem(incomeItemId, this);

        WalletsDao walletsDao = new WalletsDao(this);
        wallets = walletsDao.getAllWallets();

        walletOperationsDao = new WalletOperationsDao(this);

        operations = getSortOperations(walletOperationsDao.getWalletOperations(
                incomeItem.getId(),
                fromDateCalendar.getTimeInMillis(),
                toDateCalendar.getTimeInMillis()));

        etIncomeItemName = (EditText) findViewById(R.id.et_edit_income_item_name);
        etIncomeItemName.setText(incomeItem.getName());

        TextView tvIncomeItemCurrency = (TextView) findViewById(R.id.tv_edit_income_item_currency);
        tvIncomeItemCurrency.setText(incomeItem.getCurrency());

        Button bUpdateIncomeItem = (Button) findViewById(R.id.b_update_income_item);
        bUpdateIncomeItem.setOnClickListener(getUpdateOnClickListener(this));

        Button bDeleteIncomeItem = (Button) findViewById(R.id.b_delete_income_item);
        bDeleteIncomeItem.setOnClickListener(getDeleteOnClickListener(this));

        initRecyclerViewWalletOperation();
    }

    private void initFromToDate() {
        fromDateCalendar = GarbageTools.getCurrentMonthStartCalendar();
        fromDatePickerDialog = new DatePickerDialog(
                this,
                getOnFromDateSetListener(),
                fromDateCalendar.get(Calendar.YEAR),
                fromDateCalendar.get(Calendar.MONTH),
                fromDateCalendar.get(Calendar.DAY_OF_MONTH)
        );
        tvFromDate = (TextView) findViewById(R.id.tv_edit_income_item_from_date);
        tvFromDate.setText(dateFormat.format(fromDateCalendar.getTime()));
        tvFromDate.setOnClickListener(getSelectFromDateOnClickListener());

        toDateCalendar = Calendar.getInstance();
        toDatePickerDialog = new DatePickerDialog(
                this,
                getOnToDateSetListener(),
                toDateCalendar.get(Calendar.YEAR),
                toDateCalendar.get(Calendar.MONTH),
                toDateCalendar.get(Calendar.DAY_OF_MONTH)
        );
        tvToDate = (TextView) findViewById(R.id.tv_edit_income_item_to_date);
        tvToDate.setText(dateFormat.format(toDateCalendar.getTime()));
        tvToDate.setOnClickListener(getSelectToDateOnClickListener());

        ImageButton ibRefreshOperation = (ImageButton) findViewById(R.id.ib_income_item_refresh_from_to);
        ibRefreshOperation.setOnClickListener(getOnRefreshClickListener(this));
    }

    private void initRecyclerViewWalletOperation() {
        rvWalletOperations = (RecyclerView) findViewById(R.id.rv_income_item_operation_list);
        rvWalletOperations.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManagerWalletOperations = new LinearLayoutManager(this);
        rvWalletOperations.setLayoutManager(layoutManagerWalletOperations);

        adapterWalletOperations = new WalletOperationAdapter(operations, incomeItem, wallets, null);
        rvWalletOperations.setAdapter(adapterWalletOperations);
    }

    private View.OnClickListener getUpdateOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                incomeItem.setName(etIncomeItemName.getText().toString());
                if (incomeItem.isComplete()) {
                    if (incomeItem.update(context) == 1) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                }
            }
        };
    }

    private View.OnClickListener getDeleteOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (incomeItem.archive(context) == 1) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            }
        };
    }

    private List<IWalletOperation> getSortOperations(List<WalletOperation> walletOperations) {
        List<IWalletOperation> operations = new LinkedList<>();
        operations.addAll(walletOperations);
        Collections.sort(operations, new Comparator<IWalletOperation>() {
            @Override
            public int compare(IWalletOperation o1, IWalletOperation o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        return operations;
    }

    private DatePickerDialog.OnDateSetListener getOnFromDateSetListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                fromDateCalendar.set(year, month, dayOfMonth);
                tvFromDate.setText(dateFormat.format(fromDateCalendar.getTime()));
            }
        };
    }

    private View.OnClickListener getSelectFromDateOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fromDatePickerDialog.show();
            }
        };
    }

    private DatePickerDialog.OnDateSetListener getOnToDateSetListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                toDateCalendar.set(year, month, dayOfMonth, 23, 59, 59);
                tvToDate.setText(dateFormat.format(toDateCalendar.getTime()));
            }
        };
    }

    private View.OnClickListener getSelectToDateOnClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toDatePickerDialog.show();
            }
        };
    }

    private View.OnClickListener getOnRefreshClickListener(Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                operations = getSortOperations(
                        walletOperationsDao.getWalletOperations(
                                incomeItem.getId(),
                                fromDateCalendar.getTimeInMillis(),
                                toDateCalendar.getTimeInMillis()));
                adapterWalletOperations = new WalletOperationAdapter(operations, incomeItem, wallets, null);
                rvWalletOperations.setAdapter(adapterWalletOperations);
            }
        };
    }
}
