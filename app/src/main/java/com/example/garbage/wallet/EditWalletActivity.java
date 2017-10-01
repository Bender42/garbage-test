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

import com.example.garbage.R;
import com.example.garbage.wallet_operation.WalletOperation;
import com.example.garbage.wallet_operation.WalletOperationAdapter;
import com.example.garbage.wallet_operation.WalletOperationsDao;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EditWalletActivity extends AppCompatActivity {

    private Wallet wallet;
    private Map<Integer, Wallet> wallets = new LinkedHashMap<>();
    private List<WalletOperation> walletOperations = new LinkedList<>();

    private WalletsDao walletsDao;
    private WalletOperationsDao walletOperationsDao;

    private EditText etWalletName;
    private EditText etWalletAmount;

    private RecyclerView rvWalletOperations;
    private RecyclerView.Adapter adapterWalletOperations;
    private RecyclerView.LayoutManager layoutManagerWalletOperations;

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

        walletsDao = new WalletsDao(this);
        wallets = walletsDao.getWallets();

        walletOperationsDao = new WalletOperationsDao(this);
        walletOperations = walletOperationsDao.getWalletOperations(walletId);

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

    private void initRecyclerViewWalletOperation() {
        rvWalletOperations = (RecyclerView) findViewById(R.id.rv_wallet_wallet_operation_list);
        rvWalletOperations.setHasFixedSize(false);

        layoutManagerWalletOperations = new LinearLayoutManager(this);
        rvWalletOperations.setLayoutManager(layoutManagerWalletOperations);

        adapterWalletOperations = new WalletOperationAdapter(walletOperations, wallet, wallets);
        rvWalletOperations.setAdapter(adapterWalletOperations);
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
                if (wallet.delete(context) == 1) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            }
        };
    }
}