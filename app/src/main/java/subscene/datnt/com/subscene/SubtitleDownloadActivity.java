package subscene.datnt.com.subscene;

import android.animation.ValueAnimator;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
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

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import co.ceryle.segmentedbutton.SegmentedButtonGroup;

public class SubtitleDownloadActivity extends AppCompatActivity {
    private Subtitle subtitle;
    private Film film;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis = this;
        setContentView(R.layout.activity_subtitle_download);
        subtitle = (Subtitle) getIntent().getSerializableExtra("Subtitle");
        film = (Film) getIntent().getSerializableExtra("Film");

        imgPoster = findViewById(R.id.img_poster);
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .showImageOnFail(R.mipmap.ic_launcher)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(subtitle.getPoster(), imgPoster, options, new
                SimpleImageLoadingListener());
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(film.getName());
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
        new SubDetailAsyn().execute(subtitle.getLink());
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

    private class SubDetailAsyn extends AsyncTask<String, Void, ArrayList<SubtitleDetail>> {
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
        protected ArrayList<SubtitleDetail> doInBackground(String... strings) {
            Document document = null;
            ArrayList<SubtitleDetail> subDetails = new ArrayList<>();
            try {
                document = (Document) Jsoup.connect(strings[0]).get();
                if (document != null) {
                    //Get link download
                    Elements download = document.select("div.download");
                    linkDownload = download.select("a").attr("href");
                    //Get detail
                    Elements windows = document.select("div.details > div.window");
                    if (windows.size() > 0) {
                        for (Element window : windows) {
                            String attribute = window.attr("id");
                            if (attribute.equals("details")) {
                                Elements details = windows.select("ul > li");
                                String content = "";
                                for (Element detail : details) {
                                    content += detail.html() + "<br>";
                                }
                                subDetails.add(new SubtitleDetail(attribute, content));
                            } else if (attribute.equals("preview")) {
                                String details = windows.select("p").html();
                                subDetails.add(new SubtitleDetail(attribute, details));
                            }
                        }
                    }

                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return subDetails;
        }

        @Override
        protected void onPostExecute(ArrayList<SubtitleDetail> subtitleDetails) {
            super.onPostExecute(subtitleDetails);
            arraySubDetail = subtitleDetails;
            for (SubtitleDetail detail : subtitleDetails) {
                if (detail.getId().equals("details")) {
                    TextView text = new TextView(mThis);
                    text.setText(getSpannedText(detail.getContent()));
                    subDetail.addView(text);
                    currentHeight = getMeasureOfView(cardView);
                }
            }
            dialog.dismiss();
        }
    }

    private void UpdateDetailSub(String status) {
        subDetail.removeAllViews();
        int newHeight = 0;
        boolean isExist = false;
        for (SubtitleDetail detail : arraySubDetail)
            if (detail.getId().equals(status)) {
                TextView text = new TextView(mThis);
                text.setText(getSpannedText(detail.getContent()));
                subDetail.addView(text);
                isExist = true;
                newHeight = getMeasureOfView(cardView);

            }
        if (!isExist){
            TextView text = new TextView(mThis);
            text.setText("No have "+status);
            subDetail.addView(text);
            newHeight = getMeasureOfView(cardView);
        }
        ValueAnimator mAnimator = ValueAnimator.ofFloat(currentHeight, newHeight);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final Float fraction = (Float) animation.getAnimatedValue();
                int x = fraction.intValue();
                cardView.getLayoutParams().height= x;
                cardView.requestLayout();
            }
        });

        mAnimator.setDuration(500);
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

    public void DownloadSubtitle(View view){
        new DownloadFileFromURL().execute("https://subscene.com"+linkDownload);
    }

    public class DownloadFileFromURL extends AsyncTask<String, Integer, String> {
        private ProgressDialog dialog;
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
         * */

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
         * */
        @Override
        protected String doInBackground(String... f_url) {
            InputStream input = null;
            OutputStream output = null;
            HttpURLConnection connection = null;
            try {
                URL url = new URL(f_url[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                String a = FilenameUtils.getExtension(url.getPath());
                String b = FilenameUtils.getBaseName(url.getPath());
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
                output = new FileOutputStream("/sdcard/file_name.rar");

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
         * */
        protected void onProgressUpdate(Integer... progress) {
            // setting progress percentage
            dialog.setProgress(progress[0]);
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String result) {
            // dismiss the dialog after the file was downloaded
            mWakeLock.release();
            dialog.dismiss();
            if (result != null)
                Toast.makeText(mThis,"Download error: "+result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(mThis,"LocalFile downloaded", Toast.LENGTH_SHORT).show();
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
