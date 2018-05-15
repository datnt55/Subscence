package subscene.datnt.com.subscene.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.model.Language;
import subscene.datnt.com.subscene.thread.GetLanguagesAsynTask;
import subscene.datnt.com.subscene.utils.PermissionUtils;
import subscene.datnt.com.subscene.utils.SharePreference;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;
import static subscene.datnt.com.subscene.utils.Globals.APP_FOLDER;

public class SplashActivity extends AppCompatActivity implements GetLanguagesAsynTask.OnGetLanguageListener {
    private SharePreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preference = new SharePreference(this);
        if (PermissionUtils.checkAndRequestPermissions(this)) {
            initComponents();
        }
    }

    private void initComponents() {
        File path = new File(APP_FOLDER);
        if (!path.exists()) {
            path.mkdirs();
        }
        if (preference.getLanguage().size() > 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 1500);
        else
            new GetLanguagesAsynTask(this, this).execute();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PermissionUtils.REQUEST_ID_MULTIPLE_PERMISSIONS) {
            if ((grantResults.length > 0 && grantResults[0] == PERMISSION_GRANTED))
                initComponents();
        }
    }

    @Override
    public void onGetLanguage(ArrayList<Language> languages) {
        Collections.sort(languages, new Comparator<Language>() {
            @Override
            public int compare(Language s1, Language s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });
        try {
            JSONObject languageObj = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            String current = Locale.getDefault().getDisplayLanguage(Locale.ENGLISH);
            Language english;
            for (int i = 1; i < languages.size(); i++) {
                if (languages.get(i).getName().toLowerCase().equals(current.toLowerCase()))
                    preference.saveCurrentLanguage(languages.get(i));
                JSONObject item = new JSONObject();
                item.put("id", languages.get(i).getId());
                item.put("name", languages.get(i).getName());
                jsonArray.put(item);
            }
            languageObj.put("language", jsonArray);
            preference.saveLanguage(languageObj.toString());


        } catch (JSONException e) {
            e.printStackTrace();
        }


        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
