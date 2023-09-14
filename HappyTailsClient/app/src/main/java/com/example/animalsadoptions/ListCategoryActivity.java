package com.example.animalsadoptions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import adapters.ListCategoryAdapter;
import api.AnimalsAPI;
import models.Animal;
import models.Category;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.AnimalStatus;
import utils.ApiClient;

public class ListCategoryActivity extends AppCompatActivity {

    private ListView animalListView;
    private ListCategoryAdapter animalAdapter;
    private AnimalsAPI animalsAPI;
    private String categoryString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_category);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        animalsAPI = ApiClient.getClient().create(AnimalsAPI.class);
        animalListView = (ListView) findViewById(R.id.animal_list_view);

        Intent intent = getIntent();
        Category selectedCategory = (Category) intent.getSerializableExtra("selected_category");

        //serialized the Category object to be included in the query string of the request URL.
        Gson gson = new Gson();
        categoryString = gson.toJson(selectedCategory);
        fetchDataFromDatabase();

    }

    // for the back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void fetchDataFromDatabase() {
        Call<List<Animal>> call = animalsAPI.getAnimalsByCategory(categoryString);

        call.enqueue(new Callback<List<Animal>>() {
            @Override
            public void onResponse(Call<List<Animal>> call, Response<List<Animal>> response) {
                if (response.isSuccessful()) {
                    List<Animal> animals = response.body();
                    List<Animal> activeAnimals = new ArrayList<>();

                    AnimalStatus activeStatus = AnimalStatus.ACTIVE;
                    if(animals != null && !animals.isEmpty()) {
                        // get only the active animals (not in status 'Hidden')
                        for (Animal animal : animals) {
                            if (activeStatus.getDisplayName().equals(animal.getStatus())) {
                                activeAnimals.add(animal);
                            }
                        }
                        animalAdapter = new ListCategoryAdapter(ListCategoryActivity.this, activeAnimals);
                        animalListView.setAdapter(animalAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Animal>> call, Throwable t) {
                Toast.makeText(ListCategoryActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchDataFromDatabase();
    }

}