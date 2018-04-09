package subscene.datnt.com.subscene.thread;

import android.os.Handler;
import android.os.HandlerThread;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.MovieHint;
import subscene.datnt.com.subscene.utils.ServerType;

/**
 * Created by DatNT on 4/5/2018.
 */

public class HttpService {
    private final Handler handler;
    private static final String TAG = "HttpService";
    private HttpResponseListener listener;
    public HttpService(String tag, HttpResponseListener listener) {
        this.listener = listener;
        final HandlerThread handlerThread = new HandlerThread(tag);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }


    public void getHintData(final String movie){
        handler.post(new Runnable() {
            @Override
            public void run() {
                innerGetHintData(movie);
            }
        });
    }

    public void innerGetHintData(String movie){
        ArrayList<Film> listHint = new ArrayList<>();
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(3000, TimeUnit.MILLISECONDS);
        OkHttpClient client = builder.build();
        HttpUrl.Builder httpBuider = HttpUrl.parse("https://www.yifysubtitles.com/ajax_search.php").newBuilder();
        httpBuider.addQueryParameter("mov", movie);
        Request request = new Request.Builder().url(httpBuider.build()).build();

        try {
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0 ; i < jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);
                listHint.add(new Film(ServerType.YIFYSUBTITLE, json.getString("movie"),"https://www.yifysubtitles.com/movie-imdb/"+json.getString("imdb")));
            }
            // Do something with the response.
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (listener != null)
            listener.onGetHint(movie, listHint);
        handler.getLooper().quit();
    }

    public interface HttpResponseListener{
        void onGetHint(String query, ArrayList<Film> listHint);
    }
}
