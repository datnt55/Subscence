package subscene.datnt.com.subscene.activity;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.apache.commons.io.FilenameUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;
import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.model.FilmInfo;
import subscene.datnt.com.subscene.model.PopularFilm;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.model.SubtitleDetail;
import subscene.datnt.com.subscene.model.YiFyFilm;
import subscene.datnt.com.subscene.thread.HttpService;
import subscene.datnt.com.subscene.thread.OpenSubtitle;
import subscene.datnt.com.subscene.thread.SubServer;
import subscene.datnt.com.subscene.thread.Subscene;
import subscene.datnt.com.subscene.thread.YifySubtitles;
import subscene.datnt.com.subscene.utils.Decompress;
import subscene.datnt.com.subscene.utils.FileUtil;
import subscene.datnt.com.subscene.utils.ServerType;
import subscene.datnt.com.subscene.widget.FilePickerBottomSheet;

import static subscene.datnt.com.subscene.utils.Globals.APP_FOLDER;

public class SubtitleDownloadActivity extends AppCompatActivity implements FilePickerBottomSheet.FilePickerListener,OnSceneListener, HttpService.HttpResponseListener {
    private Subtitle subtitle;
    private Film film;
    private PopularFilm popularFilm;
    private LinearLayout subDetail;
    private Context mThis;
    private ImageView imgPoster;
    private DisplayImageOptions options;
    private Toolbar toolbar;
    private SegmentedButtonGroup segmentedButtonGroup;
    private ArrayList<SubtitleDetail> arraySubDetail = new ArrayList<>();
    private String linkDownload;
    private int currentHeight = 0;
    private CardView cardView;
    public ArrayList<File> extractFile;
    private RelativeLayout layoutShadow;
    private FilePickerBottomSheet mBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout layoutContent;
    private ProgressBar progressBar;
    private ScrollView scrollView;
    private SubServer subscene;
    private TextView txtDescription;
    private String imdb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis = this;
        setContentView(R.layout.activity_subtitle_download);
        if (getIntent().hasExtra("PopularFilm")){
            popularFilm = (PopularFilm) getIntent().getSerializableExtra("PopularFilm");
        }else{
            subtitle = (Subtitle) getIntent().getSerializableExtra("Subtitle");
            film = (Film) getIntent().getSerializableExtra("Film");
        }
        if (getIntent().hasExtra("imdb")){
            imdb = getIntent().getStringExtra("imdb");
            if (!imdb.equals("")){
                HttpService httpService = new HttpService("SubtitleDownloadActivity", SubtitleDownloadActivity.this);
                httpService.getFilmInformation(imdb);
            }
        }

        layoutContent = findViewById(R.id.layout_content);
        txtDescription = findViewById(R.id.txt_filmname);
        progressBar = findViewById(R.id.progressBar);
        imgPoster = findViewById(R.id.img_poster);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.loading)
                .showImageForEmptyUri(R.drawable.no_image)
                .showImageOnFail(R.drawable.no_image)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            if (film != null)
                getSupportActionBar().setTitle(film.getName());
            else
                getSupportActionBar().setTitle(popularFilm.getName());
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        subDetail = findViewById(R.id.sub_detail);
        segmentedButtonGroup = findViewById(R.id.segment_button);
        segmentedButtonGroup.setOnPositionChangedListener(new SegmentedButtonGroup.OnPositionChangedListener() {
            @Override
            public void onPositionChanged(int position) {
                changeDetails(position);
            }
        });
        cardView = findViewById(R.id.card_view);
        scrollView = findViewById(R.id.scroll);
        ServerType serverType = film != null ? film.getServer() : popularFilm.getServer();
        if (serverType == ServerType.SUBSCENE)
            subscene = new Subscene(this);
        else if (serverType == ServerType.YIFYSUBTITLE) {
            subscene = new YifySubtitles(this);
            try {
                YiFyFilm yiFyFilm = (YiFyFilm) film;
                txtDescription.setText(yiFyFilm.getDescription());
            }catch (Exception e){
                txtDescription.setText("");
            }
        } else if (serverType == ServerType.OPENSUBTITLE) {
            //subscene = new OpenSubtitle(this);
            updateView("",subtitle.getLink(),"","");
        }
        if (subscene != null) {
            if (subtitle != null)
                subscene.getLinkDownloadSubtitle(subtitle.getLink());
            else
                subscene.getLinkDownloadSubtitle(popularFilm.getUrl());
        }
        initLayoutMigrateSub();
    }

    private void initLayoutMigrateSub() {
        layoutShadow = findViewById(R.id.shadow);
        mBottomSheet = findViewById(R.id.file_picker_bottom_sheet);
        mBottomSheet.setOnFilePickerListener(this);
        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.setHideable(true);
        //bottomSheetBehavior.setPeekHeight(126);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    layoutShadow.setVisibility(View.VISIBLE);
                else if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    layoutShadow.setVisibility(View.GONE);
                else if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    layoutShadow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    private void changeDetails(int position) {
        switch (position) {
            case 0:
                UpdateDetailSub("details");
                break;
            case 1:
                UpdateDetailSub("preview");
                break;
        }
    }

    private void updateView(String poster, String linkDownload, String detail, String preview) {
        if (subtitle != null)
            poster = subtitle.getPoster();
        ImageLoader.getInstance().displayImage(poster, imgPoster, options, new SimpleImageLoadingListener());
        layoutContent.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        arraySubDetail.add(new SubtitleDetail("details", detail));
        arraySubDetail.add(new SubtitleDetail("preview", preview));
        for (SubtitleDetail d : arraySubDetail) {
            if (d.getId().equals("details")) {
                TextView text = new TextView(mThis);
                String content = d.getContent().equals("")?"No have details" : d.getContent();
                text.setText(getSpannedText(content));
                subDetail.addView(text);
                currentHeight = getMeasureOfView(cardView);
            }
        }
        this.linkDownload = linkDownload;
    }

    private void UpdateDetailSub(String status) {
        subDetail.removeAllViews();
        int newHeight = 0;
        boolean isExist = false;
        for (SubtitleDetail detail : arraySubDetail)
            if (detail.getId().equals(status)) {
                TextView text = new TextView(mThis);
                String content = detail.getContent().equals("")?"No have "+status : detail.getContent();
                text.setText(getSpannedText(content));
                subDetail.addView(text);
                isExist = true;
                newHeight = getMeasureOfView(cardView);

            }

        final int[] current = {currentHeight};
        ValueAnimator mAnimator = ValueAnimator.ofFloat(currentHeight, newHeight);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final Float fraction = (Float) animation.getAnimatedValue();
                int x = fraction.intValue();
                cardView.getLayoutParams().height = x;
                cardView.requestLayout();
                scrollView.smoothScrollTo(current[0], x);
                current[0] = x;
            }
        });

        mAnimator.setDuration(300);
        mAnimator.start();
        currentHeight = newHeight;

    }

    public static int getMeasureOfView(final View view) {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        return view.getMeasuredHeight();
    }

    private Spanned getSpannedText(String text) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT);
        } else {
            return Html.fromHtml(text);
        }
    }

    @Override
    public void onCopy(String fileName, File path) {
        for (File file : extractFile) {
            String extension = FilenameUtils.getExtension(file.getAbsolutePath());
            FileUtil.move(fileName + "." + extension, file, path);
        }
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onCancelCopy() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onFoundFilm(String query, ArrayList<Film> films) {

    }

    @Override
    public void onFoundLinkDownload(final String poster, final String dbimdb, final String linkDownload, final String detail, final String url) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                updateView(poster, linkDownload, detail, url);
                if (imdb == null) {
                    HttpService httpService = new HttpService("SubtitleDownloadActivity", SubtitleDownloadActivity.this);
                    httpService.getFilmInformation(dbimdb);
                }
            }
        });

    }

    @Override
    public void onFoundListSubtitle(ArrayList<Subtitle> listSubtitle) {

    }

    public void DownloadSubtitle(View view) {
        new DownloadFileFromURL().execute(linkDownload);
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

    public class DownloadFileFromURL extends AsyncTask<String, Integer, String> {
        private ProgressDialog dialog;
        private String fileName = "";
        private PowerManager.WakeLock mWakeLock;

        public DownloadFileFromURL() {
            dialog = new ProgressDialog(mThis);
            dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            dialog.setIndeterminate(false);
            dialog.setTitle("Subtitle");
            dialog.setMessage("Downloading...");
        }

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         */

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.show();
            PowerManager pm = (PowerManager) mThis.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
        }

        /**
         * Downloading file in background thread
         */
        @Override
        protected String doInBackground(String... f_url) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(f_url[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                long length = Long.parseLong(connection.getHeaderField("Content-Length"));
                String type = connection.getHeaderField("Content-Type");
                String content = connection.getHeaderField("Content-Disposition");
                if (content != null && content.indexOf("=") != -1) {
                    fileName = content.split("=")[1]; // getting value after '='
                    fileName = fileName.replaceAll("\"", "").replaceAll("]", "");
                }
                // expect HTTP 200 OK, so we don't mistakenly save error report
                // instead of the file
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    return "Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage();
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(new File(APP_FOLDER , fileName));

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return null;
                    }
                    total += count;
                    // publishing the progress....
                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
            } catch (Exception e) {
                return e.toString();
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ignored) {
                }

                if (connection != null)
                    connection.disconnect();
            }
            return null;
        }

        /**
         * Updating progress bar
         */
        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            dialog.setProgress(progress[0]);
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         **/
        @Override
        protected void onPostExecute(String result) {
            // dismiss the dialog after the file was downloaded
            mWakeLock.release();
            dialog.dismiss();
            if (result != null)
                Toast.makeText(mThis, "Download error: " + result, Toast.LENGTH_LONG).show();
            else {
                //Toast.makeText(mThis, fileName + " downloaded", Toast.LENGTH_SHORT).show();
                if (FilenameUtils.getExtension(new File(APP_FOLDER, fileName).getAbsolutePath()).equals("rar"))
                    extractFile = Decompress.extractArchive(APP_FOLDER+ "/" + fileName, APP_FOLDER);
                else  if (FilenameUtils.getExtension(new File(APP_FOLDER, fileName).getAbsolutePath()).equals("zip"))
                    try {
                        extractFile = Decompress.unzip(APP_FOLDER+ "/" + fileName, APP_FOLDER);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                else  if (FilenameUtils.getExtension(new File(APP_FOLDER, fileName).getAbsolutePath()).equals("gz"))
                    try {
                        extractFile = Decompress.unGZip(APP_FOLDER+ "/" + fileName, APP_FOLDER);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                new File(APP_FOLDER, fileName).delete();
                moveSubtitleToVideoFolder();
            }

        }

    }

    private void moveSubtitleToVideoFolder() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("");
        builder.setMessage("Do you want to move this subtitle to folder of compatible video?");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                if (subscene != null)
                    subscene.release();
                Intent intent = new Intent(mThis, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | IntentCompat.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
        builder.show();

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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                mBottomSheet.onBackPressed();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
