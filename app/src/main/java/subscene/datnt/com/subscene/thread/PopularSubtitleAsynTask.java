package subscene.datnt.com.subscene.thread;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import subscene.datnt.com.subscene.model.PopularFilm;

/**
 * Created by DatNT on 3/30/2018.
 */

public class PopularSubtitleAsynTask extends AsyncTask<Void, Void, ArrayList<PopularFilm>> {
    private String language;
    private OnSearchPopolarFilmListener listener;

    public PopularSubtitleAsynTask(String language , OnSearchPopolarFilmListener listener) {
        this.language = language;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }
        @Override
    protected ArrayList<PopularFilm> doInBackground(Void... strings) {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        final Request original = chain.request();
                        final Request authorized = original.newBuilder()
                                .addHeader("Cookie", "LanguageFilter=45")
                                .build();
                        return chain.proceed(authorized);
                    }
                })
                .build();
        Request request = new Request.Builder()
                .url("https://subscene.com/browse/popular/series/1")
                .build();
        try {
            Response response = client.newCall(request).execute();
            ArrayList<PopularFilm> popularFilms = new ArrayList<>();
            Document document = (Document)Jsoup.parse(response.body().string());
            if (document != null) {
                Elements result = document.select("table > tbody >tr");
                for (Element element : result) {
                    Elements sub = element.select("td");
                    String link = "",name = "",download = "",date = "";
                    for (Element td : sub) {
                        String attribute = td.attr("class");

                        if (attribute.equals("a1")) {
                            Element links = td.select("a").first();
                            link = links.attr("href");
                            name = td.select("div.visited").text();
                        }else if (attribute.equals("a6")) {
                            date =  td.text();
                        }else if (attribute.equals("a7")) {
                            download = td.text();
                        }
                    }
                    popularFilms.add(new PopularFilm(name,link,date,download));
                }
                return popularFilms;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(ArrayList<PopularFilm> films) {
        super.onPostExecute(films);
        if (listener != null)
            listener.onSearchSuccess(films);

    }


    public interface OnSearchPopolarFilmListener{
        void onSearchSuccess(ArrayList<PopularFilm> link);
    }
}