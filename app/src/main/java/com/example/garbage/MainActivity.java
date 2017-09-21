package com.example.garbage;

import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.example.garbage.wallet.AddWalletActivity;
import com.example.garbage.wallet.Wallet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static int ADD_WALLET_ACTIVITY_CODE = 2;
    public static int SQL_ACTIVITY_CODE = 3;

    private LinearLayout layoutWallets;

    private SQLiteHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new SQLiteHelper(this);

        createShortcuts();
        initNavigation();
        initLayoutWallets();
        refreshWallets();
        initButtonAddWallet();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            return;
        }
        if (ADD_WALLET_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            refreshWallets();
        }
        if (SQL_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
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

        ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
        shortcutManager.setDynamicShortcuts(Arrays.asList(webShortcutInfo, threeShortcutInfo));
    }

    private void initLayoutWallets() {
        layoutWallets = (LinearLayout) findViewById(R.id.layout_wallets);
    }

    private void initButtonAddWallet() {
        ImageButton buttonAddWallet = (ImageButton) findViewById(R.id.image_button_add_wallet);
        buttonAddWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPref = new Intent(v.getContext(), AddWalletActivity.class);
                startActivityForResult(intentPref, ADD_WALLET_ACTIVITY_CODE);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_shortcut_one) {
            Intent intentShortcut = new Intent(this, ShortcutOneActivity.class);
            startActivity(intentShortcut);
        } else if (id == R.id.nav_preferences) {
            Intent intentPref = new Intent(this, PreferencesActivity.class);
            startActivityForResult(intentPref, 1);
        } else if (id == R.id.change_shortcut_rank) {
            ShortcutInfo webShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_two_web")
                    .setRank(1)
                    .build();
            ShortcutInfo threeShortcutInfo = new ShortcutInfo.Builder(this, "shortcut_three")
                    .setRank(0)
                    .build();
            ShortcutManager shortcutManager = getSystemService(ShortcutManager.class);
            shortcutManager.updateShortcuts(Arrays.asList(webShortcutInfo, threeShortcutInfo));
        } else if (id == R.id.nav_sql) {
            Intent intentPref = new Intent(this, SQLiteActivity.class);
            startActivityForResult(intentPref, SQL_ACTIVITY_CODE);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initNavigation() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }
}
