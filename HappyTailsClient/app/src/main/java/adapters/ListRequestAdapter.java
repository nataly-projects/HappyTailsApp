package adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.example.animalsadoptions.R;
import java.util.List;
import api.RequestAPI;
import models.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;
import utils.RequestStatus;

public class ListRequestAdapter extends BaseAdapter {

    private Context context;
    private String type;
    private List<Request> requests;
    private RequestAPI requestAPI;


    public ListRequestAdapter(Context context, List<Request> requests, String type) {
        this.context = context;
        this.requests = requests;
        this.type = type;
    }

    @Override
    public int getCount() {
        return requests.size();
    }

    @Override
    public Object getItem(int position) {
        return requests.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.request_list_item, parent, false);
        }

        requestAPI = ApiClient.getClient().create(RequestAPI.class);
        Request request = requests.get(position);
        TextView animalName = convertView.findViewById(R.id.animalName);
        TextView userReqName = convertView.findViewById(R.id.userReqName);
        TextView userReqEmail = convertView.findViewById(R.id.userReqEmail);
        TextView userReqPhone = convertView.findViewById(R.id.userReqPhone);
        TextView messageText = convertView.findViewById(R.id.messageTextView);
        Button acceptBtn = convertView.findViewById(R.id.acceptButton);
        Button denyBtn = convertView.findViewById(R.id.denyButton);

        LinearLayout messageSection = convertView.findViewById(R.id.messageSection);

        animalName.setText(request.getAnimalName());
        userReqName.setText(request.getUserReqId().getFullName());
        userReqEmail.setText(request.getUserReqId().getEmail());
        userReqPhone.setText(request.getUserReqId().getPhone());

        if(request.getMessage() != null) {
            messageText.setText(request.getMessage());
            messageSection.setVisibility(View.VISIBLE);
        } else {
            messageSection.setVisibility(View.GONE);
        }

        RequestStatus activeStatus = RequestStatus.PENDING;

        // pending request
        if(!type.equals("") && type.equals(activeStatus.getDisplayName())) {
            acceptBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    acceptRequest(request.getId());
                }
            });

            denyBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    denyRequest(request.getId());
                }
            });
        } else { //history request
            acceptBtn.setVisibility(View.INVISIBLE);
            denyBtn.setText(request.getStatus());
        }

        return convertView;
    }


    private void acceptRequest(int requestId) {
        Call<Void> call = requestAPI.acceptRequest(requestId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    ((Activity) context).finish();
                    Toast.makeText(context, "Request accepted successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // handle the failure
                Toast.makeText(context, "Error - Request not accepted successfully", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void denyRequest(int requestId) {
        Call<Void> call = requestAPI.denyRequest(requestId);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    ((Activity) context).finish();
                    Toast.makeText(context, "Request denied successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // handle the failure
                Toast.makeText(context, "Error - Request not denied successfully", Toast.LENGTH_SHORT).show();
            }
        });
    }



}




