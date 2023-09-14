package com.example.animalsadoptions;

import androidx.annotation.NonNull;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import com.example.animalsadoptions.databinding.ActivityHomePageBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import adapters.AnimalCategoryAdapter;
import api.CategoriesAPI;
import models.Category;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;
import utils.SPUtils;

public class HomePageActivity extends AppCompatActivity {

    private List<Category> animalCategories = new ArrayList<>();
    private ActivityHomePageBinding binding;
    private Intent intent;

    private CategoriesAPI categoriesAPI;

    FloatingActionButton addFab;
    User user;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        intent = getIntent();
        String title = "";
        if (intent.getExtras() != null) {
            user = (User) intent.getSerializableExtra("user");
            saveDataToSP(user, getResources().getString(R.string.user_sp));

            title = "Welcome " + user.getFullName();
        } else {
            title = "Welcome Guest";
        }
        getSupportActionBar().setTitle(title);

        binding = ActivityHomePageBinding.inflate(getLayoutInflater());
        binding = DataBindingUtil.setContentView(this, R.layout.activity_home_page);

        setContentView(binding.getRoot());
        boolean showButtonView = user != null;
        binding.setShowButton(showButtonView);

        GridView gridView = findViewById(R.id.gridView);

        getCategories(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                // handle the response
                if (response.isSuccessful()) {
                    animalCategories = response.body();
                    saveDataToSP(animalCategories, getResources().getString(R.string.categories_sp));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            gridView.setAdapter(new AnimalCategoryAdapter(HomePageActivity.this, animalCategories));
                        }
                    });
                } else {
                    // handle the error
                    RuntimeException e = new RuntimeException("Failed to get categories");
                    onFailure(call, e);
                }
            }

            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                // handle the error
                t.printStackTrace();
            }
        });
        addFab = findViewById(R.id.addFab);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item
                Category selectedCategory = animalCategories.get(position);
                // Launch the next activity and pass the selected item as an intent extra
                Intent intent = new Intent(HomePageActivity.this, ListCategoryActivity.class);
                intent.putExtra("selected_category",  selectedCategory);
                startActivity(intent);
            }
        });

        // go to add a new animal to adoption
        addFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // go to add animal activity form
                Intent intent = new Intent(HomePageActivity.this, AddAnimalActivity.class);
                startActivity(intent);
            }
        });
    }

    private void saveDataToSP(Object object, String str) {
        // Get a reference to the SharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String objectSerialized = gson.toJson(object);
        // Save the response
        editor.putString(str, objectSerialized);
        // Commit the changes
        editor.apply();
    }


    //menu

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        //check if user or guest
        if( intent.getExtras() == null ) { //guest
            getMenuInflater().inflate(R.menu.menu_guest, menu);
        }
        else { //user
            getMenuInflater().inflate(R.menu.menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.profile:
                // go to the profile activity
                intent = new Intent(HomePageActivity.this, UserProfileActivity.class);
                startActivity(intent);
                break;
            case R.id.about_us:
                // go to the about us activity
                intent = new Intent(HomePageActivity.this, AboutUsActivity.class);
                startActivity(intent);
                break;
            case R.id.requests:
                // go to the requests activity
                intent = new Intent(HomePageActivity.this, RequestsActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:
                SPUtils spUtils = new SPUtils(this);
                spUtils.cleanSP();
                // go to the login activity
                intent = new Intent(HomePageActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                break;
            case R.id.registration:
                // go to the registration activity
                intent = new Intent(HomePageActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void getCategories(final Callback<List<Category>> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                categoriesAPI = ApiClient.getClient().create(CategoriesAPI.class);
                Call<List<Category>> call = categoriesAPI.getCategories();
                try {
                    Response<List<Category>> response = call.execute();
                    callback.onResponse(call, response);
                } catch (IOException e) {
                    callback.onFailure(call, e);
                }
            }
        }).start();
    }



}