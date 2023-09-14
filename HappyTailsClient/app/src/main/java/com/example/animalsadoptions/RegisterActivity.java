package com.example.animalsadoptions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import api.AuthAPI;
import api.RegisterDto;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;
import utils.PasswordUtils;

public class RegisterActivity extends AppCompatActivity {

    private EditText etFullName, etPhone, etPassword, etConfirmPassword, etEmail;

    private AuthAPI authAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_register);

        etFullName = findViewById(R.id.etFullName);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        Button btnRegister = findViewById(R.id.btnRegister);
        TextView loginLink = findViewById(R.id.loginLink);

        authAPI = ApiClient.getClient().create(AuthAPI.class);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    registerUser();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });

        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToLogin();
            }
        });
    }

    private void registerUser() throws NoSuchAlgorithmException {
        String fullName = etFullName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // inputs validation
        boolean isEmailValid = validateEmail(email);
        boolean isPasswordValid = validatePassword(password, confirmPassword);
        boolean isInputValid = validateInput(fullName, phone);

        // if all the inputs valid - save to db
        if (isEmailValid && isPasswordValid && isInputValid) {
            String encryptPassword = PasswordUtils.encryptPassword(password);
            RegisterDto registerDto = new RegisterDto(fullName, phone, email, encryptPassword);
            sendRegisterRequest(registerDto);
        }
    }

    private boolean validateEmail(String email) {
        boolean isValid = true;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (email == null || TextUtils.isEmpty(email)) {
            etEmail.setError("The field is required");
            isValid = false;
        }

        if (!pat.matcher(email).matches()) {
            etEmail.setError("Invalid Email");
            isValid = false;
        }
        return isValid;
    }

    private boolean validatePassword(String password, String confirmPassword) {
        boolean isValid = true;

        //validate the password not empty
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("The field is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("The field is required");
            isValid = false;
        }

        //validate the password length and contain
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])[a-zA-Z0-9@#$%^&+=]{6,}$";
        Pattern pat = Pattern.compile(passwordRegex);
        boolean isPatternMatch =  pat.matcher(password).matches();
        if (!isPatternMatch) {
            etPassword.setError("Password must be at least 6 characters long, contain at least one number, one letter, and one symbol.");
            isValid = false;
        }

        //validate that the password and the confirm password are the same
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Not match to the password");
            isValid = false;
        }
        return isValid;
    }

    private boolean validateInput(String fullName, String phone) {
        boolean isValid = true;

        if (TextUtils.isEmpty(fullName)) {
            etFullName.setError("The field is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(phone)) {
            etPhone.setError("The field is required");
            isValid = false;
        }

        // Regular expression for a phone number pattern
        String phonePattern = "^[+]?[0-9]{10,13}$";
        Pattern pattern = Pattern.compile(phonePattern);
        boolean isPatternMatch =  pattern.matcher(phone).matches();
        if (!isPatternMatch) {
            etPhone.setError("Invalid phone number");
            isValid = false;
        }
        return isValid;
    }

    private void sendRegisterRequest(RegisterDto registerDto) {
        Call<User> call = authAPI.register(registerDto);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // handle the response
                if(response.isSuccessful()) {
                    if(response.body() != null) {
                        User user = response.body();
                        Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, HomePageActivity.class).putExtra("user", user ));
                        finish();
                    } else {
                        // the email is already exists
                        etEmail.setError("The email is already exists");
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(RegisterActivity.this, "Error - Register unsuccessfully", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void goToLogin() {
        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}