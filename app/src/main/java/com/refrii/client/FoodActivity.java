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
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FoodActivity extends AppCompatActivity {

    private static final String TAG = "FoodActivity";

    private SharedPreferences sharedPreferences;
    private TextView foodBoxTextView;
    private TextView amountTextView;
    private TextView expirationDateTextView;
    private TextView createdUserTextView;
    private TextView updatedUserTextView;
    private Food food;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        amountTextView = (TextView) findViewById(R.id.amountTextView);
        expirationDateTextView = (TextView) findViewById(R.id.expirationDateTextView);
        createdUserTextView = (TextView) findViewById(R.id.createdUserTextView);
        updatedUserTextView = (TextView) findViewById(R.id.updatedUserTextView);
        foodBoxTextView = (TextView) findViewById(R.id.foodBoxTextView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FoodActivity.this, EditFoodActivity.class);
                intent.putExtra("food", food);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        int foodId = intent.getIntExtra("foodId", 0);

        FoodService service = RetrofitFactory.create(FoodService.class);
        Call<Food> call = service.getFood("Bearer " + sharedPreferences.getString("jwt", null), foodId);
        call.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                food = response.body();
                toolbar.setTitle(food.getName());
                foodBoxTextView.setText(food.getBox().getName());
                amountTextView.setText(food.getAmountWithUnit());
                expirationDateTextView.setText(food.getExpirationDate().toString());
                createdUserTextView.setText(food.getCreatedInfo());
                updatedUserTextView.setText(food.getUpdatedInfo());
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(FoodActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        boolean result = true;

        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                result = super.onOptionsItemSelected(item);
        }

        return result;
    }
}
