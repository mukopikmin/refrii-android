package com.refrii.client;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.PopupMenu;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.swipe.util.Attributes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.http.PUT;

public class BoxActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BoxActivity";
    private static final int REQUEST_CODE = 1;

    private SharedPreferences sharedPreferences;
    private ListView listView;
    private SubMenu subMenu;
    private ProgressBar progressBar;
    private List<Box> boxes;
    private Box selectedBox;
    private String jwt;
    private FloatingActionButton floatingActionButton;
    private FoodListAdapter foodListAdapter;
    private PopupMenu popupMenu;

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
                intent.putExtra("boxId", selectedBox.getId());
                startActivityForResult(intent, REQUEST_CODE);
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
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        subMenu = menu.addSubMenu("Boxes");

        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = headerView.findViewById(R.id.nameNavHeaderTextView);
        TextView mailTextView = headerView.findViewById(R.id.mailNavHeaderTextView);
        ImageView avatarImageView = (ImageView) headerView.findViewById(R.id.avatarNavHeaderImageView);
        String name = sharedPreferences.getString("name", "name");
        String mail = sharedPreferences.getString("mail", "mail");
        String avatarUrl = sharedPreferences.getString("avatar", null);
        nameTextView.setText(name);
        mailTextView.setText(mail);
        if (avatarUrl != null) {
            new ImageDownloadTask(avatarImageView).execute(avatarUrl);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        jwt = getSharedPreferences("DATA", Context.MODE_PRIVATE).getString("jwt", null);
        if (jwt == null) {
            Intent intent = new Intent(BoxActivity.this, SigninActivity.class);
            intent.putExtra("email", sharedPreferences.getString("email", null));
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
        if (id == R.id.action_box_info) {
            Intent intent = new Intent(BoxActivity.this, BoxInfoActivity.class);
            intent.putExtra("box", selectedBox);
            startActivity(intent);
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
                signOut();
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getBoxes(String token) {
        BoxService service = RetrofitFactory.getClient(BoxService.class, BoxActivity.this);
        Call<List<Box>> call = service.getBoxes();
        call.enqueue(new BasicCallback<List<Box>>(BoxActivity.this) {
            @Override
            public void onResponse(Call<List<Box>> call, Response<List<Box>> response) {
                super.onResponse(call, response);

                if (response.code() == 200) {
                    boxes = response.body();
                    selectedBox = boxes.get(0);
                    subMenu.clear();
                    for (Box box : boxes) {
                        subMenu.add(Menu.NONE, box.getId(), Menu.NONE, box.getName());
                    }
                    if (boxes.size() > 0) {
                        if (selectedBox == null) {
                            setFoods(boxes.get(0));
                        } else {
                            setFoods(boxes.get(boxes.indexOf(selectedBox)));
                        }
                    }

                    hideProgressBar();
                }
            }

            @Override
            public void onFailure(Call<List<Box>> call, Throwable t) {
                Toast.makeText(BoxActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setFoods(Box box) {
        this.foodListAdapter = new FoodListAdapter(this, box.getFoods());
        listView.setAdapter(foodListAdapter);
        foodListAdapter.setMode(Attributes.Mode.Single);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = (Food) parent.getItemAtPosition(position);
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
                final Food food = (Food) parent.getItemAtPosition(position);
                String[] items = { "Show", "Remove", "Cancel" };
                new AlertDialog.Builder(BoxActivity.this)
                        .setTitle(food.getName())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case 0:
                                        Intent intent = new Intent(BoxActivity.this, FoodActivity.class);
                                        intent.putExtra("foodId", food.getId());
                                        startActivity(intent);
                                        break;
                                    case 1:
                                        Toast.makeText(BoxActivity.this, "Needs implementation to remove this food", Toast.LENGTH_LONG).show();;
                                        FoodService service = RetrofitFactory.getClient(FoodService.class, BoxActivity.this);
                                        Call<Void> call = service.remove("Bearer " + jwt, food.getId());
                                        call.enqueue(new Callback<Void>() {
                                            @Override
                                            public void onResponse(Call<Void> call, Response<Void> response) {
                                                if (response.code() == 204) {
                                                    foodListAdapter.remove(food);
                                                    foodListAdapter.notifyDataSetChanged();
                                                    Snackbar.make(listView, "Removed successfully", Snackbar.LENGTH_LONG)
                                                            .setAction("Dismiss", null)
                                                            .show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<Void> call, Throwable t) {

                                            }
                                        });
                                        break;
                                }
                            }
                        })
                        .show();
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
        FoodService service = RetrofitFactory.getClient(FoodService.class, BoxActivity.this);
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("amount", String.valueOf(food.getAmount()))
                .build();
        Call<Food> call = service.updateFood(food.getId(), body);
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Food food = (Food) data.getSerializableExtra("food");
                foodListAdapter.add(food);
                foodListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void signOut() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("mail");
        editor.remove("name");
        editor.remove("jwt");
        editor.commit();

        Intent intent = new Intent(BoxActivity.this, SigninActivity.class);
        startActivity(intent);
    }
}
