package subscene.datnt.com.subscene.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;


public class SharePreference {

    private Context activity;
    private String LANGUAGE = "language";
    // constructor
    public SharePreference(Context activity) {
        this.activity = activity;
    }

    public void saveLanguage(String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(LANGUAGE, token);
        editor.apply();
    }

    public ArrayList<String> getLanguage() {
        ArrayList<String> languages = new ArrayList<>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String values = sp.getString(LANGUAGE, "");
        if (values.equals(""))
            return languages;
        String[] array = values.split(",");
        for (int i = 0 ; i < array.length ; i++)
            languages.add(array[i]);
        return languages;
    }
}
