package subscene.datnt.com.subscene.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import subscene.datnt.com.subscene.model.Language;


public class SharePreference {

    private Context activity;
    private String LANGUAGE = "language";
    private String CURRENT_LANGUAGE = "current language";

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

    public ArrayList<Language> getLanguage() {
        ArrayList<Language> languages = new ArrayList<>();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String values = sp.getString(LANGUAGE, "");
        if (values.equals(""))
            return new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(values);
            JSONArray jsonArray = jsonObject.getJSONArray("language");
            for (int i = 0 ; i < jsonArray.length(); i++){
                JSONObject object = jsonArray.getJSONObject(i);
                Language language = new Language(object.getString("id"), object.getString("name"));
                languages.add(language);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return languages;
    }

    public void saveCurrentLanguage(Language languages) {
        try {
            JSONObject item = new JSONObject();
            item.put("id", languages.getId());
            item.put("name", languages.getName());
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(CURRENT_LANGUAGE, item.toString());
            editor.apply();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Language getCurrentLanguage() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(activity);
        String values = sp.getString(CURRENT_LANGUAGE, "");
        Language language = null;
        try {
            JSONObject jsonObject = new JSONObject(values);
            String id = jsonObject.getString("id");
            String name = jsonObject.getString("name");
            language = new Language(id, name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return language;
    }
}
