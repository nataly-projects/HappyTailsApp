package api;

import java.util.List;

import models.Animal;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface AnimalsAPI {

    @GET("animals/animals/")
    Call<List<Animal>> getAnimalsByCategory(@Query("category") String category);

    @GET("animals/user_animals/")
    Call<List<Animal>> getAnimalsByUser(@Query("userId") int userId);

    @POST("animals/add/")
    Call<Void> addAnimal(@Body Animal animal);

    @DELETE("animals/delete")
    Call<Void> deleteAnimal(@Query("animal_id") int animalId);

    @PUT("animals/{id}")
    Call<Animal> updateAnimal(@Path("id") int id, @Body UpdateAnimalDto updateAnimalDto);
}
