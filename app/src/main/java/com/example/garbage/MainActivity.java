package com.example.garbage;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.garbage.wallet.AddWalletActivity;
import com.example.garbage.wallet.Wallet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static int ADD_WALLET_ACTIVITY_CODE = 2;

    private TextView textViewGoPref;
    private Button buttonGoPref;
    private ImageButton buttonAddWallet;

    private LinearLayout layoutWallets;

    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new SQLiteHelper(this);

        textViewGoPref = (TextView) findViewById(R.id.textViewGoPref);
        buttonGoPref = (Button) findViewById(R.id.buttonGoPref);
        buttonGoPref.setOnClickListener(this);

        Button toShortcutOneButton = (Button) findViewById(R.id.buttonShortcutOne);
        toShortcutOneButton.setOnClickListener(this);
        Button changeRankButton = (Button) findViewById(R.id.buttonChangeRank);
        changeRankButton.setOnClickListener(this);

        createShortcuts();

        initLayoutWallets();
        refreshWallets();
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
        if (1 == requestCode && RESULT_OK == resultCode) {
            String name = data.getStringExtra("name");
            textViewGoPref.setText("Data from preferences: " + name);
        } else if (ADD_WALLET_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            refreshWallets();
        }
    }

    private void refreshWallets() {
        List<Wallet> wallets = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(Wallet.WALLET_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                wallets.add(new Wallet(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        layoutWallets.removeAllViews();
        for (Wallet wallet : wallets) {
            wallet.draw(layoutWallets, this);
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
                .setIntents(new Intent[]{
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, ShortcutThreeActivity.class)})
                .build();

        ShortcutInfo sqlShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_sql")
                .setShortLabel("SQL")
                .setLongLabel("Открываем SQL")
                .setIcon(Icon.createWithResource(this, R.drawable.shortcut_icon_sql))
                .setIntents(new Intent[]{
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK),
                        new Intent(Intent.ACTION_MAIN, Uri.EMPTY, this, SQLiteActivity.class)})
                .build();

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        shortcutManager.setDynamicShortcuts(Arrays.asList(webShortcutInfo, threeShortcutInfo, sqlShortcutInfo));
    }

    private void initLayoutWallets() {
        layoutWallets = (LinearLayout) findViewById(R.id.layout_wallets);
    }

    private void initButtonAddWallet() {
        buttonAddWallet = (ImageButton) findViewById(R.id.image_button_add_wallet);
        buttonAddWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPref = new Intent(v.getContext(), AddWalletActivity.class);
                startActivityForResult(intentPref, ADD_WALLET_ACTIVITY_CODE);
            }
        });
    }
}
