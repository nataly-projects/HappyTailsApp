package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import com.example.animalsadoptions.R;
import java.util.List;

import models.Category;

public class AnimalCategoryAdapter extends ArrayAdapter<Category> {
    private Context context;
    private List<Category> animalCategories;

    public AnimalCategoryAdapter(Context context, List<Category> animalCategories) {
        super(context, R.layout.animal_category_item, animalCategories);
        this.context = context;
        this.animalCategories = animalCategories;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View gridView = inflater.inflate(R.layout.animal_category_item, null, true);
        TextView textView = gridView.findViewById(R.id.category_name);
        textView.setText(animalCategories.get(position).getName());
        return gridView;
    }

}


