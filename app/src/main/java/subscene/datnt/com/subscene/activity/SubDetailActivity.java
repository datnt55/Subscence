package subscene.datnt.com.subscene.activity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.Build;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.ArrayList;

import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.thread.OpenSubtitle;
import subscene.datnt.com.subscene.thread.SubServer;
import subscene.datnt.com.subscene.thread.Subscene;
import subscene.datnt.com.subscene.thread.YifySubtitles;
import subscene.datnt.com.subscene.utils.ServerType;
import subscene.datnt.com.subscene.widget.DividerItemDecoration;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.adapter.SubtitleAdapter;

public class SubDetailActivity extends AppCompatActivity implements OnItemClickListener, OnSceneListener {
    private Film film;
    private DisplayImageOptions options;
    private ImageView imgPoster;
    private RecyclerView listSubtitle;
    private Context mThis;
    private TextView txtYear;
    private Toolbar toolbar;
    private TextView txtTitle;
    private ProgressBar progressBar;
    private SubServer subscene;
    private TextView txtNoSub;
    private NestedScrollView layoutContent;

    private ArrayList<Subtitle> arraySubtitle = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_detail);
        mThis = this;
        film = (Film) getIntent().getSerializableExtra("Film");
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
        if (film.getServer() == ServerType.SUBSCENE)
            subscene = new Subscene(this);
        else if (film.getServer() == ServerType.YIFYSUBTITLE)
            subscene = new YifySubtitles(this);
        else if (film.getServer() == ServerType.OPENSUBTITLE)
            subscene = new OpenSubtitle(this);
        subscene.searchSubsFromMovieName(film.getUrl(),"vietnamese");

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
        intent.putExtra("Subtitle", arraySubtitle.get(position));
        intent.putExtra("Film", film);
        startActivity(intent);
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
        arraySubtitle.add(0, new Subtitle("Release Name/Film title","Language","","",""));
        ImageLoader.getInstance().displayImage(subtitles.get(1).getPoster(),imgPoster, options, new SimpleImageLoadingListener());
        SubtitleAdapter adapter = new SubtitleAdapter(mThis, arraySubtitle);
        listSubtitle.setAdapter(adapter);
        txtYear.setText(getSpannedText(mThis.getResources().getString(R.string.film_year, subtitles.get(1).getYear())));
        adapter.setOnItemClickListener(SubDetailActivity.this);
        txtTitle.setText("Subtitles for "+ film.getName());
    }

    private Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
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
}
