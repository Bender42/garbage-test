package com.example.garbage.wallet;

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

import com.example.garbage.IWalletOperation;
import com.example.garbage.R;
import com.example.garbage.cost_item.CostItem;
import com.example.garbage.cost_item.CostItemDao;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.expenditure.ExpenditureDao;
import com.example.garbage.wallet_operation.WalletOperation;
import com.example.garbage.wallet_operation.WalletOperationAdapter;
import com.example.garbage.wallet_operation.WalletOperationsDao;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EditWalletActivity extends AppCompatActivity {

    //public static int ADD_WALLET_OPERATION_ACTIVITY_CODE = 1;

    private Wallet wallet;
    private Map<Integer, Wallet> wallets = new LinkedHashMap<>();
    private Map<Integer, Expenditure> expenditures = new LinkedHashMap<>();
    private List<IWalletOperation> operations = new LinkedList<>();

    private WalletOperationsDao walletOperationsDao;
    private CostItemDao costItemDao;

    private EditText etWalletName;
    private EditText etWalletAmount;

    private RecyclerView rvWalletOperations;
    private RecyclerView.Adapter adapterWalletOperations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet);

        Intent intent = getIntent();
        int walletId = intent.getIntExtra("walletId", 0);
        if (walletId == 0) {
            //TODO Не работает, приложение падает
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
        wallet = new Wallet(walletId, this);

        WalletsDao walletsDao = new WalletsDao(this);
        wallets = walletsDao.getAllWallets();

        ExpenditureDao expenditureDao = new ExpenditureDao(this);
        expenditures = expenditureDao.getExpenditures();

        walletOperationsDao = new WalletOperationsDao(this);
        costItemDao = new CostItemDao(this);

        operations = getSortOperations(
                walletOperationsDao.getWalletOperations(walletId),
                costItemDao.getCostItems(wallet.getId())
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

        /*Button bAddWallet = (Button) findViewById(R.id.b_add_wallet);
        bAddWallet.setOnClickListener(getAddOnClickListener(this));*/

        initRecyclerViewWalletOperation();
    }

    /*@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            if (ADD_WALLET_OPERATION_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
                refreshDataAndAdapter();
            }
        }
    }*/

    /*private void refreshDataAndAdapter() {
        wallet = new Wallet(wallet.getId(), this);
        etWalletAmount.setText(String.valueOf(wallet.getAmount()));
        operations = getSortOperations(
                walletOperationsDao.getWalletOperations(wallet.getId()),
                costItemDao.getCostItems(wallet.getId())
        );
        adapterWalletOperations = new WalletOperationAdapter(operations, wallet, wallets, expenditures);
        rvWalletOperations.setAdapter(adapterWalletOperations);
    }*/

    private void initRecyclerViewWalletOperation() {
        rvWalletOperations = (RecyclerView) findViewById(R.id.rv_wallet_wallet_operation_list);
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

    /*private View.OnClickListener getAddOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddWalletOperationActivity.class);
                intent.putExtra("toWallet", wallet);
                startActivityForResult(intent, ADD_WALLET_OPERATION_ACTIVITY_CODE);
            }
        };
    }*/
}