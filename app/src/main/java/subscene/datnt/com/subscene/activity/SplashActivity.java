package subscene.datnt.com.subscene.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.thread.GetLanguagesAsynTask;
import subscene.datnt.com.subscene.utils.PermissionUtils;
import subscene.datnt.com.subscene.utils.SharePreference;

import static android.support.v4.content.PermissionChecker.PERMISSION_GRANTED;

public class SplashActivity extends AppCompatActivity implements GetLanguagesAsynTask.OnGetLanguageListener {
    private SharePreference preference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        preference = new SharePreference(this);
        if (PermissionUtils.checkAndRequestPermissions(this)){
            initComponents();
        }
    }

    private void initComponents() {
        if (preference.getLanguage().size() > 0)
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, 3000);
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
    public void onGetLanguage(ArrayList<String> languages) {
        Collections.sort(languages, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareToIgnoreCase(s2);
            }
        });
        String language = languages.get(0);
        for (int i = 1; i < languages.size(); i++)
            language = language + "," + languages.get(i);
        preference.saveLanguage(language);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
