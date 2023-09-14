package com.example.animalsadoptions;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import adapters.ListRequestAdapter;
import api.RequestAPI;
import models.Request;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;
import utils.RequestStatus;

public class RequestsActivity extends AppCompatActivity {

    private ListView requestsListView, historyListView;
    private ListRequestAdapter requestAdapter;
    private TextView pendingRequestsTextView, historyRequestsTextView;

    private RequestAPI requestAPI;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Requests");

        requestAPI = ApiClient.getClient().create(RequestAPI.class);
        requestsListView = findViewById(R.id.requests_list_view);
        historyListView = findViewById(R.id.history_requests_list_view);

        pendingRequestsTextView = findViewById(R.id.pendingRequestText);
        historyRequestsTextView = findViewById(R.id.historyRequestText);

        getUserDetails();
        getUserRequests();
    }

    private void getUserRequests() {
        Call<List<Request>> call = requestAPI.getUserRequests(user.getId());

        call.enqueue(new Callback<List<Request>>() {
            @Override
            public void onResponse(Call<List<Request>> call, Response<List<Request>> response) {
                // handle the response
                if (response.isSuccessful()) {
                    List<Request> requests = response.body();

                    RequestStatus activeStatus = RequestStatus.PENDING;

                    List<Request> pendingRequests = new ArrayList<>();
                    List<Request> otherRequests = new ArrayList<>();
                    if (requests != null) {
                        for (Request request : requests) {
                            if (activeStatus.getDisplayName().equals(request.getStatus())) {
                                pendingRequests.add(request);
                            } else {
                                otherRequests.add(request);
                            }
                        }

                        // there is no pending request
                        if (pendingRequests.size() == 0) {
                            String pendingRequestText = "You don't have an pending request yet.";
                            pendingRequestsTextView.setVisibility(View.VISIBLE);
                            pendingRequestsTextView.setText(pendingRequestText);
                        } else {
                            requestAdapter = new ListRequestAdapter(RequestsActivity.this, pendingRequests, activeStatus.getDisplayName());
                            requestsListView.setAdapter(requestAdapter);
                        }

                        // there is no history request
                        if (otherRequests.size() == 0) {
                            String historyRequestText = "You don't have an history request yet.";
                            historyRequestsTextView.setVisibility(View.VISIBLE);
                            historyRequestsTextView.setText(historyRequestText);
                        } else {
                            requestAdapter = new ListRequestAdapter(RequestsActivity.this, otherRequests, "");
                            historyListView.setAdapter(requestAdapter);
                        }
                    } else {
                        Toast.makeText(RequestsActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<List<Request>> call, Throwable t) {
                Toast.makeText(RequestsActivity.this, "There is an Error occurred", Toast.LENGTH_SHORT).show();
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


    // for the back arrow
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}