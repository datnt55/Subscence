package subscene.datnt.com.subscene.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.support.design.widget.Snackbar;
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
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.widget.DividerItemDecoration;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.adapter.SubtitleAdapter;

public class SubDetailActivity extends AppCompatActivity implements OnItemClickListener {
    private Film film;
    private DisplayImageOptions options;
    private ImageView imgPoster;
    private RecyclerView listSubtitle;
    private Context mThis;
    private TextView txtYear;
    private String year, poster;
    private Toolbar toolbar;
    private TextView txtTitle;

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
        toolbar = findViewById(R.id.toolbar);
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
        new SubDetailAsyn().execute(film.getUrl());

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
    }

    private class SubDetailAsyn extends AsyncTask<String, Void, ArrayList<Subtitle>> {
        private ProgressDialog dialog;
        public SubDetailAsyn() {
            dialog = new ProgressDialog(mThis);
            dialog.setMessage("Loading subtitle list...");
            dialog.setCancelable(false);
            dialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
        }

        @Override
        protected ArrayList<Subtitle> doInBackground(String... strings) {
            Document document = null;
            ArrayList<Subtitle> listSubtitle = new ArrayList<>();
            try {
                document = (Document) Jsoup.connect(strings[0]).get();
                if (document != null) {
                    //Get poster
                    Element posterElement = document.select("div[class=top left]").first();
                    poster = posterElement.select("a").attr("href");

                    //Get year
                    Element yearElement = document.select("div.header > ul > li").first();
                    String textYear = yearElement.select("strong").text();
                    year = yearElement.text().replaceFirst(textYear, "").trim();

                    Elements table = document.select("table > tbody > tr > td");
                    for (Element element : table) {
                        Element links = element.select("a").first();
                        if (links != null) {
                            String link = links.attr("href");
                            Elements contents = element.select("span");
                            String language = "";
                            String filmName = "";
                            if (contents.size() != 0) {
                                if (contents.get(0) != null)
                                    language = contents.get(0).text();
                                if (contents.get(1) != null)
                                    filmName = contents.get(1).text();
                                Subtitle subtitle = new Subtitle(filmName,language,poster,link, year);
                                listSubtitle.add(subtitle);
                            }

                        }


                    }
                }

            } catch (HttpStatusException e) {
                e.printStackTrace();
                return  null;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listSubtitle;
        }

        @Override
        protected void onPostExecute(ArrayList<Subtitle> subtitles) {
            super.onPostExecute(subtitles);
            if (subtitles == null){
                Snackbar snackbar = Snackbar
                        .make(findViewById(R.id.root), "Oops, Fetching data failure ", Snackbar.LENGTH_LONG)
                        .setAction("Try again", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                new SubDetailAsyn().execute(film.getUrl());
                            }
                        });

                snackbar.show();
                return;
            }
            arraySubtitle = subtitles;
            arraySubtitle.add(0, new Subtitle("Release Name/Film title","Language","","",""));
            ImageLoader.getInstance().displayImage(poster,imgPoster, options, new
                    SimpleImageLoadingListener());
            SubtitleAdapter adapter = new SubtitleAdapter(mThis, arraySubtitle);
            listSubtitle.setAdapter(adapter);
            txtYear.setText(getSpannedText(mThis.getResources().getString(R.string.film_year, year)));
            adapter.setOnItemClickListener(SubDetailActivity.this);
            txtTitle.setText("Subtitles for "+ film.getName());
            dialog.dismiss();
        }
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
