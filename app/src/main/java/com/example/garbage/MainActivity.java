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
import android.widget.TextView;

import com.example.garbage.cost_item.AddCostItemActivity;
import com.example.garbage.expenditure.AddExpenditureActivity;
import com.example.garbage.expenditure.EditExpenditureActivity;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.tools.GarbageTools;
import com.example.garbage.wallet.AddWalletActivity;
import com.example.garbage.wallet.EditWalletActivity;
import com.example.garbage.wallet.Wallet;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static int SQL_ACTIVITY_CODE = 1;
    public static int ADD_WALLET_ACTIVITY_CODE = 2;
    public static int EDIT_WALLET_ACTIVITY_CODE = 3;
    public static int ADD_EXPENDITURE_ACTIVITY_CODE = 4;
    public static int EDIT_EXPENDITURE_ACTIVITY_CODE = 5;
    public static int ADD_COST_ITEM_ACTIVITY_CODE = 6;

    public static int EXPENDITURE_COLUMN_COUNT = 5;

    private List<Wallet> wallets = new LinkedList<>();
    private List<Expenditure> expenditures = new LinkedList<>();
    private Wallet selectedWallet = new Wallet();
    private Expenditure selectedExpenditure = new Expenditure();

    private LinearLayout layoutWallets;
    private LinearLayout layoutExpenditures;

    private TextView fromWallet;
    private TextView toExpenditure;

    private SQLiteHelper dbHelper;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new SQLiteHelper(this);

        wallets = getWallets();
        expenditures = getExpenditures();

        createShortcuts();
        initNavigation();
        initFromTo();

        layoutWallets = (LinearLayout) findViewById(R.id.layout_wallets);
        initAddWalletButton();
        layoutExpenditures = (LinearLayout) findViewById(R.id.scroll_linear_layout_expenditures);

        redrawWallets();
        redrawExpenditures();
        redrawFromTo();
    }

    private void initFromTo() {
        fromWallet = (TextView) findViewById(R.id.tv_main_from_wallet);
        toExpenditure = (TextView) findViewById(R.id.tv_main_to_expenditure);
    }

    private void redrawFromTo() {
        fromWallet.setText(selectedWallet.getName());
        toExpenditure.setText(selectedExpenditure.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null) {
            selectedWallet.setId(null);
            selectedWallet.setName(null);
            selectedExpenditure.setId(null);
            selectedExpenditure.setName(null);
            redrawFromTo();
            return;
        }
        if (ADD_WALLET_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            wallets = getWallets();
            redrawWallets();
        }
        if (EDIT_WALLET_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            wallets = getWallets();
            redrawWallets();
        }
        if (ADD_EXPENDITURE_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            expenditures = getExpenditures();
            redrawExpenditures();
        }
        if (EDIT_EXPENDITURE_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            expenditures = getExpenditures();
            redrawExpenditures();
        }
        if (SQL_ACTIVITY_CODE == requestCode) {
            wallets = getWallets();
            redrawWallets();
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

    private void initAddWalletButton() {
        ImageButton buttonAddWallet = (ImageButton) findViewById(R.id.image_button_add_wallet);
        buttonAddWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPref = new Intent(v.getContext(), AddWalletActivity.class);
                startActivityForResult(intentPref, ADD_WALLET_ACTIVITY_CODE);
            }
        });
    }

    private List<Wallet> getWallets() {
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
        return wallets;
    }

    private void redrawWallets() {
        layoutWallets.removeAllViews();
        for (Wallet wallet : wallets) {
            ImageButton walletButton = wallet.draw(layoutWallets, this);
            walletButton.setOnLongClickListener(getOnWalletLongClickListener(wallet.getId()));
            walletButton.setOnClickListener(getOnWalletClickListener(wallet));
        }
    }

    private View.OnLongClickListener getOnWalletLongClickListener(final Integer id) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), EditWalletActivity.class);
                intent.putExtra("walletId", id);
                startActivityForResult(intent, EDIT_WALLET_ACTIVITY_CODE);
                return true;
            }
        };
    }

    private View.OnClickListener getOnWalletClickListener(final Wallet wallet) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedExpenditure.getId() != null) {
                    selectedWallet.setId(wallet.getId());
                    selectedWallet.setName(wallet.getName());
                    Intent intent = new Intent(v.getContext(), AddCostItemActivity.class);
                    intent.putExtra("walletId", selectedWallet.getId());
                    intent.putExtra("walletName", selectedWallet.getName());
                    intent.putExtra("expenditureId", selectedExpenditure.getId());
                    intent.putExtra("expenditureName", selectedExpenditure.getName());
                    startActivityForResult(intent, ADD_COST_ITEM_ACTIVITY_CODE);
                } else if (selectedWallet.getId() != null) {
                    if (selectedWallet.getId().equals(wallet.getId())) {
                        selectedWallet.setId(null);
                        selectedWallet.setName(null);
                    } else {
                        selectedWallet.setId(wallet.getId());
                        selectedWallet.setName(wallet.getName());
                    }
                } else {
                    selectedWallet.setId(wallet.getId());
                    selectedWallet.setName(wallet.getName());
                }
                redrawFromTo();
            }
        };
    }

    private View.OnClickListener getOnExpenditureClickListener(final Expenditure expenditure) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedWallet.getId() != null) {
                    selectedExpenditure.setId(expenditure.getId());
                    selectedExpenditure.setName(expenditure.getName());
                    Intent intent = new Intent(v.getContext(), AddCostItemActivity.class);
                    intent.putExtra("walletId", selectedWallet.getId());
                    intent.putExtra("walletName", selectedWallet.getName());
                    intent.putExtra("expenditureId", selectedExpenditure.getId());
                    intent.putExtra("expenditureName", selectedExpenditure.getName());
                    startActivityForResult(intent, ADD_COST_ITEM_ACTIVITY_CODE);
                } else if (selectedExpenditure.getId() != null) {
                    if (selectedExpenditure.getId().equals(expenditure.getId())) {
                        selectedExpenditure.setId(null);
                        selectedExpenditure.setName(null);
                    } else {
                        selectedExpenditure.setId(expenditure.getId());
                        selectedExpenditure.setName(expenditure.getName());
                    }
                } else {
                    selectedExpenditure.setId(expenditure.getId());
                    selectedExpenditure.setName(expenditure.getName());
                }
                redrawFromTo();
            }
        };
    }

    private List<Expenditure> getExpenditures() {
        List<Expenditure> expenditures = new LinkedList<>();
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        Cursor cursor = database.query(Expenditure.EXPENDITURE_TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                expenditures.add(new Expenditure(cursor));
            } while (cursor.moveToNext());
        }
        cursor.close();
        dbHelper.close();
        return expenditures;
    }

    private void redrawExpenditures() {
        layoutExpenditures.removeAllViews();
        LinearLayout linearLayout = getNewLinearLayoutExpenditure();
        layoutExpenditures.addView(linearLayout);
        int index = 0;
        for (Expenditure expenditure : expenditures) {
            if (index % EXPENDITURE_COLUMN_COUNT == 0) {
                linearLayout = getNewLinearLayoutExpenditure();
                layoutExpenditures.addView(linearLayout);
            }
            ImageButton expenditureButton = expenditure.draw(linearLayout, this);
            expenditureButton.setOnLongClickListener(getOnExpenditureLongClickListener(expenditure.getId()));
            expenditureButton.setOnClickListener(getOnExpenditureClickListener(expenditure));
            index++;
        }
        drawAddExpenditureButton(linearLayout, index);
    }

    private View.OnLongClickListener getOnExpenditureLongClickListener(final Integer id) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), EditExpenditureActivity.class);
                intent.putExtra("expenditureId", id);
                startActivityForResult(intent, EDIT_EXPENDITURE_ACTIVITY_CODE);
                return true;
            }
        };
    }

    private void drawAddExpenditureButton(LinearLayout linearLayout, final int index) {
        ImageButton buttonNewButton = new ImageButton(this);
        buttonNewButton.setImageDrawable(this.getResources().getDrawable(android.R.drawable.ic_menu_add, this.getTheme()));
        buttonNewButton.setMinimumHeight(GarbageTools.convertDpsTpPixels(80, this));
        buttonNewButton.setMinimumWidth(GarbageTools.convertDpsTpPixels(80, this));
        buttonNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), AddExpenditureActivity.class);
                startActivityForResult(intent, ADD_EXPENDITURE_ACTIVITY_CODE);
            }
        });
        if (index % EXPENDITURE_COLUMN_COUNT == 0) {
            linearLayout = getNewLinearLayoutExpenditure();
            layoutExpenditures.addView(linearLayout);
        }
        linearLayout.addView(buttonNewButton);
    }

    private LinearLayout getNewLinearLayoutExpenditure() {
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        return linearLayout;
    }
}
