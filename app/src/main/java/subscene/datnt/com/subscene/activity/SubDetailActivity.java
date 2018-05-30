package subscene.datnt.com.subscene.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.FilmInfo;
import subscene.datnt.com.subscene.model.Language;
import subscene.datnt.com.subscene.thread.HttpService;
import subscene.datnt.com.subscene.thread.OpenSubtitle;
import subscene.datnt.com.subscene.thread.SubServer;
import subscene.datnt.com.subscene.thread.Subscene;
import subscene.datnt.com.subscene.thread.YifySubtitles;
import subscene.datnt.com.subscene.utils.ServerType;
import subscene.datnt.com.subscene.utils.SharePreference;
import subscene.datnt.com.subscene.widget.DividerItemDecoration;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.adapter.SubtitleAdapter;

public class SubDetailActivity extends AppCompatActivity implements OnItemClickListener, OnSceneListener,   AdapterView.OnItemSelectedListener, HttpService.HttpResponseListener {
    private Film film;
    private DisplayImageOptions options;
    private ImageView imgPoster;
    private RecyclerView listSubtitle;
    private Context mThis;
    private Toolbar toolbar;
    private TextView txtTitle;
    private ProgressBar progressBar;
    private SubServer subscene;
    private TextView txtNoSub;
    private NestedScrollView layoutContent;
    private Spinner spnLanguage;
    private ArrayList<String> languages =new ArrayList<>();
    private ArrayList<Subtitle> arraySubtitle = new ArrayList<>();
    private ArrayList<Subtitle> arraySubtitleFilter = new ArrayList<>();
    private SubtitleAdapter adapter;
    private HttpService httpService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail);
        mThis = this;
        film = (Film) getIntent().getSerializableExtra("Film");
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.no_image)
                .showImageOnFail(R.drawable.no_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        imgPoster = findViewById(R.id.img_poster);
        listSubtitle = findViewById(R.id.list_sub);
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        txtNoSub = findViewById(R.id.txt_no_sub);
        spnLanguage = findViewById(R.id.spinner);
        layoutContent = findViewById(R.id.content);
        httpService = new HttpService("SubDetailActivity", this);
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
        if (film.getServer() == ServerType.SUBSCENE)
            subscene = new Subscene(this);
        else if (film.getServer() == ServerType.YIFYSUBTITLE) {
            subscene = new YifySubtitles(this);
            String[] list = film.getUrl().split("/");
            String imdb = list[list.length-1];
            httpService.getFilmInformation(imdb);
        }
        else if (film.getServer() == ServerType.OPENSUBTITLE) {
            arraySubtitle = (ArrayList<Subtitle>) getIntent().getSerializableExtra("Subtitle");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    updateView(arraySubtitle);
                }
            },500);
        }
        if (subscene != null)
            subscene.searchSubsFromMovieName(film.getUrl(),"");

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
        if (subscene != null)
            subscene.release();
    }

    @Override
    public void onFoundFilm(String query, ArrayList<Film> films) {

    }

    @Override
    public void onFoundLinkDownload(String poster, String linkDownload, String detail, String url) {

    }

    @Override
    public void onFoundListSubtitle(final ArrayList<Subtitle> subtitles) {
        if (subtitles.size() > 0 )
            httpService.getFilmInformation(subtitles.get(0).getImdb());
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateView(subtitles);
            }
        });
    }

    private void updateView(ArrayList<Subtitle> subtitles) {
        progressBar.setVisibility(View.GONE);
        if (subtitles == null || subtitles.size() == 0){
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
            arraySubtitleFilter.add(subtitle);
        arraySubtitleFilter.add(0, new Subtitle("Release Name/Film title","Language","","",""));
        ImageLoader.getInstance().displayImage(subtitles.get(0).getPoster(),imgPoster, options, new SimpleImageLoadingListener());
        adapter = new SubtitleAdapter(mThis, arraySubtitleFilter);
        listSubtitle.setAdapter(adapter);
        adapter.setOnItemClickListener(SubDetailActivity.this);
        txtTitle.setText("Subtitles for "+ film.getName());
        ArrayList<Language> languageArrayList = new SharePreference(this).getLanguage();
        for (Language language : languageArrayList)
            languages.add(language.getName());
        languages.add(0, "All");
        spnLanguage.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,languages);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLanguage.setAdapter(aa);

    }

    private Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    private void filterLanguage(String language) {
        arraySubtitleFilter.clear();
        if (language.equals("")){
            for (Subtitle subtitle : arraySubtitle)
                    arraySubtitleFilter.add(subtitle);

        }else {

            for (Subtitle subtitle : arraySubtitle)
                if (subtitle.getLanguague().toLowerCase().equals(language.toLowerCase()))
                    arraySubtitleFilter.add(subtitle);
        }
        if (arraySubtitleFilter.size() > 0) {
            txtNoSub.setVisibility(View.GONE);
            listSubtitle.setVisibility(View.VISIBLE);
            arraySubtitleFilter.add(0, new Subtitle("Release Name/Film title", "Language", "", "", ""));
            adapter.notifyDataSetChanged();
        }else{
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

    @Override
    public void onGetHint(String query, ArrayList<Film> listHint) {

    }

    @Override
    public void onGetFilmInfo(final FilmInfo filmInfo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LinearLayout layoutInfo = findViewById(R.id.layout_info);
                TextView txtGenre = findViewById(R.id.txt_genre);
                TextView txtRating = findViewById(R.id.txt_rating);
                TextView txtRuntime = findViewById(R.id.txt_runtime);
                TextView txtCountry = findViewById(R.id.txt_country);
                TextView txtYear = findViewById(R.id.txt_year);
                if (filmInfo != null){
                    layoutInfo.setVisibility(View.VISIBLE);
                    txtGenre.setText(filmInfo.getGenre());
                    txtRating.setText(filmInfo.getRating()+ " ★");
                    txtRuntime.setText(filmInfo.getRunTime());
                    txtCountry.setText(filmInfo.getCountry());
                    txtYear.setText(filmInfo.getReleased());
                }
            }
        });

    }
}
