package com.example.garbage;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PreferencesActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText editText;
    private Button buttonSave;
    private Button buttonLoad;
    private Button buttonGoBackPref;

    private final static String SAVED_TEXT = "saved_text";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        editText = (EditText) findViewById(R.id.editText);

        buttonSave = (Button) findViewById(R.id.bSavePref);
        buttonSave.setOnClickListener(this);

        buttonLoad = (Button) findViewById(R.id.bLoadPref);
        buttonLoad.setOnClickListener(this);

        buttonGoBackPref = (Button) findViewById(R.id.bGoBackPref);
        buttonGoBackPref.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bSavePref:
                saveText();
                break;
            case R.id.bLoadPref:
                loadText();
                break;
            case R.id.bGoBackPref:
                setResult();
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        setResult();
    }

    private void saveText(){
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        String result = editText.getText().toString();
        editor.putString(SAVED_TEXT, result);
        editor.apply();
        Toast.makeText(this, "Saved: " + result, Toast.LENGTH_SHORT).show();
    }

    private void loadText() {
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String savedText = preferences.getString(SAVED_TEXT, "No value");
        editText.setText(savedText);
        Toast.makeText(this, "Loaded: " + savedText, Toast.LENGTH_SHORT).show();
    }

    private void setResult() {
        Intent intent = new Intent();
        SharedPreferences preferences = getPreferences(MODE_PRIVATE);
        String savedText = preferences.getString(SAVED_TEXT, "No value");
        intent.putExtra("name", savedText);
        setResult(RESULT_OK, intent);
        finish();
    }
}
