package api;

import models.User;
import retrofit2.Call;
import retrofit2.http.Body;

import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AuthAPI {

    @POST("users/signing/")
    Call<User> login(@Body SigninDto request);

    @POST("users/signup/")
    Call<User> register(@Body RegisterDto request);

    @POST("users/contact_us/")
    Call<Void> contactUs(@Body ContactUsDto contactUsDto);

    @PUT("users/{id}")
    Call<User> updateUser(@Path("id") int id, @Body UpdateUserDto userUpdateDto);

    @POST("users/reset_password_code/")
    Call<String> resetPasswordCode(@Query("email") String email);

    @POST("users/reset_password/")
    Call<Void> resetPassword(@Body ResetPasswordDto resetPasswordDto);

}
