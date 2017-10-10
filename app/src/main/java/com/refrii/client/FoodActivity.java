package com.refrii.client;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodActivity extends AppCompatActivity {

    private static final String TAG = "FoodActivity";

    private SharedPreferences sharedPreferences;
    private TextView foodNameTextView;
    private TextView foodBoxTextView;
    private TextView amountTextView;
    private TextView noticeTextView;
    private TextView expirationDateTextView;
    private TextView createdUserTextView;
    private TextView updatedUserTextView;
    private ImageView editNameImageView;
    private ImageView editAmountImageView;
    private ImageView editNoticeImageView;
    private ImageView editExpirationDateImageView;
    private Food food;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        foodNameTextView = (TextView) findViewById(R.id.foodNameTextView);
        amountTextView = (TextView) findViewById(R.id.amountTextView);
        noticeTextView = (TextView) findViewById(R.id.noticeTextView);
        expirationDateTextView = (TextView) findViewById(R.id.expirationDateTextView);
        createdUserTextView = (TextView) findViewById(R.id.createdUserTextView);
        updatedUserTextView = (TextView) findViewById(R.id.updatedUserTextView);
        foodBoxTextView = (TextView) findViewById(R.id.foodBoxTextView);
        editNameImageView = (ImageView) findViewById(R.id.editNameImageView);
        editAmountImageView = (ImageView) findViewById(R.id.editAmountImageView);
        editNoticeImageView = (ImageView) findViewById(R.id.editNoticeImageView);
        editExpirationDateImageView = (ImageView) findViewById(R.id.editExpirationDateImageView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FoodService service = RetrofitFactory.getClient(FoodService.class, FoodActivity.this);
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
                RequestBody body = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("name", food.getName())
                        .addFormDataPart("notice", food.getNotice())
                        .addFormDataPart("amount", String.valueOf(food.getAmount()))
                        .addFormDataPart("box_id", String.valueOf(food.getBox().getId()))
                        .addFormDataPart("unit_id", String.valueOf(food.getUnit().getId()))
                        .addFormDataPart("expiration_date", formatter.format(food.getExpirationDate()))
                        .build();
                Call<Food> call = service.updateFood(food.getId(), body);
                call.enqueue(new BasicCallback<Food>(FoodActivity.this) {
                    @Override
                    public void onResponse(Call<Food> call, Response<Food> response) {
                        if (response.code() == 200) {
                            Food food = response.body();
                            Log.d("aasssa",food.getName());
                            Intent intent = new Intent();
                            intent.putExtra("food", food);
                            setResult(RESULT_OK, intent);
                            finish();
                        } else {
                            Log.d(TAG, "Failed with status: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<Food> call, Throwable t) {
                        Log.d(TAG, t.getMessage());
                        Toast.makeText(FoodActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

        editNameImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(FoodActivity.this);
                editText.setText(String.valueOf(food.getName()));

                new AlertDialog.Builder(FoodActivity.this)
                        .setTitle("Name")
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                food.setName(editText.getText().toString());
                                setFoodOnView(food);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        editAmountImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(FoodActivity.this);
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                editText.setText(String.valueOf(food.getAmount()));

                new AlertDialog.Builder(FoodActivity.this)
                        .setTitle("Amount")
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                food.setAmount(Double.valueOf(editText.getText().toString()));
                                setFoodOnView(food);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        editNoticeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText editText = new EditText(FoodActivity.this);
                editText.setSingleLine(false);
                editText.setMaxLines(5);
                editText.setText(String.valueOf(food.getNotice()));

                new AlertDialog.Builder(FoodActivity.this)
                        .setTitle("Notice")
                        .setView(editText)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                food.setNotice(editText.getText().toString());
                                setFoodOnView(food);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
            }
        });

        editExpirationDateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar calendar = Calendar.getInstance();
                calendar.setTime(food.getExpirationDate());
                final DatePickerDialog datePickerDialog = new DatePickerDialog(
                        FoodActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                Toast.makeText(FoodActivity.this,
                                        String.valueOf(year) + "/" +
                                                String.valueOf(monthOfYear + 1) + "/" +
                                                String.valueOf(dayOfMonth),
                                        Toast.LENGTH_SHORT).show();
                                calendar.set(year, monthOfYear, dayOfMonth);
                                food.setExpirationDate(calendar.getTime());
                                setFoodOnView(food);
                            }
                        },
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        int foodId = intent.getIntExtra("foodId", 0);
        setFood(foodId);
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

    private void setFood(int id) {
        FoodService service = RetrofitFactory.getClient(FoodService.class, FoodActivity.this);
        Call<Food> call = service.getFood("Bearer " + sharedPreferences.getString("jwt", null), id);
        call.enqueue(new BasicCallback<Food>(FoodActivity.this) {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                super.onResponse(call, response);

                if (response.code() == 200) {
                    SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");
                    food = response.body();
                    setFoodOnView(food);
                }
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(FoodActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setFoodOnView(Food food) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");

        toolbar.setTitle(food.getName());
        foodNameTextView.setText(food.getName());
        foodBoxTextView.setText(food.getBox().getName());
        amountTextView.setText(food.getAmount() + " " + food.getUnit().getLabel());
        noticeTextView.setText(food.getNotice());
        expirationDateTextView.setText(dateFormatter.format(food.getExpirationDate()));
        createdUserTextView.setText(timeFormatter.format(food.getCreatedAt()) + " by " + food.getCreatedUser().getName());
        updatedUserTextView.setText(timeFormatter.format(food.getUpdatedAt()) + " by " + food.getUpdatedUser().getName());
    }
}
