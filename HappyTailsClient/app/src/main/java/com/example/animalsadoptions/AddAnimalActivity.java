package com.example.animalsadoptions;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import android.util.Base64;
import android.widget.Toast;
import api.AnimalsAPI;
import api.CategoriesAPI;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Animal;
import models.Category;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;
import utils.CategoryUtils;

public class AddAnimalActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private Spinner mSpinnerCategory;
    private EditText etName, etAge, etWeight, etDescription;
    private CircleImageView circleImage;
    private RadioGroup genderRadioGroup;

    private String name, image, description;
    private Category category;
    private int age, weight;

    private User user;

    private Map<String, Category> categoryMap;

    private AnimalsAPI animalsAPI;
    private CategoriesAPI categoriesAPI;
    private AlertDialog categoryDialog;
    private List<Category> categoriesFromSP = new ArrayList<>();
    private ArrayAdapter<String> spinnerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_animal);

        getSupportActionBar().hide();

        animalsAPI = ApiClient.getClient().create(AnimalsAPI.class);

        mSpinnerCategory = findViewById(R.id.spinnerCategory);
        categoriesFromSP = getCategoriesList();

        // Create an ArrayAdapter using the string array and a default spinner layout
        spinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // add the category name to the spinner adapter
        spinnerAdapter.add("Choose Category");
        for (Category category : categoriesFromSP) {
            spinnerAdapter.add(category.getName());
        }
        categoryMap = CategoryUtils.createCategoryMap(categoriesFromSP);
        spinnerAdapter.add(getResources().getString(R.string.other_category));
        // Apply the adapter to the spinner
        mSpinnerCategory.setAdapter(spinnerAdapter);
        mSpinnerCategory.setSelection(0);

        etName = findViewById(R.id.et_name);
        etAge = findViewById(R.id.et_age);
        etWeight = findViewById(R.id.et_weight);
        etDescription = findViewById(R.id.et_description);
        circleImage = findViewById(R.id.image);
        ImageView backBtn = findViewById(R.id.back_button);
        genderRadioGroup = findViewById(R.id.gender_radio_group);

        Button submitButton = findViewById(R.id.submitButton);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleSubmitButton();
            }
        });

        circleImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mSpinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected category
                Category selectedCategory = null;
                if (mSpinnerCategory.getSelectedItem() != null && position != 0) {
                    if(mSpinnerCategory.getSelectedItem() == getResources().getString(R.string.other_category)) {
                        handleOtherCategorySelected();
                    } else {
                        String selectedCategoryName = mSpinnerCategory.getSelectedItem().toString();
                        selectedCategory = categoryMap.get(selectedCategoryName);
                    }
                }
                if(selectedCategory != null) {
                    category = selectedCategory;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            circleImage.setImageURI(uri);

            // Decode the image into a Bitmap object
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            // Convert the Bitmap to a byte array
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            byte[] imageBytes = byteArrayOutputStream.toByteArray();

            // Convert the byte array to a base64 encoded string
            String imageBase64 = Base64.encodeToString(imageBytes, Base64.DEFAULT);
            //save the image
            image = imageBase64;
        }
    }


    private void handleSubmitButton() {
        if (validateForm()) {
            Animal animal = new Animal();
            animal.setName(name);
            animal.setCategory(category);
            animal.setAge(age);
            animal.setWeight(weight);

            if(description != null && !description.isEmpty()) {
                animal.setDescription(description);
            }

            int selectedId = genderRadioGroup.getCheckedRadioButtonId();
            String genderText = ((RadioButton)findViewById(selectedId)).getText().toString();
            animal.setGender(genderText);

            if(image != null && !image.isEmpty()) {
                animal.setImage(image);
            }
            user = getDataFromSP();
            animal.setOwner(user);
            addAnimal(animal);
        }
    }

    private void addAnimal(Animal animal) {
        Call<Void> call = animalsAPI.addAnimal(animal);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(AddAnimalActivity.this, "Animal added successfully", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(AddAnimalActivity.this, HomePageActivity.class).putExtra("user", user);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // handle the failure
                Toast.makeText(AddAnimalActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private boolean validateForm() {
        boolean isValid = true;

        name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            etName.setError("Name is required");
            isValid = false;
        }

        if (category == null){
            ((TextView) mSpinnerCategory.getSelectedView()).setError("The field is required");
            isValid = false;
        }

        String ageString = etAge.getText().toString().trim();
        if (TextUtils.isEmpty(ageString)) {
            etAge.setError("The field is required");
            isValid = false;
        } else {
            age = Integer.parseInt(ageString);
        }


        String weightString = etWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weightString)) {
            etWeight.setError("The field is required");
            isValid = false;
        } else {
            weight = Integer.parseInt(weightString);
        }

        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        if (selectedId == -1) { //nothing selected
            ((RadioButton)genderRadioGroup.getChildAt(1)).setError("Please select a gender");
            isValid = false;
        }
        description = etDescription.getText().toString().trim();
        
        return isValid;
    }

    private User getDataFromSP() {
        // Get a reference to the SharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the serialized object
        String serializedObject = sharedPreferences.getString("user", "");

        // Create a Gson object
        Gson gson = new Gson();

        // Deserialize the serialized object to an object
        User user = gson.fromJson(serializedObject, User.class);
        return user;

    }

    private List<Category> getCategoriesList() {
        //get the categories list from the SP
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String categoriesJson = sharedPreferences.getString(getResources().getString(R.string.categories_sp), "");

        Gson gson = new Gson();
        Type type = new TypeToken<List<Category>>(){}.getType();
        return gson.fromJson(categoriesJson, type);
    }

    private void handleOtherCategorySelected() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        // Set the dialog layout
        View dialogView = LayoutInflater.from(this).inflate(R.layout.add_category_dialog, null);
        builder.setView(dialogView);
        // Show the dialog
        categoryDialog = builder.create();
        categoryDialog.show();

        // Get a reference to the "OK" button
        Button addButton = categoryDialog.findViewById(R.id.dialog_add);

        // Set an onClickListener for the "ADD" button
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle "OK" button click
                EditText categoryEditText = categoryDialog.findViewById(R.id.dialog_input);
                String categoryInput = categoryEditText.getText().toString().trim();
                // check if the input is empty
                if (TextUtils.isEmpty(categoryInput)) {
                    categoryEditText.setError("The field is required");
                    return;
                }
                // check if the input is already exists in the category list
                for(Category category: categoriesFromSP) {
                    if (category.getName().equalsIgnoreCase(categoryInput)) {
                        // the category already exists
                        categoryDialog.dismiss();
                        Toast.makeText(AddAnimalActivity.this, "The Category already exist", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                // add the category
                Category newCategory = new Category(categoryInput);
                addCategory(newCategory);
                spinnerAdapter.add(categoryInput);
                categoryDialog.dismiss();
            }
        });

        // Get a reference to the "Cancel" button
        Button cancelButton = categoryDialog.findViewById(R.id.dialog_cancel);

        // Set an onClickListener for the "Cancel" button
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoryDialog.dismiss();
            }
        });
    }

    private void addCategory(Category category){
        categoriesAPI = ApiClient.getClient().create(CategoriesAPI.class);

        Call<Category> call = categoriesAPI.addCategory(category);

        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(Call<Category> call, Response<Category> response) {
                if(response.isSuccessful()) {
                    Category newCategory = response.body();
                    if (newCategory != null) {
                        categoriesFromSP.add(response.body());
                        categoryMap = CategoryUtils.createCategoryMap(categoriesFromSP);
                        // Apply the adapter to the spinner
                        mSpinnerCategory.setAdapter(spinnerAdapter);
                        int index = spinnerAdapter.getPosition(newCategory.getName());
                        if (index != -1) {
                            mSpinnerCategory.setSelection(index);
                        }
                        Toast.makeText(AddAnimalActivity.this, "The Category added successfully", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddAnimalActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Category> call, Throwable t) {
                Toast.makeText(AddAnimalActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

}