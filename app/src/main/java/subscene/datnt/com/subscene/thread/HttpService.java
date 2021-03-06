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
import subscene.datnt.com.subscene.model.FilmInfo;
import subscene.datnt.com.subscene.model.MovieHint;
import subscene.datnt.com.subscene.utils.Globals;
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
                innerGetHintOMData(movie);
            }
        });
    }

    public void getFilmInformation(final String movie){
        handler.post(new Runnable() {
            @Override
            public void run() {
                innerGetFilmInformation(movie);
            }
        });
    }

    private void innerGetFilmInformation(String movie) { ArrayList<Film> listHint = new ArrayList<>();
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(3000, TimeUnit.MILLISECONDS);
        OkHttpClient client = builder.build();
        HttpUrl.Builder httpBuider = HttpUrl.parse(Globals.URL_OMDB).newBuilder();
        httpBuider.addQueryParameter("i", movie);
        httpBuider.addQueryParameter("apikey", "d05b362f");
        Request request = new Request.Builder().url(httpBuider.build()).build();

        try {
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject data = new JSONObject(jsonData);
            String title = data.getString("Title");
            String Year = data.getString("Year");
            String Released = data.getString("Released");
            String Runtime = data.getString("Runtime");
            String Genre = data.getString("Genre");
            String Country = data.getString("Country");
            String imdbRating = data.getString("imdbRating");
            FilmInfo filmInfo = new FilmInfo(title,Year,Released,Runtime,Genre,Country,imdbRating);
            if (listener != null)
                listener.onGetFilmInfo(filmInfo);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        handler.getLooper().quit();
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
                listHint.add(new Film(ServerType.YIFYSUBTITLE, "https://www.yifysubtitles.com/movie-imdb/"+json.getString("movie"),json.getString("imdb")));
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

    public void innerGetHintOMData(String movie){
        ArrayList<Film> listHint = new ArrayList<>();
        OkHttpClient.Builder builder = new OkHttpClient.Builder().connectTimeout(3000, TimeUnit.MILLISECONDS);
        OkHttpClient client = builder.build();
        HttpUrl.Builder httpBuider = HttpUrl.parse(Globals.URL_OMDB).newBuilder();
        httpBuider.addQueryParameter("s", movie);
        httpBuider.addQueryParameter("apikey", "d05b362f");
        Request request = new Request.Builder().url(httpBuider.build()).build();

        try {
            Response response = client.newCall(request).execute();
            String jsonData = response.body().string();
            JSONObject data = new JSONObject(jsonData);

            JSONArray jsonArray = data.getJSONArray("Search");
            for (int i = 0 ; i < jsonArray.length(); i++){
                JSONObject json = jsonArray.getJSONObject(i);
                Film film = new Film(ServerType.YIFYSUBTITLE, json.getString("Title"),json.getString("imdbID"));
                film.setPoster(json.getString("Poster"));
                listHint.add(film);
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
        void onGetFilmInfo(FilmInfo filmInfo);
    }
}
