package com.example.garbage.income_item;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.garbage.IWallet;
import com.example.garbage.IWalletOperation;
import com.example.garbage.R;
import com.example.garbage.tools.GarbageTools;
import com.example.garbage.wallet.WalletsDao;
import com.example.garbage.wallet_operation.WalletOperation;
import com.example.garbage.wallet_operation.WalletOperationAdapter;
import com.example.garbage.wallet_operation.WalletOperationsDao;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EditIncomeItemActivity extends AppCompatActivity {

    private IncomeItem incomeItem;
    private Map<Integer, IWallet> wallets = new LinkedHashMap<>();
    private List<IWalletOperation> operations = new LinkedList<>();

    private WalletOperationsDao walletOperationsDao;

    private EditText etIncomeItemName;

    private RecyclerView rvWalletOperations;
    private RecyclerView.Adapter adapterWalletOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_income_item);

        Intent intent = getIntent();
        int incomeItemId = intent.getIntExtra("incomeItemId", 0);
        incomeItem = new IncomeItem(incomeItemId, this);

        WalletsDao walletsDao = new WalletsDao(this);
        wallets = walletsDao.getAllWallets();

        walletOperationsDao = new WalletOperationsDao(this);

        operations = getSortOperations(walletOperationsDao.getWalletOperations(
                incomeItemId,
                GarbageTools.getCurrentMonthStartTime(),
                null));

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

    private void initRecyclerViewWalletOperation() {
        rvWalletOperations = (RecyclerView) findViewById(R.id.rv_income_item_wallet_operation_list);
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
}
