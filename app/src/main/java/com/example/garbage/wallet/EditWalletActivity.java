package com.example.garbage.wallet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garbage.R;

public class EditWalletActivity extends AppCompatActivity {

    private Wallet wallet = new Wallet();

    private EditText etWalletName;
    private EditText etWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_wallet);

        etWalletName = (EditText) findViewById(R.id.et_edit_wallet_name);
        etWalletAmount = (EditText) findViewById(R.id.et_edit_wallet_amount);
        TextView tvWalletCurrency = (TextView) findViewById(R.id.tv_edit_wallet_currency);

        Intent intent = getIntent();
        int walletId = intent.getIntExtra("walletId", 0);
        if (walletId == 0) {
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
        wallet = new Wallet(walletId, this);
        etWalletName.setText(wallet.getName());
        etWalletAmount.setText(String.valueOf(wallet.getAmount()));
        tvWalletCurrency.setText(wallet.getCurrency());

        ImageButton ibEditWallet = (ImageButton) findViewById(R.id.ib_edit_wallet);
        ibEditWallet.setOnClickListener(getEditOnClickListener(this));
    }

    private View.OnClickListener getEditOnClickListener(final Context context) {
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
}