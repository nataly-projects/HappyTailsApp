package com.example.animalsadoptions;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.util.List;
import adapters.UserAnimalAdapter;
import api.AnimalsAPI;
import api.AuthAPI;
import api.UpdateUserDto;
import models.Animal;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;

public class UserProfileActivity extends AppCompatActivity {

    private TextView helloText, noAnimalText;
    private EditText userNameText, emailText, phoneText;
    private ImageView userImage;
    private ImageView closeBtn;
    private ImageView editBtn;
    private Button saveBtn;
    private User user;
    private RecyclerView userAnimalsRecycleView;
    private UserAnimalAdapter userAnimalAdapter;

    private String image;
    private AnimalsAPI animalsAPI;
    private AuthAPI authAPI;
    private boolean isDisable;

    public static final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        getSupportActionBar().hide();

        userImage = findViewById(R.id.profile_image);
        userNameText = findViewById(R.id.fullName);
        emailText = findViewById(R.id.email);
        phoneText = findViewById(R.id.phone);
        helloText = findViewById(R.id.hello);
        editBtn = findViewById(R.id.edit_button);
        closeBtn = findViewById(R.id.close);
        ImageView backBtn = findViewById(R.id.back_button);
        saveBtn = findViewById(R.id.save_btn);
        userAnimalsRecycleView = findViewById(R.id.animalsRecyclerView);
        noAnimalText = findViewById(R.id.animal_list_text);

        animalsAPI = ApiClient.getClient().create(AnimalsAPI.class);
        authAPI = ApiClient.getClient().create(AuthAPI.class);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        userAnimalsRecycleView.setLayoutManager(layoutManager);

        disableTheProfile();
        getUserDetails();
        setUserDetails();
        getUserAnimals();

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isDisable) {
                    chooseImage();
                }
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableTheProfile();
            }
        });

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                disableTheProfile();
                setUserDetails();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // disable the Layout
                disableTheProfile();
                UpdateUserDto updateUserDto = new UpdateUserDto(user);

                boolean isUpdated = isUserUpdated(updateUserDto);

                // only if some field change update the user
                if (isUpdated) {
                    updateTheUserDetails(user.getId(), updateUserDto);
                }
                setUserDetails();
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void updateTheUserDetails(int id, UpdateUserDto updateUserDto) {
        Call<User> call = authAPI.updateUser(id, updateUserDto);
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {

                if (response.isSuccessful()) {
                    User updatedUser = response.body();
                    if ( updatedUser != null) {
                        // save to sp the updated user
                        updatedUser.setId(id);
                        saveDataToSP(updatedUser);
                        user = updatedUser;
                        setUserDetails();
                    } else{
                        Toast.makeText(UserProfileActivity.this, "The email is already exists", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(UserProfileActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void getUserDetails() {
        //get the user details from the SP
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String userJson = sharedPreferences.getString("user", "");

        Gson gson = new Gson();
        user = gson.fromJson(userJson, User.class);
    }

    private void setUserDetails() {
        userNameText.setText(user.getFullName());
        emailText.setText(user.getEmail());
        phoneText.setText(user.getPhone());
        helloText.setText(String.format("Hello %s", user.getFullName()));
        if (user.getImage() != null && !user.getImage().isEmpty()) {
            setImage(user.getImage());
        } else {
            userImage.setImageResource(R.drawable.ic_user);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            userImage.setImageURI(uri);

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

    private void enableTheProfile() {
        isDisable = false;
        userNameText.setEnabled(true);
        emailText.setEnabled(true);
        phoneText.setEnabled(true);
        closeBtn.setVisibility(View.VISIBLE);
        saveBtn.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.GONE);
    }

    private void disableTheProfile() {
        isDisable = true;
        userNameText.setEnabled(false);
        emailText.setEnabled(false);
        phoneText.setEnabled(false);
        closeBtn.setVisibility(View.GONE);
        saveBtn.setVisibility(View.GONE);
        editBtn.setVisibility(View.VISIBLE);
    }

    private void getUserAnimals() {
        Call<List<Animal>> call = animalsAPI.getAnimalsByUser(user.getId());

        call.enqueue(new Callback<List<Animal>>() {
            @Override
            public void onResponse(Call<List<Animal>> call, Response<List<Animal>> response) {
                // handle the response
                if (response.isSuccessful()) {
                    List<Animal> animals = response.body();

                    if (animals == null || animals.isEmpty()) {
                        noAnimalText.setVisibility(View.VISIBLE);
                        userAnimalsRecycleView.setVisibility(View.GONE);
                    } else {
                        userAnimalAdapter = new UserAnimalAdapter(animals, user, UserProfileActivity.this);
                        userAnimalsRecycleView.setAdapter(userAnimalAdapter);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Animal>> call, Throwable t) {
                Toast.makeText(UserProfileActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveDataToSP(User updatedUser) {
        // Get a reference to the SharedPreferences object
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        Gson gson = new Gson();
        String objectSerialized = gson.toJson(updatedUser);
        // Save the response
        editor.putString(getResources().getString(R.string.user_sp), objectSerialized);
        // Commit the changes
        editor.apply();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void setImage(String image) {
        String base64String = image;
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        userImage.setImageBitmap(decodedBitmap);
    }

    private boolean isUserUpdated(UpdateUserDto updateUserDto) {
        boolean isUpdated = false;

        String name = userNameText.getText().toString().trim();
        if (!name.equals(user.getFullName())) {
            updateUserDto.setFullName(name);
            isUpdated = true;
        }
        String email = emailText.getText().toString().trim();
        if (!email.equals(user.getEmail())) {
            updateUserDto.setEmail(email);
            isUpdated = true;
        }
        String phone = phoneText.getText().toString().trim();
        if (!phone.equals(user.getPhone())) {
            updateUserDto.setPhone(phone);
            isUpdated = true;
        }
        if (image != null && !image.isEmpty()) {
            updateUserDto.setImage(image);
            isUpdated = true;
        }
        return isUpdated;
    }

}
