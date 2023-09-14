package com.example.animalsadoptions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.gson.Gson;
import java.util.regex.Pattern;
import api.AuthAPI;
import api.ContactUsDto;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;

public class AboutUsActivity extends AppCompatActivity {

    private EditText name, email, message;
    private Button sendBtn;

    private User user;
    private AuthAPI authAPI;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_us);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        authAPI = ApiClient.getClient().create(AuthAPI.class);
        getDataFromSP();

        name = findViewById(R.id.name_edittext);
        email = findViewById(R.id.email_edittext);
        message = findViewById(R.id.message_edittext);
        sendBtn = findViewById(R.id.send_button);

        if (user != null) {
            name.setVisibility(View.GONE);
            email.setVisibility(View.GONE);
        }

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isValidForm = validateContactUs();
                if (isValidForm) {
                    // send server request to send the email;
                    sendContactUs();
                }
            }
        });

    }

    private void getDataFromSP() {
        // Get a reference to the SharedPreferences object
        SharedPreferences sharedPreferences = this.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

        // Get the serialized object
        String serializedObject = sharedPreferences.getString("user", "");

        // Create a Gson object
        Gson gson = new Gson();

        // Deserialize the serialized object to an object
        User object = gson.fromJson(serializedObject, User.class);
        if (object != null) {
            user = object;
        } else {
            user = null;
        }
    }

    private boolean validateContactUs() {
        boolean isValid = true;

        if (name.getVisibility() != View.GONE && name.getText().toString().isEmpty()) {
            name.setError("Name is required");
            isValid = false;
        }

        if (message.getText().toString().isEmpty()) {
            message.setError("Message is required");
            isValid = false;
        }

        if (email.getVisibility() != View.GONE) {
            boolean isEmailValid = validateEmail();
            if (!isEmailValid) {
                isValid = false;
            }
        }
        return isValid;
    }

    private boolean validateEmail() {
        boolean isValid = true;
        String emailInputText = email.getText().toString();

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (emailInputText.isEmpty()) {
            email.setError("Email is required");
            isValid = false;
        }
        if (!pat.matcher(emailInputText).matches()) {
            email.setError("Invalid Email");
            isValid = false;
        }
        return isValid;
    }

    private void sendContactUs() {
        ContactUsDto contactUsDto = new ContactUsDto();
        if (user != null){
            contactUsDto.setUserId(user.getId());
            contactUsDto.setName(user.getFullName());
            contactUsDto.setEmail(user.getEmail());
        } else {
            contactUsDto.setName(name.getText().toString());
            contactUsDto.setEmail(email.getText().toString());
        }
        contactUsDto.setMessage(message.getText().toString());

        Call<Void> call = authAPI.contactUs(contactUsDto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(AboutUsActivity.this, "Send message successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // handle the failure
                Toast.makeText(AboutUsActivity.this, "Error while sending the message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        // Handle back button click
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}