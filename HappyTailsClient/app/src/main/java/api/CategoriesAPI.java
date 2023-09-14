package api;

import java.util.List;

import models.Category;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface CategoriesAPI {

    @GET("categories/")
    Call<List<Category>> getCategories();

    @POST("categories/")
    Call<Category> addCategory(@Body Category category);

}
