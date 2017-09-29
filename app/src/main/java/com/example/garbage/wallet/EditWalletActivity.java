package com.example.garbage.wallet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.garbage.R;

public class EditWalletActivity extends AppCompatActivity {

    private Wallet wallet;

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
            //TODO Не работает, приложение падает
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
        wallet = new Wallet(walletId, this);
        etWalletName.setText(wallet.getName());
        etWalletAmount.setText(String.valueOf(wallet.getAmount()));
        tvWalletCurrency.setText(wallet.getCurrency());

        Button bUpdateWallet = (Button) findViewById(R.id.b_update_wallet);
        bUpdateWallet.setOnClickListener(getUpdateOnClickListener(this));

        Button bDeleteWallet = (Button) findViewById(R.id.b_delete_wallet);
        bDeleteWallet.setOnClickListener(getDeleteOnClickListener(this));
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