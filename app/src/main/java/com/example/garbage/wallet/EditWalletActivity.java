package com.example.garbage.wallet;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garbage.IWallet;
import com.example.garbage.IWalletOperation;
import com.example.garbage.R;
import com.example.garbage.cost_item.CostItem;
import com.example.garbage.cost_item.CostItemDao;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.expenditure.ExpenditureDao;
import com.example.garbage.tools.GarbageTools;
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

public class EditWalletActivity extends AppCompatActivity {

    private Wallet wallet;
    private Map<Integer, IWallet> wallets = new LinkedHashMap<>();
    private Map<Integer, Expenditure> expenditures = new LinkedHashMap<>();
    private List<IWalletOperation> operations = new LinkedList<>();

    private WalletOperationsDao walletOperationsDao;
    private CostItemDao costItemDao;

    private EditText etWalletName;
    private EditText etWalletAmount;
    private TextView tvFromDate;
    private TextView tvToDate;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog toDatePickerDialog;

    private RecyclerView rvWalletOperations;
    private RecyclerView.Adapter adapterWalletOperations;

    private Calendar fromDateCalendar;
    private Calendar toDateCalendar;

    private SimpleDateFormat dateFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet);

        dateFormat = new SimpleDateFormat(getString(R.string.date_label_format), Locale.getDefault());

        initFromToDate();

        Intent intent = getIntent();
        int walletId = intent.getIntExtra("walletId", 0);
        wallet = new Wallet(walletId, this);

        WalletsDao walletsDao = new WalletsDao(this);
        wallets = walletsDao.getAllWallets();

        ExpenditureDao expenditureDao = new ExpenditureDao(this);
        expenditures = expenditureDao.getAllExpenditures();

        walletOperationsDao = new WalletOperationsDao(this);
        costItemDao = new CostItemDao(this);

        operations = getSortOperations(
                walletOperationsDao.getWalletOperations(
                        wallet.getId(),
                        fromDateCalendar.getTimeInMillis(),
                        toDateCalendar.getTimeInMillis()),
                costItemDao.getCostItems(
                        wallet.getId(),
                        fromDateCalendar.getTimeInMillis(),
                        toDateCalendar.getTimeInMillis())
        );

        etWalletName = (EditText) findViewById(R.id.et_edit_wallet_name);
        etWalletName.setText(wallet.getName());

        etWalletAmount = (EditText) findViewById(R.id.et_edit_wallet_amount);
        etWalletAmount.setText(String.valueOf(wallet.getAmount()));

        TextView tvWalletCurrency = (TextView) findViewById(R.id.tv_edit_wallet_currency);
        tvWalletCurrency.setText(wallet.getCurrency());

        Button bUpdateWallet = (Button) findViewById(R.id.b_update_wallet);
        bUpdateWallet.setOnClickListener(getUpdateOnClickListener(this));

        Button bDeleteWallet = (Button) findViewById(R.id.b_delete_wallet);
        bDeleteWallet.setOnClickListener(getDeleteOnClickListener(this));

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
        tvFromDate = (TextView) findViewById(R.id.tv_edit_wallet_from_date);
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
        tvToDate = (TextView) findViewById(R.id.tv_edit_wallet_to_date);
        tvToDate.setText(dateFormat.format(toDateCalendar.getTime()));
        tvToDate.setOnClickListener(getSelectToDateOnClickListener());

        ImageButton ibRefreshOperation = (ImageButton) findViewById(R.id.ib_wallet_refresh_from_to);
        ibRefreshOperation.setOnClickListener(getOnRefreshClickListener(this));
    }

    private void initRecyclerViewWalletOperation() {
        rvWalletOperations = (RecyclerView) findViewById(R.id.rv_wallet_operation_list);
        rvWalletOperations.setHasFixedSize(false);

        RecyclerView.LayoutManager layoutManagerWalletOperations = new LinearLayoutManager(this);
        rvWalletOperations.setLayoutManager(layoutManagerWalletOperations);

        adapterWalletOperations = new WalletOperationAdapter(operations, wallet, wallets, expenditures);
        rvWalletOperations.setAdapter(adapterWalletOperations);
    }

    private List<IWalletOperation> getSortOperations(List<WalletOperation> walletOperations,
                                                     List<CostItem> costItems
    ) {
        List<IWalletOperation> operations = new LinkedList<>();
        operations.addAll(walletOperations);
        operations.addAll(costItems);
        Collections.sort(operations, new Comparator<IWalletOperation>() {
            @Override
            public int compare(IWalletOperation o1, IWalletOperation o2) {
                return o2.getTime().compareTo(o1.getTime());
            }
        });
        return operations;
    }

    private View.OnClickListener getUpdateOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet.setName(etWalletName.getText().toString());
                wallet.setAmount(etWalletAmount.getText().toString());
                if (wallet.isComplete()) {
                    if (wallet.update(context) == 1) {
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
                if (wallet.archive(context) == 1) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            }
        };
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

    private DatePickerDialog.OnDateSetListener getOnToDateSetListener() {
        return new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                toDateCalendar.set(year, month, dayOfMonth, 23, 59, 59);
                tvToDate.setText(dateFormat.format(toDateCalendar.getTime()));
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
                                wallet.getId(),
                                fromDateCalendar.getTimeInMillis(),
                                toDateCalendar.getTimeInMillis()),
                        costItemDao.getCostItems(
                                wallet.getId(),
                                fromDateCalendar.getTimeInMillis(),
                                toDateCalendar.getTimeInMillis())
                );
                adapterWalletOperations = new WalletOperationAdapter(operations, wallet, wallets, expenditures);
                rvWalletOperations.setAdapter(adapterWalletOperations);
            }
        };
    }
}