package com.example.garbage.expenditure;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.garbage.IWallet;
import com.example.garbage.R;
import com.example.garbage.cost_item.CostItem;
import com.example.garbage.cost_item.CostItemDao;
import com.example.garbage.cost_item.CostItemsAdapter;
import com.example.garbage.wallet.WalletsDao;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class EditExpenditureActivity extends AppCompatActivity {

    private Expenditure expenditure;
    private Map<Integer, IWallet> wallets = new LinkedHashMap<>();
    private List<CostItem> costItems = new LinkedList<>();

    private WalletsDao walletsDao;
    private CostItemDao costItemDao;

    private EditText etExpenditureName;

    private RecyclerView rvCostItems;
    private RecyclerView.Adapter adapterCostItems;
    private RecyclerView.LayoutManager layoutManagerCostItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_expenditure);

        Intent intent = getIntent();
        int expenditureId = intent.getIntExtra("expenditureId", 0);
        expenditure = new Expenditure(expenditureId, this);

        walletsDao = new WalletsDao(this);
        wallets = walletsDao.getAllWallets();

        costItemDao = new CostItemDao(this);
        costItems = costItemDao.getCostItems(expenditureId, wallets);//TODO Добавить ограничение по времени. Берем объекты только текущего месяца.

        etExpenditureName = (EditText) findViewById(R.id.et_edit_expenditure_name);
        etExpenditureName.setText(expenditure.getName());

        Button bUpdateExpenditure = (Button) findViewById(R.id.b_update_expenditure);
        bUpdateExpenditure.setOnClickListener(getUpdateOnClickListener(this));

        Button bDeleteExpenditure = (Button) findViewById(R.id.b_delete_expenditure);
        bDeleteExpenditure.setOnClickListener(getDeleteOnClickListener(this));

        initRecyclerViewCostItems();
    }

    private void initRecyclerViewCostItems() {
        rvCostItems = (RecyclerView) findViewById(R.id.rv_expenditure_cost_item_list);
        rvCostItems.setHasFixedSize(false);//TODO Нужно вернуться к этой хрени и подумать

        layoutManagerCostItems = new LinearLayoutManager(this);
        rvCostItems.setLayoutManager(layoutManagerCostItems);

        adapterCostItems = new CostItemsAdapter(costItems);
        rvCostItems.setAdapter(adapterCostItems);
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