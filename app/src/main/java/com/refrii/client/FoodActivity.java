package com.refrii.client;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

    private Food food;

    private ConstraintLayout constraintLayout;
    private FloatingActionButton fab;
    private ProgressBar progressBar;
    private TextView foodEditedMessageTextView;
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
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        constraintLayout = (ConstraintLayout) findViewById(R.id.constraintLayout);
        progressBar = (ProgressBar) findViewById(R.id.foodProgressBar);
        foodEditedMessageTextView = (TextView) findViewById(R.id.foodEditedMessageTextView);
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

        constraintLayout.setVisibility(View.GONE);
        foodEditedMessageTextView.setVisibility(View.GONE);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
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
                            Intent intent = new Intent();
                            intent.putExtra("food", food);
                            setResult(RESULT_OK, intent);
                            progressBar.setVisibility(View.GONE);
                            finish();
                        } else {
                            Log.d(TAG, "Failed with status: " + response.code());
                        }
                    }
                });
            }
        });

        fab.setVisibility(View.GONE);

        editNameImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNameDialogFragment fragment = new EditNameDialogFragment();
                fragment.setFood(food);
                fragment.show(getFragmentManager(), "edit_name");
            }
        });

        editAmountImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditAmountDialogFragment fragment = new EditAmountDialogFragment();
                fragment.setFood(food);
                fragment.show(getFragmentManager(), "edit_amount");
            }
        });

        editNoticeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditNoticeDialogFragment fragment = new EditNoticeDialogFragment();
                fragment.setFood(food);
                fragment.show(getFragmentManager(), "edit_notice");
            }
        });

        editExpirationDateImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditDateDialogFragment fragment = new EditDateDialogFragment();
                fragment.setFood(food);
                fragment.show(getFragmentManager(), "edit_expiration_date");
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
        Call<Food> call = service.getFood(id);
        call.enqueue(new BasicCallback<Food>(FoodActivity.this) {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                super.onResponse(call, response);

                if (response.code() == 200) {
                    food = response.body();
                    setFoodOnView(food);
                    constraintLayout.setVisibility(View.VISIBLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Log.d(TAG, t.getMessage());
                Toast.makeText(FoodActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void setFoodOnView(Food food) {
        SimpleDateFormat timeFormatter = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy/MM/dd");

        long daysLeft = (food.getExpirationDate().getTime() - new Date().getTime()) / (24 * 60 * 60 * 1000);

        toolbar.setTitle(food.getName());
        foodNameTextView.setText(food.getName());
        foodBoxTextView.setText(food.getBox().getName());
        amountTextView.setText(food.getAmount() + " " + food.getUnit().getLabel());
        noticeTextView.setText(food.getNotice());
        expirationDateTextView.setText(dateFormatter.format(food.getExpirationDate()));
        if (daysLeft < 0) {
            expirationDateTextView.append(" (" + Math.abs(daysLeft) + " days over)");
            expirationDateTextView.setTextColor(Color.RED);
        } else {
            expirationDateTextView.append(" (" + Math.abs(daysLeft) + " days left)");
        }
        createdUserTextView.setText(timeFormatter.format(food.getCreatedAt()) + " by " + food.getCreatedUser().getName());
        updatedUserTextView.setText(timeFormatter.format(food.getUpdatedAt()) + " by " + food.getUpdatedUser().getName());
    }

    public void onEdited() {
        foodEditedMessageTextView.setVisibility(View.VISIBLE);
        fab.setVisibility(View.VISIBLE);
    }

    public static class EditNameDialogFragment extends DialogFragment {
        private Food mFood;
        private EditText mEditText;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.edit_text_dialog, null);
            mEditText = content.findViewById(R.id.editText);
            mEditText.setText(mFood.getName());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("Name")
                    .setView(content)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mFood.setName(mEditText.getText().toString());
                            ((FoodActivity) getActivity()).setFoodOnView(mFood);
                            ((FoodActivity) getActivity()).onEdited();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });

            return builder.create();
        }

        public void setFood(Food food) {
            mFood = food;
        }
    }

    public static class EditAmountDialogFragment extends DialogFragment {
        private Food mFood;
        private EditText mEditText;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.edit_text_dialog, null);
            mEditText = content.findViewById(R.id.editText);
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
            mEditText.setText(String.valueOf(mFood.getAmount()));

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("Amount")
                    .setView(content)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mFood.setAmount(Double.valueOf(mEditText.getText().toString()));
                            ((FoodActivity) getActivity()).setFoodOnView(mFood);
                            ((FoodActivity) getActivity()).onEdited();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });

            return builder.create();
        }

        public void setFood(Food food) {
            mFood = food;
        }
    }

    public static class EditNoticeDialogFragment extends DialogFragment {
        private Food mFood;
        private EditText mEditText;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.edit_text_dialog, null);
            mEditText = content.findViewById(R.id.editText);
            mEditText.setSingleLine(false);
            mEditText.setMaxLines(5);
            mEditText.setText(mFood.getNotice());

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setTitle("Notice")
                    .setView(content)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mFood.setNotice(mEditText.getText().toString());
                            ((FoodActivity) getActivity()).setFoodOnView(mFood);
                            ((FoodActivity) getActivity()).onEdited();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });

            return builder.create();
        }

        public void setFood(Food food) {
            mFood = food;
        }
    }

    public static class EditDateDialogFragment extends DialogFragment {
        private Food mFood;
        private DatePicker mDatePicker;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View content = inflater.inflate(R.layout.edit_date_dialog, null);
            mDatePicker = content.findViewById(R.id.datePicker);

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(mFood.getExpirationDate());
            mDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity())
                    .setView(content)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.set(mDatePicker.getYear(), mDatePicker.getMonth(), mDatePicker.getDayOfMonth());
                            mFood.setExpirationDate(calendar.getTime());
                            ((FoodActivity) getActivity()).setFoodOnView(mFood);
                            ((FoodActivity) getActivity()).onEdited();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });

            return builder.create();
        }

        public void setFood(Food food) {
            mFood = food;
        }
    }
}
