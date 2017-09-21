package com.refrii.client;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.daimajia.swipe.SimpleSwipeListener;
import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.text.SimpleDateFormat;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yusuke on 2017/09/17.
 */

public class FoodListAdapter extends BaseSwipeAdapter {

    private Context context;
    private List<Food> foods;
    private String jwt;
    private FoodListAdapter foodListAdapter;

    public FoodListAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.foods = foods;
        foodListAdapter = this;
        SharedPreferences sharedPreferences = context.getSharedPreferences("DATA", Context.MODE_PRIVATE);
        this.jwt = sharedPreferences.getString("jwt", null);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.foodListSwipeLayout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(context).inflate(R.layout.food_list_row, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(context, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        v.findViewById(R.id.incrementImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("aaaaaaaaa", "AAAAAAAAAAAAAAAAAAAa" + position);

                Food food = foods.get(position);
                food.setAmount(food.getAmount() + 1);
                updateFood(food, view);
            }
        });
        v.findViewById(R.id.decrementImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "click decrement", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

        ((TextView)convertView.findViewById(R.id.nameFoodListTextView)).setText(foods.get(position).getName());
        ((TextView)convertView.findViewById(R.id.expirationDateFoodListTextView)).setText(formatter.format(foods.get(position).getExpirationDate()));
        ((TextView)convertView.findViewById(R.id.amountFoodListTextView)).setText(String.valueOf(foods.get(position).getAmountWithUnit()));
    }

    @Override
    public int getCount() {
        return this.foods.size();
    }

    @Override
    public Object getItem(int position) {
        return this.foods.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private void updateFood(Food food, final View view) {
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", String.valueOf(food.getAmount()))
                .build();
        FoodService service = RetrofitFactory.create(FoodService.class);
        Call<Food> call = service.updateFood("Bearer " + jwt, food.getId(), body);
        call.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Snackbar.make(view, "Incremented amount", Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", null)
                        .show();
                foodListAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                Snackbar.make(view, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", null)
                        .show();
            }
        });
    }
}
