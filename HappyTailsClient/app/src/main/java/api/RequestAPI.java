package api;

import java.util.List;

import models.Request;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

public interface RequestAPI {

    @GET("requests/user_requests/")
    Call<List<Request>> getUserRequests(@Query("userId") int userId);

    @POST("requests/add/")
    Call<Void> addRequest(@Body CreateRequestDto createRequestDto);

    @DELETE("requests/accept/")
    Call<Void> acceptRequest(@Query("reqId") int reqId);

    @PUT("requests/deny")
    Call<Void> denyRequest(@Query("reqId") int reqId);
}
