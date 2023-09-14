package utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SPUtils {

    private Context context;

    public SPUtils(Context context) {
        this.context = context;
    }

    public void cleanSP() {
        SharedPreferences preferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        // Clear all data in SharedPreferences
        editor.clear();
        editor.apply();
    }
}
