package com.example.animalsadoptions;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import java.security.NoSuchAlgorithmException;
import java.util.regex.Pattern;
import api.AuthAPI;
import api.ResetPasswordCodeListener;
import api.ResetPasswordDto;
import api.SigninDto;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;
import utils.PasswordUtils;
import utils.SPUtils;

public class LoginActivity extends AppCompatActivity implements ResetPasswordCodeListener {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView registerLink, guestLink, resetPasswordLink;

    private AuthAPI authAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        registerLink = findViewById(R.id.registerLink);
        guestLink = findViewById(R.id.guestLink);
        resetPasswordLink = findViewById(R.id.resetPasswordLink);

        authAPI = ApiClient.getClient().create(AuthAPI.class);

        getSupportActionBar().hide();

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    loginUser();
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        });

        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToRegister();
            }
        });

        guestLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                continueAsGuest();
            }
        });

        resetPasswordLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleResetLink();
            }
        });
    }

    private void goToRegister() {
        startActivity(new Intent(this, RegisterActivity.class));
        finish();
    }

    private void continueAsGuest() {
        SPUtils spUtils = new SPUtils(this);
        spUtils.cleanSP();
        startActivity(new Intent(this, HomePageActivity.class));
        finish();
    }

    private void loginUser() throws NoSuchAlgorithmException {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Add validation to check if all fields are filled in
        boolean isFormValid = validateUser(email, password);
        if(isFormValid) {
            String encryptPassword = PasswordUtils.encryptPassword(password);
            SigninDto signinDto = new SigninDto(email, encryptPassword);
            sendLoginRequest(signinDto);
        }
    }

    private boolean validateUser(String email, String password) {
        boolean isValid = true;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (!pat.matcher(email).matches()) {
            etEmail.setError("Invalid Email");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Password is required");
            isValid = false;
        }
        return isValid;
    }

    private void sendLoginRequest(SigninDto signinDto) {
        Call<User> call = authAPI.login(signinDto);

        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                // handle the response
                if(response.isSuccessful()) {
                    if(response.body() == null) {
                        Toast.makeText(LoginActivity.this, "Wrong input", Toast.LENGTH_SHORT).show();

                    } else {
                        User loginResponse = response.body();
                        startActivity(new Intent(LoginActivity.this, HomePageActivity.class).putExtra("user", loginResponse));
                        finish();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleResetLink() {
        showForgetPasswordDialog();
    }

    private void showForgetPasswordDialog() {
        // Create a new Dialog instance
        Dialog dialog = new Dialog(LoginActivity.this);

        // Set the content view to your custom layout file
        dialog.setContentView(R.layout.reset_password_code_dialog);

        // Get references to the views in the dialog
        Button sendButton = dialog.findViewById(R.id.send_email_button);
        ImageView closeBtn = dialog.findViewById(R.id.close);
        EditText emailInput = dialog.findViewById(R.id.emailInput);
        EditText codeInput = dialog.findViewById(R.id.codeInput);
        TextView textMessage = dialog.findViewById(R.id.text);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            // set the width of the dialog window to 80% of the screen's width
            layoutParams.width = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.9);
            window.setAttributes(layoutParams);
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Set the click listener for the send email button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (emailInput.getVisibility() == View.VISIBLE) {
                    String email = emailInput.getText().toString().trim();

                    boolean isEmailValid = validateEmailDialogInput(emailInput, email);
                    if (isEmailValid) {
                        sendResetPasswordCode(email, new ResetPasswordCodeListener() {
                            @Override
                            public String onResetPasswordCodeSent(String code) {

                                // hide the email input
                                emailInput.setVisibility(View.GONE);

                                //show the code input
                                codeInput.setVisibility(View.VISIBLE);

                                //change the message
                                textMessage.setText("Enter the code you get in the mail");

                                sendButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String codeTextInput = codeInput.getText().toString().trim();

                                        boolean isCodeValid = validateCodeDialogInput(codeInput, codeTextInput, code);

                                        // check if the code we get from the server is equals to this code
                                        if (isCodeValid) {
                                            showResetPasswordDialog(email);
                                            dialog.dismiss();
                                        }
                                    }
                                });
                                return null;
                            }
                        });
                    }
                }
            }
        });
        // Show the dialog box
        dialog.show();
    }

    private void sendResetPasswordCode(String email, ResetPasswordCodeListener listener) {
        Call<String> call = authAPI.resetPasswordCode(email);
        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Send message successfully", Toast.LENGTH_SHORT).show();
                    String code = response.body();
                    if (code != null) {
                        listener.onResetPasswordCodeSent(code);
                    }
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Something wrong when sending the message", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Boolean validateEmailDialogInput(EditText emailInput, String email) {
        boolean isValid = true;

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\."+
                "[a-zA-Z0-9_+&*-]+)*@" +
                "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (!pat.matcher(email).matches()) {
            emailInput.setError("Invalid Email");
            isValid = false;
        }

        if (TextUtils.isEmpty(email)) {
            etEmail.setError("Email is required");
            isValid = false;
        }
        return isValid;
    }

    private Boolean validateCodeDialogInput(EditText codeInput, String code, String serverCode) {
        boolean isValid = true;

        if (TextUtils.isEmpty(code)) {
            codeInput.setError("Code is required");
            isValid = false;
        }

        if(serverCode == null || !serverCode.equals(code)) {
            codeInput.setError("The code is not match");
            isValid = false;
        }
        return isValid;
    }

    private void showResetPasswordDialog(String email) {
        // Create a new Dialog instance
        Dialog dialog = new Dialog(LoginActivity.this);

        // Set the content view to your custom layout file
        dialog.setContentView(R.layout.reset_password_dialog);

        // Get references to the views in the dialog
        Button sendButton = dialog.findViewById(R.id.send_button);
        ImageView closeBtn = dialog.findViewById(R.id.close);
        EditText passwordInput = dialog.findViewById(R.id.passwordInput);
        EditText passwordConfirmInput = dialog.findViewById(R.id.passwordConfirmInput);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = (int) (this.getResources().getDisplayMetrics().widthPixels * 0.9); // set the width of the dialog window to 80% of the screen's width
            window.setAttributes(layoutParams);
        }

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        // Set the click listener for the send email button
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String passwordText = passwordInput.getText().toString();
                String passwordConfirmText = passwordConfirmInput.getText().toString();

                boolean isNewPasswordValid = validateNewPassword(passwordInput, passwordConfirmInput, passwordText, passwordConfirmText);
                if (isNewPasswordValid) {

                    try {
                        sendResetPassword(email, passwordText);
                    } catch (NoSuchAlgorithmException e) {
                        e.printStackTrace();
                    }
                    dialog.dismiss();
                }
            }
        });

        // Show the dialog box
        dialog.show();
    }

    private Boolean validateNewPassword(EditText passwordEt, EditText confirmPasswordEt, String password, String confirmPassword) {
        boolean isValid = true;

        if (TextUtils.isEmpty(password)) {
            passwordEt.setError("The field is required");
            isValid = false;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordEt.setError("The field is required");
            isValid = false;
        }

        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=])[a-zA-Z0-9@#$%^&+=]{6,}$";
        Pattern pat = Pattern.compile(passwordRegex);
        boolean isPatternMatch =  pat.matcher(password).matches();
        if (!isPatternMatch) {
            passwordEt.setError("Password must be at least 6 characters long, contain at least one number, one letter, and one symbol.");
            isValid = false;
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordEt.setError("Not match to the password");
            isValid = false;
        }
        return isValid;
    }

    private void sendResetPassword(String email, String password) throws NoSuchAlgorithmException {
        String encryptPassword = PasswordUtils.encryptPassword(password);
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto(email, encryptPassword);

        Call<Void> call = authAPI.resetPassword(resetPasswordDto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Password updated successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "Something wrong when updating the password", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public String onResetPasswordCodeSent(String code) {
        return code;
    }



}