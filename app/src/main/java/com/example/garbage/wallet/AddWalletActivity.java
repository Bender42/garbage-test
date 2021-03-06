package com.example.garbage.wallet;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.example.garbage.R;

public class AddWalletActivity extends AppCompatActivity {

    private Wallet wallet = new Wallet();

    private EditText etWalletName;
    private EditText etWalletAmount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_wallet);
        etWalletName = (EditText) findViewById(R.id.et_add_wallet_name);
        etWalletAmount = (EditText) findViewById(R.id.et_add_wallet_amount);

        ImageButton ibAddWallet = (ImageButton) findViewById(R.id.ib_add_wallet);
        ibAddWallet.setOnClickListener(getAddOnClickListener(this));

        initSpinnerCurrency();
    }

    private View.OnClickListener getAddOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wallet.setName(etWalletName.getText().toString());
                wallet.setAmount(etWalletAmount.getText().toString());
                if (wallet.isComplete()) {
                    if (wallet.post(context)) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                }
            }
        };
    }

    private void initSpinnerCurrency() {
        Spinner spinnerCurrency = (Spinner) findViewById(R.id.s_wallet_currency);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.currency, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCurrency.setAdapter(adapter);
        spinnerCurrency.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                wallet.setCurrency(((AppCompatTextView) view).getText().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }
}