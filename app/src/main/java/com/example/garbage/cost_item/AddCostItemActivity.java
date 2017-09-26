package com.example.garbage.cost_item;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.garbage.R;

public class AddCostItemActivity extends AppCompatActivity {

    private TextView tvWalletName;
    private TextView tvExpenditureName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_cost_item);

        Intent intent = getIntent();
        int walletId = intent.getIntExtra("walletId", 0);
        String walletName= intent.getStringExtra("walletName");
        int expenditureId = intent.getIntExtra("expenditureId", 0);
        String expenditureName= intent.getStringExtra("expenditureName");
        if (walletId == 0 || walletName == null ||
                expenditureId == 0 || expenditureName == null) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }

        tvWalletName = (TextView) findViewById(R.id.tv_cost_item_wallet_name);
        tvExpenditureName = (TextView) findViewById(R.id.tv_cost_item_expenditure_name);
        tvWalletName.setText(walletName);
        tvExpenditureName.setText(expenditureName);
    }
}