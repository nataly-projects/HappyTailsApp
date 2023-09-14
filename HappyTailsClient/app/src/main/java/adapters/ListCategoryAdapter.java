package adapters;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.example.animalsadoptions.AnimalDetailsActivity;
import com.example.animalsadoptions.R;
import com.google.gson.Gson;
import java.util.List;
import api.AnimalsAPI;
import api.CreateRequestDto;
import api.RequestAPI;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Animal;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;
import utils.RequestStatus;

public class ListCategoryAdapter extends BaseAdapter {

    private Context context;
    private List<Animal> animals;
    private User user;
    private CircleImageView circleImage;
    private AnimalsAPI animalsAPI;
    private RequestAPI requestAPI;

    public ListCategoryAdapter(Context context, List<Animal> animals) {
        this.context = context;
        this.animals = animals;
    }

    @Override
    public int getCount() {
        return animals.size();
    }

    @Override
    public Object getItem(int position) {
        return animals.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.animal_list_item, parent, false);
        }
        getDataFromSP();

        animalsAPI = ApiClient.getClient().create(AnimalsAPI.class);
        requestAPI = ApiClient.getClient().create(RequestAPI.class);

        Animal animal = animals.get(position);

        TextView animalName = convertView.findViewById(R.id.animal_name);
        TextView animalDescription = convertView.findViewById(R.id.animal_description);
        ImageView genderImage = convertView.findViewById(R.id.gender_image);
        circleImage = convertView.findViewById(R.id.image);
        Button btn = convertView.findViewById(R.id.btnAction);

        //set the gender image by the animal gender
        setGenderImage(animal.getGender(), genderImage);
        // only if it user shows the buttons
        if(user != null ) {
            if (user.getId() == animal.getOwner().getId()) {
                btn.setText("Delete");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDeleteConfirmationDialog(animal);
                    }
                });
            } else {
                btn.setText("Adopt me");
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showOwnerDetailsDialog(animal);
                    }
                });
            }
        } else {
            btn.setVisibility(View.INVISIBLE);
        }

        animalName.setText(animal.getName());
        animalDescription.setText(animal.getDescription());
        if (animal.getImage() != null) {
            setImage(animal.getImage());
        }

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle click event
                Intent intent = new Intent(context, AnimalDetailsActivity.class);
                intent.putExtra("animal", animal);
                context.startActivity(intent);
            }
        });

        return convertView;
    }

    private void getDataFromSP() {
        // Get a reference to the SharedPreferences object
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);

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

    private void setImage(String image) {
        String base64String = image;
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
        circleImage.setImageBitmap(decodedBitmap);
    }

    private void setGenderImage(String animalGender, ImageView genderImage) {
        if (animalGender.equalsIgnoreCase("male")) {
            genderImage.setImageResource(R.drawable.ic_male);
        } else {
            genderImage.setImageResource(R.drawable.ic_female);
        }
    }

    private void showDeleteConfirmationDialog(final Animal animal) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Delete Confirmation")
                .setMessage("Are you sure you want to delete this animal?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Perform delete operation
                        deleteAnimal(animal);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel delete operation
                        dialog.dismiss();
                    }
                })
                .create()
                .show();
    }

    private void deleteAnimal(Animal animal ) {
        Call<Void> call = animalsAPI.deleteAnimal(animal.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Animal deleted successfully", Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // handle the failure
                Toast.makeText(context, "Animal deleted failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void showOwnerDetailsDialog(Animal animal) {
        // Create a new Dialog instance
        Dialog dialog = new Dialog(context);

        // Set the content view to your custom layout file
        dialog.setContentView(R.layout.adopt_me_dialog);

        // Get references to the views in the layout
        TextView animalName = dialog.findViewById(R.id.animal_text);
        TextView ownerName = dialog.findViewById(R.id.owner_name);
        TextView ownerEmail = dialog.findViewById(R.id.owner_email);
        TextView ownerPhone = dialog.findViewById(R.id.owner_phone);
        Button sendEmailButton = dialog.findViewById(R.id.send_email_button);
        ImageView closeBtn = dialog.findViewById(R.id.close);
        EditText messageText = dialog.findViewById(R.id.messageText);

        Window window = dialog.getWindow();
        if (window != null) {
            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            layoutParams.copyFrom(window.getAttributes());
            layoutParams.width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9); // set the width of the dialog window to 80% of the screen's width
            window.setAttributes(layoutParams);
        }

        animalName.setText(String.format("We are very happy that you are interested in adopting %s", animal.getName()));
        ownerName.setText(animal.getOwner().getFullName());
        ownerEmail.setText(animal.getOwner().getEmail());
        ownerPhone.setText(animal.getOwner().getPhone());

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });


        // Set the click listener for the send email button
        sendEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkAdoptFormValid(messageText)) {
                    String message = messageText.getText().toString();
                    sendRequestToAdoption(animal, message);
                    dialog.dismiss();
                }

            }
        });

        // Show the dialog box
        dialog.show();
    }

    private boolean checkAdoptFormValid(EditText messageText) {
        boolean isValid = true;

        String message = messageText.getText().toString();
        if (message.isEmpty()) {
            messageText.setError("message is requires.\nplease write a short message for the owner");
            isValid = false;

        }
        return isValid;
    }

    private void sendRequestToAdoption(Animal animal, String message) {

        CreateRequestDto createRequestDto = new CreateRequestDto();
        createRequestDto.setAnimalId(animal.getId());
        createRequestDto.setOwnerId(animal.getOwner());
        RequestStatus pendingStatus = RequestStatus.PENDING;
        createRequestDto.setStatus(pendingStatus.getDisplayName());
        createRequestDto.setAnimalName(animal.getName());
        createRequestDto.setUserReqId(user);

        createRequestDto.setMessage(message);

        Call<Void> call = requestAPI.addRequest(createRequestDto);

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "The Request sent successfully", Toast.LENGTH_SHORT).show();
                    ((Activity) context).finish();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                // handle the failure
                Toast.makeText(context, "Error while sending the message", Toast.LENGTH_SHORT).show();
            }
        });
    }



}




