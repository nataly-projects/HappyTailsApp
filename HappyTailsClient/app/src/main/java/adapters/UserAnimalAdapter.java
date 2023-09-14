package adapters;


import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import com.example.animalsadoptions.R;
import java.util.List;
import api.AnimalsAPI;
import de.hdodenhof.circleimageview.CircleImageView;
import models.Animal;
import models.User;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import utils.ApiClient;

public class UserAnimalAdapter extends RecyclerView.Adapter<UserAnimalAdapter.ViewHolder> {
    private List<Animal> animals;
    private User user;
    private Context context;
    private AnimalsAPI animalsAPI;


    public UserAnimalAdapter(List<Animal> animals, User user, Context context) {
        this.animals = animals;
        this.user = user;
        this.context = context;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CircleImageView circleImage;
        public TextView nameTextView;
        public TextView descriptionTextView;
        public ImageView genderImageView;
        public Button actionButton;

        public ViewHolder(View itemView) {
            super(itemView);
            circleImage = itemView.findViewById(R.id.image);
            nameTextView = itemView.findViewById(R.id.animal_name);
            descriptionTextView = itemView.findViewById(R.id.animal_description);
            genderImageView = itemView.findViewById(R.id.gender_image);
            actionButton = itemView.findViewById(R.id.btnAction);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.animal_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Animal animal = animals.get(position);

        holder.nameTextView.setText(animal.getName());
        holder.descriptionTextView.setText(animal.getDescription());
        holder.actionButton.setText("Delete");
        setGenderImage(animal.getGender(), holder.genderImageView);
        if (animal.getImage() != null) {
            setImage(animal.getImage(), holder.circleImage);
        }

        holder.actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(animal);
            }
        });

    }

    private void setImage(String image, CircleImageView circleImage) {
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
                        // Perform delete operation here
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
        animalsAPI = ApiClient.getClient().create(AnimalsAPI.class);

        Call<Void> call = animalsAPI.deleteAnimal(animal.getId());

        call.enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if(response.isSuccessful()) {
                    Toast.makeText(context, "Animal deleted successfully", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(context, "Error - Failed to delete", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return animals.size();
    }
}

