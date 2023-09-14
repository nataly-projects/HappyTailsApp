package com.example.animalsadoptions;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import api.AnimalsAPI;
import api.UpdateAnimalDto;
import models.Animal;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;

public class AnimalDetailsActivity extends AppCompatActivity {

    private TextView nameText;
    private EditText nameEditText, ageText, weightText, genderText, descriptionText, ownerName,ownerEmail, ownerPhone, categoryText;
    private ImageView animalImage;
    private ImageView closeBtn;
    private ImageView editBtn;
    private LinearLayout ownerLayout;
    private Button saveBtn;
    private View dividerView;
    private RadioGroup genderRadioGroup;
    private LinearLayout categoryLayout;

    private String image;
    private Animal animal;
    private User user;
    private boolean isDisable;
    private boolean isOwner;

    public static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animal_details);

        getSupportActionBar().hide();

        animalImage = findViewById(R.id.animal_image);
        nameText = findViewById(R.id.nameText);
        nameEditText = findViewById(R.id.name);
        ageText = findViewById(R.id.age);
        weightText = findViewById(R.id.weight);
        descriptionText = findViewById(R.id.description);
        editBtn = findViewById(R.id.edit_button);
        closeBtn = findViewById(R.id.close);
        ImageView backBtn = findViewById(R.id.back_button);
        saveBtn = findViewById(R.id.save_btn);
        ownerName = findViewById(R.id.owner_name);
        ownerEmail = findViewById(R.id.owner_email);
        ownerPhone = findViewById(R.id.owner_phone);
        ownerLayout = findViewById(R.id.ownerLayout);
        categoryText = findViewById(R.id.categoryText);
        dividerView = findViewById(R.id.divider);
        genderText = findViewById(R.id.genderText);
        genderRadioGroup = findViewById(R.id.gender_radio_group);
        categoryLayout = findViewById(R.id.categoryLayout);


        // get the animal details
        Intent intent = getIntent();
        if (intent.getExtras() != null) {
            animal = (Animal) intent.getSerializableExtra("animal");
        }

        if (user != null && animal.getOwner().getId() == user.getId()){
            isOwner = true;
        } else {
            isOwner = false;
        }

        getUserDataFromSP();
        setDetails();
        disableTheProfile();



        animalImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isDisable) {
                    chooseImage();
                }
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // enable the edit texts
                enableTheProfile();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disable the edit texts
                disableTheProfile();
                setDetails();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disable the edit texts
                disableTheProfile();
                UpdateAnimalDto updateAnimalDto = new UpdateAnimalDto(animal);
                boolean isUpdated = isAnimalUpdated(updateAnimalDto);

                // only if some field change update the animal
                if (isUpdated) {
                    updateTheAnimalDetails(user.getId(), updateAnimalDto);
                }
                setDetails();
            }
        });
    }

    private void setDetails() {
        nameText.setText(animal.getName());
        nameEditText.setText(animal.getName());
        ageText.setText(String.valueOf(animal.getAge()));
        weightText.setText(String.valueOf(animal.getWeight()));
        descriptionText.setText(animal.getDescription());
        categoryText.setText(animal.getCategory().getName());
        if (animal.getImage() != null) {
            setImage(animal.getImage());
        } else {
            animalImage.setImageResource(R.mipmap.ic_paw);
        }
        if(user != null && animal.getOwner().getId() == user.getId()) {
            editBtn.setVisibility(View.VISIBLE);
        } else {
            dividerView.setVisibility(View.VISIBLE);
            ownerLayout.setVisibility(View.VISIBLE);
            ownerName.setText(animal.getOwner().getFullName());
            ownerEmail.setText(animal.getOwner().getEmail());
            ownerPhone.setText(animal.getOwner().getPhone());
        }
        genderText.setText(animal.getGender());
    }

    private void setImage(String image) {
        String base64String = image;
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        animalImage.setImageBitmap(decodedBitmap);
    }

    private void getUserDataFromSP() {
        // Get a reference to the SharedPreferences object
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the serialized object
        String serializedObject = sharedPreferences.getString("user", "");

        // Create a Gson object
        Gson gson = new Gson();

        // Deserialize the serialized object to an object
        user = gson.fromJson(serializedObject, User.class);
    }

    private void disableTheProfile() {
        isDisable = true;
        nameEditText.setEnabled(false);
        ageText.setEnabled(false);
        weightText.setEnabled(false);
        descriptionText.setEnabled(false);
        closeBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        if (isOwner) {
            editBtn.setVisibility(View.VISIBLE);
        }
        categoryText.setVisibility(View.VISIBLE);
        genderRadioGroup.setVisibility(View.GONE);
        genderText.setVisibility(View.VISIBLE);
        categoryLayout.setVisibility(View.VISIBLE);
    }

    private void enableTheProfile() {
        isDisable = false;
        nameEditText.setEnabled(true);
        ageText.setEnabled(true);
        weightText.setEnabled(true);
        descriptionText.setEnabled(true);
        closeBtn.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.GONE);
        categoryText.setVisibility(View.GONE);
        genderRadioGroup.setVisibility(View.VISIBLE);
        genderText.setVisibility(View.GONE);
        categoryLayout.setVisibility(View.GONE);

        //set the radio button check
        if (animal.getGender().equals("Male")) {
            RadioButton maleRadioButton = findViewById(R.id.male_radio_button);
            maleRadioButton.setChecked(true);
        } else if (animal.getGender().equals("Female")) {
            RadioButton femaleRadioButton = findViewById(R.id.female_radio_button);
            femaleRadioButton.setChecked(true);
        }
    }

    private boolean isAnimalUpdated(UpdateAnimalDto updateAnimalDto) {
        boolean isUpdated = false;

        String name = nameText.getText().toString().trim();
        if (!name.trim().equals(animal.getName())) {
            updateAnimalDto.setName(name);
            isUpdated = true;
        }
        int age = Integer.parseInt(ageText.getText().toString().trim());
        if (age != animal.getAge()) {
            updateAnimalDto.setAge(age);
            isUpdated = true;
        }
        int weight = Integer.parseInt(weightText.getText().toString().trim());
        if (weight != animal.getWeight()) {
            updateAnimalDto.setWeight(weight);
            isUpdated = true;
        }
        if (image != null && !image.isEmpty()) {
            updateAnimalDto.setImage(image);
            isUpdated = true;
        }
        String description = descriptionText.getText().toString().trim();
        if (!description.trim().equals(animal.getDescription())) {
            updateAnimalDto.setDescription(description);
            isUpdated = true;
        }
        int selectedId = genderRadioGroup.getCheckedRadioButtonId();
        String genderText = ((RadioButton)findViewById(selectedId)).getText().toString();
        if (!genderText.equals(animal.getGender())) {
            updateAnimalDto.setGender(genderText);
            isUpdated = true;
        }
        return isUpdated;
    }

    private void updateTheAnimalDetails(int id, UpdateAnimalDto updateAnimalDto) {
        AnimalsAPI animalsAPI = ApiClient.getClient().create(AnimalsAPI.class);

        Call<Animal> call = animalsAPI.updateAnimal(id, updateAnimalDto);

        call.enqueue(new Callback<Animal>() {
            @Override
            public void onResponse(Call<Animal> call, Response<Animal> response) {

                if (response.isSuccessful()) {
                    Animal updatedAnimal = response.body();
                    if ( updatedAnimal != null) {
                        Toast.makeText(AnimalDetailsActivity.this, "The animal update successfully", Toast.LENGTH_SHORT).show();
                        animal = updatedAnimal;
                        setDetails();
                    }
                } else {
                    Toast.makeText(AnimalDetailsActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Animal> call, Throwable t) {
                Toast.makeText(AnimalDetailsActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
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
            animalImage.setImageURI(uri);

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

}