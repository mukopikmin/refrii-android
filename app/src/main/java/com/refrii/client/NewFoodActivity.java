package com.refrii.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewFoodActivity extends AppCompatActivity {

    private static final String TAG = "NewFoodActivity";

    private SharedPreferences sharedPreferences;
    private Spinner spinner;

    private List<Unit> units;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_food);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        final int boxId = intent.getIntExtra("boxId", 0);

        spinner = (Spinner) findViewById(R.id.newFoodUnitSpinner);

        sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);
        String token = sharedPreferences.getString("jwt", null);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText nameEditText = (EditText) findViewById(R.id.newFoodNameEditText);
                EditText noticeEditText = (EditText) findViewById(R.id.newFoodNoticeEditText);
                EditText amountEditText = (EditText) findViewById(R.id.newFoodAmountEditText);

                String selectedUnitLabel = spinner.getSelectedItem().toString();
                Unit selectedUnit = null;
                for (Unit unit : units) {
                    if (unit.getLabel().equals(selectedUnitLabel)) {
                        selectedUnit = unit;
                        break;
                    }
                }

                Date expirationDate = new Date();
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", nameEditText.getText().toString())
                        .addFormDataPart("notice", noticeEditText.getText().toString())
                        .addFormDataPart("amount", amountEditText.getText().toString())
                        .addFormDataPart("box_id", String.valueOf(boxId))
                        .addFormDataPart("unit_id", String.valueOf(selectedUnit.getId()))
                        .addFormDataPart("expiration_date", simpleDateFormat.format(expirationDate))
                        .build();

                FoodService service = RetrofitFactory.create(FoodService.class);
                Call<Food> call = service.addFood(
                        "Bearer " + sharedPreferences.getString("jwt", null),
                        body);
                call.enqueue(new Callback<Food>() {
                    @Override
                    public void onResponse(Call<Food> call, Response<Food> response) {
                        if (response.code() == 201) {
                            // With success of creating food, exit this activity
                            Food food = response.body();
                            Log.d("aasssa",food.getName());
                            Intent intent = new Intent();
                            intent.putExtra("food", food);
                            setResult(1, intent);
                            finish();
                        } else {
                            Log.d(TAG, "Failed with status: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Food> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        Toast.makeText(NewFoodActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        UnitService service = RetrofitFactory.create(UnitService.class);
        Call<List<Unit>> call = service.getUnits("Bearer " + token);
        call.enqueue(new Callback<List<Unit>>() {
            @Override
            public void onResponse(Call<List<Unit>> call, Response<List<Unit>> response) {
                units = response.body();
                ArrayAdapter<String> adapter = new ArrayAdapter<>(NewFoodActivity.this, android.R.layout.simple_spinner_dropdown_item);
                for(Unit unit : units) {
                    adapter.add(unit.getLabel());
                }
                spinner.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<Unit>> call, Throwable t) {
                Toast.makeText(NewFoodActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

}
