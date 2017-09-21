package com.refrii.client;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoxActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BoxActivity";

    private SharedPreferences sharedPreferences;
    private ListView listView;
    private SubMenu subMenu;
    private ProgressBar progressBar;
    private List<Box> boxes;
    private Box selectedBox;
    private String jwt;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoxActivity.this, NewFoodActivity.class);
                intent.putExtra("Box", selectedBox.getId());
                startActivity(intent);
            }
        });

        progressBar = (ProgressBar) findViewById(R.id.boxProgressBar);
        showProgressBar();

        listView = (ListView) findViewById(R.id.listView);

        setSupportActionBar(toolbar);
        sharedPreferences = getSharedPreferences("DATA", Context.MODE_PRIVATE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        subMenu = menu.addSubMenu("Boxes");

        jwt = sharedPreferences.getString("jwt", null);
        if (jwt == null) {
            Intent intent = new Intent(BoxActivity.this, SigninActivity.class);
            startActivity(intent);
        } else {
            getBoxes(jwt);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.box, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        int boxIndex = boxes.indexOf(new Box(id));

        if (boxIndex != -1) {
            selectedBox = boxes.get(boxIndex);
            setFoods(selectedBox);
        } else {
            if (id == R.id.nav_settings) {
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
            } else if (id == R.id.nav_signout) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.remove("jwt");
                editor.commit();
                Intent intent = new Intent(this, SigninActivity.class);
                startActivity(intent);
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getBoxes(String token) {
        BoxService service = RetrofitFactory.create(BoxService.class);
        Call<List<Box>> call = service.getBoxes("Bearer " + token);
        call.enqueue(new Callback<List<Box>>() {
            @Override
            public void onResponse(Call<List<Box>> call, Response<List<Box>> response) {
                boxes = response.body();
                selectedBox = boxes.get(0);
                for (Box box : boxes) {
                    subMenu.add(Menu.NONE, box.getId(), Menu.NONE, box.getName());
                }
                if (boxes.size() > 0) {
                    setFoods(boxes.get(0));
                }

                hideProgressBar();
            }

            @Override
            public void onFailure(Call<List<Box>> call, Throwable t) {
                Toast.makeText(BoxActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setFoods(Box box) {
        FoodListAdapter mAdapter = new FoodListAdapter(this, box.getFoods());
        listView.setAdapter(mAdapter);
        mAdapter.setMode(Attributes.Mode.Single);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = (Food) parent.getItemAtPosition(position);
                Toast.makeText(BoxActivity.this, food.getName(), Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(BoxActivity.this, FoodActivity.class);
                intent.putExtra("foodId", food.getId());
                startActivity(intent);
            }
        });
        listView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(BoxActivity.this, "OnItemLongClickListener", Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.e("ListView", "onItemSelected:" + position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e("ListView", "onNothingSelected:");
            }
        });
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        floatingActionButton.hide();
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        floatingActionButton.show();
    }

    private void updateFood(Food food, double amount) {
        FoodService service = RetrofitFactory.create(FoodService.class);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", String.valueOf(food.getAmount()))
                .build();
        Call<Food> call = service.updateFood("Bearer " + jwt, food.getId(), body);
        call.enqueue(new Callback<Food>() {
            @Override
            public void onResponse(Call<Food> call, final Response<Food> response) {
                hideProgressBar();
                if (response.code() == 200) {
                    Snackbar.make(listView, "Updated amount", Snackbar.LENGTH_LONG)
                            .setAction("Revert", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    // TODO: Revert process
//                                    Food food = response.body();
//                                    food.setAmount(amount);
//                                    updateFood(food);
                                }
                            })
                            .show();
                } else {
                    onFailure(call, new Throwable("Unexpected error"));
                }
            }

            @Override
            public void onFailure(Call<Food> call, Throwable t) {
                hideProgressBar();
                Snackbar.make(listView, t.getMessage(), Snackbar.LENGTH_LONG)
                        .setAction("Dismiss", null)
                        .show();
            }
        });
    }
}
