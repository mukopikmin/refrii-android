package com.refrii.client;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
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

import com.daimajia.swipe.util.Attributes;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoxActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "BoxActivity";
    private static final int REQUEST_CODE = 1;

    private FoodListAdapter mFoodListAdapter;
    private List<Box> mBoxes;
    private Box mBox;

    private ListView mListView;
    private SubMenu mSubMenu;
    private ProgressBar mProgressBar;
    private FloatingActionButton mFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(BoxActivity.this, NewFoodActivity.class);
                intent.putExtra("boxId", mBox.getId());
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        showProgressBar();

        mListView = (ListView) findViewById(R.id.listView);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu menu = navigationView.getMenu();
        mSubMenu = menu.addSubMenu("Boxes");
    }

    @Override
    public void onStart() {
        super.onStart();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        TextView nameTextView = headerView.findViewById(R.id.nameNavHeaderTextView);
        TextView mailTextView = headerView.findViewById(R.id.mailNavHeaderTextView);
        ImageView avatarImageView = headerView.findViewById(R.id.avatarNavHeaderImageView);
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
    public void onPause() {
        super.onPause();

        if (mBox != null) {
            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(BoxActivity.this).edit();
            editor.putInt("selected_box_index", mBoxes.indexOf(mBox));
            editor.apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        getBoxes();

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BoxActivity.this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.remove("selected_box_id");
        editor.apply();
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
            intent.putExtra("box", mBox);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        int boxIndex = mBoxes.indexOf(new Box(id));

        if (boxIndex != -1) {
            mBox = mBoxes.get(boxIndex);
            setFoods(mBox);
        } else {
            Intent intent;
            switch (id) {
                case R.id.nav_settings:
                    intent = new Intent(this, SettingsActivity.class);
                    startActivity(intent);
                    break;
                case R.id.nav_signout:
                    signOut();
                    break;
                case R.id.nav_units:
                    intent = new Intent(this, UnitsActivity.class);
                    startActivity(intent);
                    break;
            }
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void getBoxes() {
        final BoxService service = RetrofitFactory.getClient(BoxService.class, BoxActivity.this);
        Call<List<Box>> call = service.getBoxes();
        call.enqueue(new BasicCallback<List<Box>>(BoxActivity.this) {
            @Override
            public void onResponse(Call<List<Box>> call, Response<List<Box>> response) {
                super.onResponse(call, response);

                if (response.code() == 200) {
                    mBoxes = response.body();
                    mSubMenu.clear();
                    for (Box box : mBoxes) {
                        mSubMenu.add(Menu.NONE, box.getId(), Menu.NONE, box.getName());
                    }
                    if (mBoxes.size() > 0) {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(BoxActivity.this);
                        int boxIndex = sharedPreferences.getInt("selected_box_index", 0);
                        mBox = mBoxes.get(boxIndex);
                        setFoods(mBox);
                    }

                    hideProgressBar();
                }
            }
        });
    }

    private void setFoods(Box box) {
        mFoodListAdapter = new FoodListAdapter(this, box.getFoods());
        mListView.setAdapter(mFoodListAdapter);
        mFoodListAdapter.setMode(Attributes.Mode.Single);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Food food = (Food) parent.getItemAtPosition(position);
                Intent intent = new Intent(BoxActivity.this, FoodActivity.class);
                intent.putExtra("foodId", food.getId());
                startActivity(intent);
            }
        });
        mListView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.e("ListView", "OnTouch");
                return false;
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                FoodOptionDialogFragment fragment = new FoodOptionDialogFragment();
                fragment.setFood((Food) parent.getItemAtPosition(position));
                fragment.setFoodListAdapter(mFoodListAdapter);
                fragment.setListView(mListView);
                fragment.show(getFragmentManager(), "food_option");
                return true;
            }
        });
        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                Log.e("ListView", "onScrollStateChanged");
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        mListView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
        mProgressBar.setVisibility(View.VISIBLE);
        mFab.hide();
    }

    private void hideProgressBar() {
        mProgressBar.setVisibility(View.GONE);
        mFab.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Food food = (Food) data.getSerializableExtra("food");
                mFoodListAdapter.add(food);
                mFoodListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void signOut() {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.clear();
        editor.apply();

        Intent intent = new Intent(BoxActivity.this, SigninActivity.class);
        startActivity(intent);
    }

    public static class FoodOptionDialogFragment extends DialogFragment {
        private Food mFood;
        private FoodListAdapter mFoodListAdapter;
        private ListView mListView;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            CharSequence[] items = {
                    "Show",
                    "Remove",
                    "Cancel"
            };
            final Activity activity = getActivity();

            AlertDialog.Builder builder = new AlertDialog.Builder(activity)
                    .setTitle(mFood.getName())
                    .setItems(items, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case 0:
                                    Intent intent = new Intent(activity, FoodActivity.class);
                                    intent.putExtra("foodId", mFood.getId());
                                    startActivity(intent);
                                    break;
                                case 1:
                                    FoodService service = RetrofitFactory.getClient(FoodService.class, activity);
                                    Call<Void> call = service.remove(mFood.getId());
                                    call.enqueue(new Callback<Void>() {
                                        @Override
                                        public void onResponse(Call<Void> call, Response<Void> response) {
                                            if (response.code() == 204) {
                                                mFoodListAdapter.remove(mFood);
                                                mFoodListAdapter.notifyDataSetChanged();
                                                Snackbar.make(mListView, "Removed successfully", Snackbar.LENGTH_LONG)
                                                        .setAction("Dismiss", null)
                                                        .show();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<Void> call, Throwable t) {

                                        }
                                    });
                                    break;
                                default:
                                    break;
                            }
                        }
                    });

            return builder.create();
        }

        public void setFood(Food food) {
            mFood = food;
        }

        public void setFoodListAdapter(FoodListAdapter foodListAdapter) {
            mFoodListAdapter = foodListAdapter;
        }

        public void setListView(ListView listView) {
            mListView = listView;
        }
    }
}
