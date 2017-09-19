package com.example.garbage;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.garbage.wallet.AddWalletActivity;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView textViewGoPref;
    private Button buttonGoPref;
    private ImageButton buttonAddWallet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewGoPref = (TextView) findViewById(R.id.textViewGoPref);
        buttonGoPref = (Button) findViewById(R.id.buttonGoPref);
        buttonGoPref.setOnClickListener(this);

        Button toShortcutOneButton = (Button) findViewById(R.id.buttonShortcutOne);
        toShortcutOneButton.setOnClickListener(this);
        Button changeRankButton = (Button) findViewById(R.id.buttonChangeRank);
        changeRankButton.setOnClickListener(this);

        createShortcuts();
        initButtonAddWallet();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonShortcutOne:
                Intent intentShortcut = new Intent(this, ShortcutOneActivity.class);
                startActivity(intentShortcut);
                break;
            case R.id.buttonChangeRank:
                ShortcutInfo webShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_two_web")
                        .setRank(1)
                        .build();
                ShortcutInfo threeShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_three")
                        .setRank(0)
                        .build();
                ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
                shortcutManager.updateShortcuts(Arrays.asList(webShortcutInfo, threeShortcutInfo));
                break;
            case R.id.buttonGoPref:
                Intent intentPref = new Intent(this, PreferencesActivity.class);
                startActivityForResult(intentPref, 1);
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (1 == requestCode) {
            String name = data.getStringExtra("name");
            textViewGoPref.setText("Data from preferences: " + name);
        }
    }

    private void createShortcuts() {
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(getResources().getColor(android.R.color.holo_blue_light, getTheme()));
        String label = "Динамический web";
        SpannableStringBuilder colouredLabel = new SpannableStringBuilder(label);
        colouredLabel.setSpan(colorSpan, 0, label.length(), Spanned.SPAN_INCLUSIVE_INCLUSIVE);

        ShortcutInfo webShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_two_web")
                .setShortLabel(colouredLabel)
                .setLongLabel("Открываем сайт google.com")
                .setIcon(Icon.createWithResource(this, R.drawable.shortcut_icon_2))
                .setIntent(new Intent(Intent.ACTION_VIEW, Uri.parse("https://google.com")))
                .build();

        ShortcutInfo threeShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_three")
                .setShortLabel("Динамический")
                .setLongLabel("Открываем динамически")
                .setIcon(Icon.createWithResource(this, R.drawable.shortcut_icon_3))
                .setIntents(new Intent[] {
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, ShortcutThreeActivity.class)})
                .build();

        ShortcutInfo sqlShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_sql")
                .setShortLabel("SQL")
                .setLongLabel("Открываем SQL")
                .setIcon(Icon.createWithResource(this, R.drawable.shortcut_icon_sql))
                .setIntents(new Intent[] {
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, SQLiteActivity.class)})
                .build();

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        shortcutManager.setDynamicShortcuts(Arrays.asList(webShortcutInfo, threeShortcutInfo, sqlShortcutInfo));
    }

    private void initButtonAddWallet() {
        buttonAddWallet = (ImageButton) findViewById(R.id.button_add_wallet);
        buttonAddWallet.setOnClickListener(getAddWalletListener());
    }

    public View.OnClickListener getAddWalletListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPref = new Intent(v.getContext(), AddWalletActivity.class);
                startActivityForResult(intentPref, 2);
            }
        };
    }
}
