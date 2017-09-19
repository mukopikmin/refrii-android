package com.refrii.client;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.daimajia.swipe.adapters.ArraySwipeAdapter;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;

import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yusuke on 2017/09/17.
 */

public class FoodListAdapter extends BaseSwipeAdapter {

    Context context;
    LayoutInflater layoutInflater;
    List<Food> foods;

    public FoodListAdapter(Context context, List<Food> foods) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.foods = foods;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return 0;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return null;
    }

    @Override
    public void fillValues(int position, View convertView) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

//    @Override
//    public int getCount() {
//        return this.foods.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return this.foods.get(position);
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return this.foods.get(position).getId();
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        convertView = layoutInflater.inflate(R.layout.food_list_row, parent, false);
//
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
//
//        ((TextView)convertView.findViewById(R.id.nameFoodListTextView)).setText(foods.get(position).getName());
//        ((TextView)convertView.findViewById(R.id.expirationDateFoodListTextView)).setText(formatter.format(foods.get(position).getExpirationDate()));
//        ((TextView)convertView.findViewById(R.id.amountFoodListTextView)).setText(String.valueOf(foods.get(position).getAmountWithUnit()));
//
//        return convertView;
//    }
}
