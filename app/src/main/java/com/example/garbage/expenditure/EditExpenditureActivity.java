package com.example.garbage.expenditure;

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
import com.example.garbage.R;
import com.example.garbage.cost_item.CostItem;
import com.example.garbage.cost_item.CostItemDao;
import com.example.garbage.cost_item.CostItemsAdapter;
import com.example.garbage.tools.GarbageTools;
import com.example.garbage.wallet.WalletsDao;
import com.example.garbage.wallet_operation.WalletOperationAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class EditExpenditureActivity extends AppCompatActivity {

    private Expenditure expenditure;
    private Map<Integer, IWallet> wallets = new LinkedHashMap<>();
    private List<CostItem> costItems = new LinkedList<>();

    private WalletsDao walletsDao;
    private CostItemDao costItemDao;

    private EditText etExpenditureName;

    private RecyclerView rvCostItems;
    private RecyclerView.Adapter adapterCostItems;
    private RecyclerView.LayoutManager layoutManagerCostItems;

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
        setContentView(R.layout.activity_edit_expenditure);

        dateFormat = new SimpleDateFormat(getString(R.string.date_label_format), Locale.getDefault());

        initFromToDate();

        Intent intent = getIntent();
        int expenditureId = intent.getIntExtra("expenditureId", 0);
        expenditure = new Expenditure(expenditureId, this);

        walletsDao = new WalletsDao(this);
        wallets = walletsDao.getAllWallets();

        costItemDao = new CostItemDao(this);
        costItems = costItemDao.getCostItems(
                expenditure.getId(),
                wallets,
                fromDateCalendar.getTimeInMillis(),
                toDateCalendar.getTimeInMillis());

        etExpenditureName = (EditText) findViewById(R.id.et_edit_expenditure_name);
        etExpenditureName.setText(expenditure.getName());

        Button bUpdateExpenditure = (Button) findViewById(R.id.b_update_expenditure);
        bUpdateExpenditure.setOnClickListener(getUpdateOnClickListener(this));

        Button bDeleteExpenditure = (Button) findViewById(R.id.b_delete_expenditure);
        bDeleteExpenditure.setOnClickListener(getDeleteOnClickListener(this));

        initRecyclerViewCostItems();
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
        tvFromDate = (TextView) findViewById(R.id.tv_edit_expenditure_from_date);
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
        tvToDate = (TextView) findViewById(R.id.tv_edit_expenditure_to_date);
        tvToDate.setText(dateFormat.format(toDateCalendar.getTime()));
        tvToDate.setOnClickListener(getSelectToDateOnClickListener());

        ImageButton ibRefreshOperation = (ImageButton) findViewById(R.id.ib_expenditure_refresh_from_to);
        ibRefreshOperation.setOnClickListener(getOnRefreshClickListener(this));
    }

    private void initRecyclerViewCostItems() {
        rvCostItems = (RecyclerView) findViewById(R.id.rv_expenditure_cost_item_list);
        rvCostItems.setHasFixedSize(false);//TODO Нужно вернуться к этой хрени и подумать

        layoutManagerCostItems = new LinearLayoutManager(this);
        rvCostItems.setLayoutManager(layoutManagerCostItems);

        adapterCostItems = new CostItemsAdapter(costItems, wallets);
        rvCostItems.setAdapter(adapterCostItems);
    }

    private View.OnClickListener getUpdateOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenditure.setName(etExpenditureName.getText().toString());
                if (expenditure.isComplete()) {
                    if (expenditure.update(context) == 1) {
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
                if (expenditure.archive(context) == 1) {
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
                costItems = costItemDao.getCostItems(
                        expenditure.getId(),
                        wallets,
                        fromDateCalendar.getTimeInMillis(),
                        toDateCalendar.getTimeInMillis());
                adapterCostItems = new CostItemsAdapter(costItems, wallets);
                rvCostItems.setAdapter(adapterCostItems);
            }
        };
    }
}