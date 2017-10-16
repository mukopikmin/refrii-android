package com.refrii.client;

import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by yusuke on 2017/09/17.
 */

public class FoodListAdapter extends BaseSwipeAdapter {

    private Context mContext;
    private List<Food> mFoods;
    private FoodListAdapter mFoodListAdapter;

    public FoodListAdapter(Context context, List<Food> foods) {
        mContext = context;
        mFoods = foods;
        mFoodListAdapter = this;
        Collections.sort(foods);
    }

    public void add(Food food) {
        this.mFoods.add(food);
    }

    public void remove(Food food) {
        mFoods.remove(food);
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.foodListSwipeLayout;
    }

    @Override
    public View generateView(final int position, ViewGroup parent) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.food_list_row, null);
        final SwipeLayout swipeLayout = (SwipeLayout)v.findViewById(getSwipeLayoutResourceId(position));
        swipeLayout.setOnDoubleClickListener(new SwipeLayout.DoubleClickListener() {
            @Override
            public void onDoubleClick(SwipeLayout layout, boolean surface) {
                Toast.makeText(mContext, "DoubleClick", Toast.LENGTH_SHORT).show();
            }
        });

        v.findViewById(R.id.incrementImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food food = mFoods.get(position);
                food.setAmount(food.getAmount() + 1);
                updateFood(food, view);
            }
        });
        v.findViewById(R.id.decrementImageView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Food food = mFoods.get(position);
                food.setAmount(food.getAmount() - 1);
                updateFood(food, view);
            }
        });
        return v;
    }

    @Override
    public void fillValues(int position, View convertView) {
        Food food = mFoods.get(position);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");

        ((TextView)convertView.findViewById(R.id.nameFoodListTextView)).setText(food.getName());
        ((TextView)convertView.findViewById(R.id.expirationDateFoodListTextView)).setText(formatter.format(food.getExpirationDate()));
        ((TextView)convertView.findViewById(R.id.amountFoodListTextView)).setText(String.valueOf(food.getAmount() + " " + food.getUnit().getLabel()));
    }

    @Override
    public int getCount() {
        return this.mFoods.size();
    }

    @Override
    public Object getItem(int position) {
        return this.mFoods.get(position);
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
        FoodService service = RetrofitFactory.getClient(FoodService.class, mContext);
        Call<Food> call = service.updateFood(food.getId(), body);
        call.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, Response<Food> response) {
                Food food = response.body();
                Snackbar.make(view, "Amount of " + food.getName() + " updated", Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", null)
                        .show();
                mFoodListAdapter.notifyDataSetChanged();
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
