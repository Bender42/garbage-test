package com.example.garbage;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ShortcutInfo;
import android.content.pm.ShortcutManager;
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
import android.widget.Toast;

import com.example.garbage.cost_item.AddCostItemActivity;
import com.example.garbage.database.SQLiteActivity;
import com.example.garbage.expenditure.AddExpenditureActivity;
import com.example.garbage.expenditure.EditExpenditureActivity;
import com.example.garbage.expenditure.Expenditure;
import com.example.garbage.expenditure.ExpenditureDao;
import com.example.garbage.income_item.AddIncomeItemActivity;
import com.example.garbage.income_item.EditIncomeItemActivity;
import com.example.garbage.income_item.IncomeItem;
import com.example.garbage.income_item.IncomeItemsDao;
import com.example.garbage.shortcut.ShortcutOneActivity;
import com.example.garbage.shortcut.ShortcutThreeActivity;
import com.example.garbage.tools.GarbageTools;
import com.example.garbage.wallet.AddWalletActivity;
import com.example.garbage.wallet.EditWalletActivity;
import com.example.garbage.wallet.Wallet;
import com.example.garbage.wallet.WalletsDao;
import com.example.garbage.wallet_operation.AddWalletOperationActivity;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.garbage.tools.GarbageTools.convertDpsTpPixels;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    public static int SQL_ACTIVITY_CODE = 1;
    public static int ADD_INCOME_ITEM_ACTIVITY_CODE = 2;
    public static int EDIT_INCOME_ITEM_ACTIVITY_CODE = 3;
    public static int ADD_WALLET_ACTIVITY_CODE = 4;
    public static int EDIT_WALLET_ACTIVITY_CODE = 5;
    public static int ADD_EXPENDITURE_ACTIVITY_CODE = 6;
    public static int EDIT_EXPENDITURE_ACTIVITY_CODE = 7;
    public static int ADD_COST_ITEM_ACTIVITY_CODE = 8;
    public static int ADD_WALLET_OPERATION_ACTIVITY_CODE = 9;

    public static int EXPENDITURE_COLUMN_COUNT = 5;

    private IncomeItemsDao incomeItemsDao;
    private WalletsDao walletsDao;
    private ExpenditureDao expenditureDao;

    private List<IncomeItem> incomeItems = new LinkedList<>();
    private Map<Integer, Wallet> wallets = new LinkedHashMap<>();
    private Map<Integer, Expenditure> expenditures = new LinkedHashMap<>();

    private IncomeItem selectedIncomeItem = new IncomeItem();
    private Wallet selectedWallet = new Wallet();
    private Expenditure selectedExpenditure = new Expenditure();

    private LinearLayout layoutIncomeItems;
    private LinearLayout layoutWallets;
    private LinearLayout layoutExpenditures;

    private TextView tvIncomeItem;
    private TextView tvWallet;
    private TextView tvExpenditure;

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

        incomeItemsDao = new IncomeItemsDao(this);
        incomeItems = incomeItemsDao.getActiveIncomeItemsWithAmounts(
                GarbageTools.getCurrentMonthStartTime(),
                null);

        walletsDao = new WalletsDao(this);
        wallets = walletsDao.getActiveWallets();

        expenditureDao = new ExpenditureDao(this);
        expenditures = expenditureDao.getActiveExpendituresWithAmounts(
                GarbageTools.getCurrentMonthStartTime(),
                null);

        createShortcuts();
        initNavigation();
        initFromTo();

        layoutIncomeItems = (LinearLayout) findViewById(R.id.layout_income_items);
        initAddIncomeItemButton();
        layoutWallets = (LinearLayout) findViewById(R.id.layout_wallets);
        initAddWalletButton();
        layoutExpenditures = (LinearLayout) findViewById(R.id.scroll_linear_layout_expenditures);

        redrawIncomeItems();
        redrawWallets();
        redrawExpenditures();
    }

    private void initFromTo() {
        tvIncomeItem = (TextView) findViewById(R.id.tv_main_income_item);
        tvWallet = (TextView) findViewById(R.id.tv_main_wallet);
        tvExpenditure = (TextView) findViewById(R.id.tv_main_expenditure);
    }

    private void redrawFromTo() {
        tvIncomeItem.setText(selectedIncomeItem.getName());
        tvWallet.setText(selectedWallet.getName());
        tvExpenditure.setText(selectedExpenditure.getName());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (ADD_WALLET_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            cleanFromTo();
            redrawWallets();
        } else if (EDIT_WALLET_ACTIVITY_CODE == requestCode && (RESULT_OK == resultCode || RESULT_CANCELED == resultCode)) {
            cleanFromTo();
            redrawIncomeItems();
            redrawWallets();
            redrawExpenditures();
        } else if (ADD_EXPENDITURE_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            cleanFromTo();
            redrawExpenditures();
        } else if (EDIT_EXPENDITURE_ACTIVITY_CODE == requestCode && (RESULT_OK == resultCode || RESULT_CANCELED == resultCode)) {
            cleanFromTo();
            redrawWallets();
            redrawExpenditures();
        } else if (ADD_COST_ITEM_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            cleanFromTo();
            redrawWallets();
            redrawExpenditures();
        } else if (ADD_WALLET_OPERATION_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            cleanFromTo();
            redrawIncomeItems();
            redrawWallets();
        } else if (SQL_ACTIVITY_CODE == requestCode) {
            cleanFromTo();
            redrawIncomeItems();
            redrawWallets();
            redrawExpenditures();
        } else if (ADD_INCOME_ITEM_ACTIVITY_CODE == requestCode && RESULT_OK == resultCode) {
            cleanFromTo();
            redrawIncomeItems();
        } else if (EDIT_INCOME_ITEM_ACTIVITY_CODE == requestCode && (RESULT_OK == resultCode || RESULT_CANCELED == resultCode)) {
            cleanFromTo();
            redrawIncomeItems();
            redrawWallets();
        }
    }

    private void cleanFromTo() {
        selectedIncomeItem.updateIncomeItem(null);
        selectedWallet.updateWallet(null);
        selectedExpenditure.updateExpenditure(null);
        redrawFromTo();
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

    private void initAddIncomeItemButton() {
        ImageButton buttonAddWallet = (ImageButton) findViewById(R.id.image_button_add_income_item);
        buttonAddWallet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentPref = new Intent(v.getContext(), AddIncomeItemActivity.class);
                startActivityForResult(intentPref, ADD_INCOME_ITEM_ACTIVITY_CODE);
            }
        });
    }

    private void redrawIncomeItems() {
        incomeItems = incomeItemsDao.getActiveIncomeItemsWithAmounts(
                GarbageTools.getCurrentMonthStartTime(),
                null);
        layoutIncomeItems.removeAllViews();
        for (IncomeItem incomeItem : incomeItems) {
            ImageButton incomeItemButton = incomeItem.draw(layoutIncomeItems, this);
            incomeItemButton.setOnLongClickListener(getOnIncomeItemLongClickListener(incomeItem.getId()));
            incomeItemButton.setOnClickListener(getOnIncomeItemClickListener(incomeItem));
        }
    }

    private View.OnLongClickListener getOnIncomeItemLongClickListener(final Integer id) {
        return new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Intent intent = new Intent(v.getContext(), EditIncomeItemActivity.class);
                intent.putExtra("incomeItemId", id);
                startActivityForResult(intent, EDIT_INCOME_ITEM_ACTIVITY_CODE);
                return true;
            }
        };
    }

    private void redrawWallets() {
        wallets = walletsDao.getActiveWallets();
        layoutWallets.removeAllViews();
        for (Wallet wallet : wallets.values()) {
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

    private View.OnClickListener getOnIncomeItemClickListener(final IncomeItem incomeItem) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedWallet.getId() != null) {
                    if (!Objects.equals(selectedWallet.getCurrency(), incomeItem.getCurrency())) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.not_equal_currencies), Toast.LENGTH_LONG).show();
                    } else {
                        selectedIncomeItem.updateIncomeItem(incomeItem);
                        Intent intent = getIncomeItemIntent(v.getContext(), incomeItem, selectedWallet);
                        startActivityForResult(intent, ADD_WALLET_OPERATION_ACTIVITY_CODE);
                    }
                } else if (selectedIncomeItem.getId() != null) {
                    if (selectedIncomeItem.getId().equals(incomeItem.getId())) {
                        selectedIncomeItem.updateIncomeItem(null);
                    } else {
                        selectedIncomeItem.updateIncomeItem(incomeItem);
                    }
                } else {
                    selectedIncomeItem.updateIncomeItem(incomeItem);
                }
                selectedExpenditure.updateExpenditure(null);
                redrawFromTo();
            }
        };
    }

    private View.OnClickListener getOnWalletClickListener(final Wallet wallet) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedExpenditure.getId() != null) {
                    selectedWallet.updateWallet(wallet);
                    Intent intent = getCostItemIntent(v.getContext(), wallet, selectedExpenditure);
                    startActivityForResult(intent, ADD_COST_ITEM_ACTIVITY_CODE);
                } else if (selectedIncomeItem.getId() != null) {
                    if (!Objects.equals(wallet.getCurrency(), selectedIncomeItem.getCurrency())) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.not_equal_currencies), Toast.LENGTH_LONG).show();
                    } else {
                        selectedWallet.updateWallet(wallet);
                        Intent intent = getIncomeItemIntent(v.getContext(), selectedIncomeItem, wallet);
                        startActivityForResult(intent, ADD_WALLET_OPERATION_ACTIVITY_CODE);
                    }
                } else if (selectedWallet.getId() != null) {
                    if (selectedWallet.getId().equals(wallet.getId())) {
                        selectedWallet.updateWallet(null);
                    } else {
                        if (!Objects.equals(selectedWallet.getCurrency(), wallet.getCurrency())) {
                            Toast.makeText(v.getContext(), getResources().getString(R.string.not_equal_currencies), Toast.LENGTH_LONG).show();
                        } else {
                            Intent intent = getWalletOperationIntent(v.getContext(), selectedWallet, wallet);
                            startActivityForResult(intent, ADD_WALLET_OPERATION_ACTIVITY_CODE);
                        }
                    }
                } else {
                    selectedWallet.updateWallet(wallet);
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
                    selectedExpenditure.updateExpenditure(expenditure);
                    Intent intent = getCostItemIntent(v.getContext(), selectedWallet, expenditure);
                    startActivityForResult(intent, ADD_COST_ITEM_ACTIVITY_CODE);
                } else if (selectedExpenditure.getId() != null) {
                    if (selectedExpenditure.getId().equals(expenditure.getId())) {
                        selectedExpenditure.updateExpenditure(null);
                    } else {
                        selectedExpenditure.updateExpenditure(expenditure);
                    }
                } else {
                    selectedExpenditure.updateExpenditure(expenditure);
                }
                selectedIncomeItem.updateIncomeItem(null);
                redrawFromTo();
            }
        };
    }

    private Intent getCostItemIntent(Context context, Wallet wallet, Expenditure expenditure) {
        Intent intent = new Intent(context, AddCostItemActivity.class);
        intent.putExtra("wallet", wallet);
        intent.putExtra("expenditure", expenditure);
        return intent;
    }

    private Intent getIncomeItemIntent(Context context, Wallet fromIncomeItem, Wallet toWallet) {
        Intent intent = new Intent(context, AddWalletOperationActivity.class);
        intent.putExtra("fromIncomeItem", fromIncomeItem);
        intent.putExtra("toWallet", toWallet);
        return intent;
    }

    private Intent getWalletOperationIntent(Context context, Wallet fromWallet, Wallet toWallet) {
        Intent intent = new Intent(context, AddWalletOperationActivity.class);
        intent.putExtra("fromWallet", fromWallet);
        intent.putExtra("toWallet", toWallet);
        return intent;
    }

    private void redrawExpenditures() {
        expenditures = expenditureDao.getActiveExpendituresWithAmounts(
                GarbageTools.getCurrentMonthStartTime(),
                null);
        layoutExpenditures.removeAllViews();
        LinearLayout linearLayout = getNewLinearLayoutExpenditure();
        layoutExpenditures.addView(linearLayout);
        int index = 0;
        for (Expenditure expenditure : expenditures.values()) {
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
        buttonNewButton.setMinimumHeight(convertDpsTpPixels(80, this));
        buttonNewButton.setMinimumWidth(convertDpsTpPixels(80, this));
        buttonNewButton.setBackground(null);
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
