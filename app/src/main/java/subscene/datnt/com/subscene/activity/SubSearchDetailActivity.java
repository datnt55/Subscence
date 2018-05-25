package subscene.datnt.com.subscene.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.adapter.SubtitleAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.Language;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.thread.SubServer;
import subscene.datnt.com.subscene.thread.Subscene;
import subscene.datnt.com.subscene.thread.YifySubtitles;
import subscene.datnt.com.subscene.utils.ServerType;
import subscene.datnt.com.subscene.utils.SharePreference;
import subscene.datnt.com.subscene.widget.DividerItemDecoration;

public class SubSearchDetailActivity extends AppCompatActivity implements OnItemClickListener, AdapterView.OnItemSelectedListener {
    private Film film;
    private DisplayImageOptions options;
    private ImageView imgPoster;
    private RecyclerView listSubtitle;
    private Context mThis;
    private TextView txtYear;
    private Toolbar toolbar;
    private TextView txtTitle;
    private ProgressBar progressBar;
    private TextView txtNoSub;
    private NestedScrollView layoutContent;
    private Spinner spnLanguage, spnSource;
    private ArrayList<String> languages = new ArrayList<>();
    private ArrayList<Subtitle> arraySubtitle = new ArrayList<>();
    private ArrayList<Subtitle> arraySubtitleFilter = new ArrayList<>();
    private ArrayList<Subtitle> arraySubscene = new ArrayList<>();
    private ArrayList<Subtitle> arrayYify = new ArrayList<>();
    private SubtitleAdapter adapter;
    private ArrayList<String> arraySource = new ArrayList<>();
    private String currentLang;
    private String currentSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_sub_detail);
        mThis = this;
        film = (Film) getIntent().getSerializableExtra("film");
        arraySubscene = (ArrayList<Subtitle>) getIntent().getSerializableExtra("SubScene");
        arrayYify = (ArrayList<Subtitle>) getIntent().getSerializableExtra("YifyFilm");
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imgPoster = findViewById(R.id.img_poster);
        txtYear = findViewById(R.id.txt_year);
        listSubtitle = findViewById(R.id.list_sub);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        txtNoSub = findViewById(R.id.txt_no_sub);
        spnLanguage = findViewById(R.id.spinner);
        spnSource = findViewById(R.id.spinner_source);
        layoutContent = findViewById(R.id.content);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(film.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
        listSubtitle.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(listSubtitle.getContext());
        listSubtitle.addItemDecoration(dividerItemDecoration);
        listSubtitle.setHasFixedSize(true);
        listSubtitle.setNestedScrollingEnabled(false);
        txtTitle = findViewById(R.id.txt_title);

        arraySource.add("Subscene");
        arraySource.add("Yifysubtitles");
        currentSource = "Subscene";
        currentLang = new SharePreference(this).getCurrentLanguage().getName();
        if (arraySubscene.size() > 0)
            ImageLoader.getInstance().displayImage(arraySubscene.get(0).getPoster(), imgPoster, options, new SimpleImageLoadingListener());
        else
            ImageLoader.getInstance().displayImage(arrayYify.get(0).getPoster(), imgPoster, options, new SimpleImageLoadingListener());

        new LoadSubAsynTask().execute();
    }

    private class LoadSubAsynTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... voids) {
            if (arraySubscene.size() > 0)
                updateView(arraySubscene);
            else
                updateView(arrayYify);
            return null;
        }
    }

    private int getLayoutManagerOrientation(int activityOrientation) {
        if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return LinearLayoutManager.VERTICAL;
        } else {
            return LinearLayoutManager.HORIZONTAL;
        }
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(mThis, SubtitleDownloadActivity.class);
        intent.putExtra("Subtitle", arraySubtitleFilter.get(position));
        intent.putExtra("Film", film);
        startActivity(intent);
    }

    private void updateView(ArrayList<Subtitle> subtitles) {
        progressBar.setVisibility(View.GONE);
        if (subtitles == null || subtitles.size() == 0) {
//            Snackbar snackbar = Snackbar
//                    .make(findViewById(R.id.root), "Oops, Fetching data failure ", Snackbar.LENGTH_LONG)
//                    .setAction("Try again", new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            subscene.searchSubsFromMovieName(film.getUrl(),"vietnamese");
//                        }
//                    });
//
//            snackbar.show();
            txtNoSub.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.GONE);
            return;
        }
        txtNoSub.setVisibility(View.GONE);
        layoutContent.setVisibility(View.VISIBLE);
        arraySubtitle = subtitles;
        for (Subtitle subtitle : arraySubtitle)
            if (subtitle.getLanguague().toLowerCase().equals(currentLang.toLowerCase()))
                arraySubtitleFilter.add(subtitle);
        arraySubtitleFilter.add(0, new Subtitle("Release Name/Film title", "Language", "", "", ""));
        ImageLoader.getInstance().displayImage(subtitles.get(0).getPoster(), imgPoster, options, new SimpleImageLoadingListener());
        adapter = new SubtitleAdapter(mThis, arraySubtitleFilter);
        listSubtitle.setAdapter(adapter);
        txtYear.setText(getSpannedText(mThis.getResources().getString(R.string.film_year, subtitles.get(0).getYear())));
        adapter.setOnItemClickListener(SubSearchDetailActivity.this);
        txtTitle.setText("Subtitles for " + film.getName());
        ArrayList<Language> languageArrayList = new SharePreference(this).getLanguage();
        for (Language language : languageArrayList)
            languages.add(language.getName());
        languages.add(0, "All");
        spnLanguage.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item, languages);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLanguage.setAdapter(aa);
        for (int i = 0 ; i < languages.size(); i++)
            if (languages.get(i).toLowerCase().equals(currentLang.toLowerCase()))
                spnLanguage.setSelection(i);

        spnSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (arraySource.get(i).equals(currentSource))
                    return;
                currentSource = arraySource.get(i);
                if (arraySource.get(i).equals("Subscene")) {
                    arraySubtitle = arraySubscene;
                } else {
                    arraySubtitle = arrayYify;
                }
                filterLanguage(currentLang);

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter aaSource = new ArrayAdapter(this, android.R.layout.simple_spinner_item, arraySource);
        aaSource.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnSource.setAdapter(aaSource);
        if (arraySubscene.size() <= 0)
            spnSource.setSelection(1);
    }

    private Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    private void filterLanguage(String language) {
        currentLang = language;
        arraySubtitleFilter.clear();
        if (language.equals("")) {
            for (Subtitle subtitle : arraySubtitle)
                arraySubtitleFilter.add(subtitle);

        } else {

            for (Subtitle subtitle : arraySubtitle)
                if (subtitle.getLanguague().toLowerCase().equals(language.toLowerCase()))
                    arraySubtitleFilter.add(subtitle);
        }
        if (arraySubtitleFilter.size() > 0) {
            txtNoSub.setVisibility(View.GONE);
            listSubtitle.setVisibility(View.VISIBLE);
            arraySubtitleFilter.add(0, new Subtitle("Release Name/Film title", "Language", "", "", ""));
            adapter.notifyDataSetChanged();
        } else {
            txtNoSub.setVisibility(View.VISIBLE);
            listSubtitle.setVisibility(View.GONE);
        }

    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
        String lang = "";
        if (position != 0)
            lang = languages.get(position);
        filterLanguage(lang);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
