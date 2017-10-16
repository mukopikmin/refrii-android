package com.refrii.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class UnitsActivity extends AppCompatActivity {

    private List<Unit> mUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_units);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ListView listView = (ListView) findViewById(R.id.listView);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Unit unit = mUnits.get(i);
                Intent intent = new Intent(UnitsActivity.this, UnitActivity.class);
                intent.putExtra("unit", unit);
                startActivity(intent);
            }
        });

        setUnits();
    }

    public void setUnits() {
        UnitService service = RetrofitFactory.getClient(UnitService.class, UnitsActivity.this);
        Call<List<Unit>> call = service.getUnits();
        call.enqueue(new BasicCallback<List<Unit>>(UnitsActivity.this) {
            @Override
            public void onResponse(Call<List<Unit>> call, Response<List<Unit>> response) {
                super.onResponse(call, response);

                if (response.code() == 200) {
                    mUnits = response.body();

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(UnitsActivity.this, android.R.layout.simple_list_item_1);
                    for (Unit unit : mUnits) {
                        adapter.add(unit.getLabel());
                    }
                    ListView listView = (ListView) findViewById(R.id.listView);
                    listView.setAdapter(adapter);
                }
            }
        });
    }
}
