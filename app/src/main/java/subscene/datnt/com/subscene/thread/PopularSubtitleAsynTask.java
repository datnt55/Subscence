package subscene.datnt.com.subscene.thread;

import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.CookieManager;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import subscene.datnt.com.subscene.model.PopularFilm;
import subscene.datnt.com.subscene.utils.ServerType;

import static subscene.datnt.com.subscene.utils.ServerType.SUBSCENE;

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
            URL url = null;
            try {
                url = new URL("https://subscene.com/browse/popular/series/1");
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Cookie", "LanguageFilter=45");
                // Send the request to the server
                //conn.connect();

                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = "";
                StringBuilder responseOutput = new StringBuilder();
                System.out.println("output===============" + br);
                while((line = br.readLine()) != null ) {
                    responseOutput.append(line);
                }
                br.close();
                String a = responseOutput.toString();
                System.out.println("Response : -- " + responseOutput.toString());
                ArrayList<PopularFilm> popularFilms = new ArrayList<>();
                Document document = (Document)Jsoup.parse(responseOutput.toString());
                if (document != null) {
                    Elements result = document.select("table > tbody >tr");
                    for (Element element : result) {
                        Elements sub = element.select("td");
                        String link = "", name = "", download = "", date = "";
                        for (Element td : sub) {
                            String attribute = td.attr("class");

                            if (attribute.equals("a1")) {
                                Element links = td.select("a").first();
                                link = "https://subscene.com"+links.attr("href");
                                name = td.select("div.visited").text();
                            } else if (attribute.equals("a6")) {
                                date = td.text();
                            } else if (attribute.equals("a7")) {
                                download = td.text();
                            }
                        }
                        popularFilms.add(new PopularFilm(SUBSCENE, name, link, date, download));
                    }
                    return popularFilms;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }catch (Exception e) {
                e.printStackTrace();
                return null;
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