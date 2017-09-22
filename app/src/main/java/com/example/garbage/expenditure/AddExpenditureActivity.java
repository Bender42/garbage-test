package com.example.garbage.expenditure;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.garbage.R;

public class AddExpenditureActivity extends AppCompatActivity {

    private Expenditure expenditure = new Expenditure();

    private EditText editTextExpenditureName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expenditure);

        editTextExpenditureName = (EditText) findViewById(R.id.edit_text_expenditure_name);

        ImageButton imageButtonPostExpenditure = (ImageButton) findViewById(R.id.image_button_post_expenditure);
        imageButtonPostExpenditure.setOnClickListener(getAddOnClickListener(this));
    }

    private View.OnClickListener getAddOnClickListener(final Context context) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expenditure.setName(editTextExpenditureName.getText().toString());
                if (expenditure.isComplete()) {
                    if (expenditure.post(context)) {
                        setResult(RESULT_OK, new Intent());
                        finish();
                    }
                }
            }
        };
    }
}