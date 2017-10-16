package com.example.garbage.wallet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.garbage.R;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class SelectWalletActivity extends AppCompatActivity {

    private Map<Integer, Wallet> wallets = new LinkedHashMap<>();

    private RecyclerView rvWallets;
    private RecyclerView.Adapter adapterWallets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_wallet);

        WalletsDao walletsDao = new WalletsDao(this);
        wallets = walletsDao.getActiveWallets();

        rvWallets = findViewById(R.id.rv_wallet_list);
        rvWallets.setHasFixedSize(true);

        RecyclerView.LayoutManager layoutManagerWallets = new GridLayoutManager(this, 4);
        rvWallets.setLayoutManager(layoutManagerWallets);

        adapterWallets = new WalletAdapter(new ArrayList<>(wallets.values()), this);
        rvWallets.setAdapter(adapterWallets);
    }
}