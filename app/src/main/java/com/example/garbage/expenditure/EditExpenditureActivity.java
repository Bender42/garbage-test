package com.example.garbage.expenditure;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.garbage.R;

public class EditExpenditureActivity extends AppCompatActivity {

    private Expenditure expenditure;

    private EditText etExpenditureName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expenditure);

        Intent intent = getIntent();
        int expenditureId = intent.getIntExtra("expenditureId", 0);
        if (expenditureId == 0) {
            //TODO Не работает, приложение падает
            setResult(RESULT_CANCELED, new Intent());
            finish();
        }
        expenditure = new Expenditure(expenditureId, this);

        etExpenditureName = (EditText) findViewById(R.id.et_edit_expenditure_name);
        etExpenditureName.setText(expenditure.getName());

        Button bUpdateExpenditure = (Button) findViewById(R.id.b_update_expenditure);
        bUpdateExpenditure.setOnClickListener(getUpdateOnClickListener(this));

        Button bDeleteExpenditure = (Button) findViewById(R.id.b_delete_expenditure);
        bDeleteExpenditure.setOnClickListener(getDeleteOnClickListener(this));
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
                if (expenditure.delete(context) == 1) {
                    setResult(RESULT_OK, new Intent());
                    finish();
                }
            }
        };
    }
}