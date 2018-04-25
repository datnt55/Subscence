package subscene.datnt.com.subscene.thread;

import android.os.AsyncTask;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.utils.ServerType;

import static subscene.datnt.com.subscene.utils.ServerType.SUBSCENE;

/**
 * Created by DatNT on 3/30/2018.
 */

public class SeachFilmAsynTask extends AsyncTask<String, Void, ArrayList<Film>> implements SeachSubAsynTask.OnSearchSubListener {
    private String language;
    private OnSearchFilmListener listener;
    public SeachFilmAsynTask(String language, OnSearchFilmListener listener) {
        this.language = language;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        listener.onStartSearch();
    }

    @Override
    protected ArrayList<Film> doInBackground(String... strings) {
        Document document = null;
        ArrayList<Film> listArticle = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {
                Elements result = document.select("div.search-result");
                Elements titles = result.select("h2");
                if (titles.first().text().equals("No results found")) {
                    return listArticle;
                }
                for (int i = 0; i < titles.size(); i++) {
                    Element sub = document.select("div.search-result > ul").get(i);
                    Elements item = sub.select("li");
                    String type = titles.get(i).select("h2").text();
                    for (Element element : item) {
                        Element link = element.select("a").first();
                        String links = link.attr("href");
                        String name = link.text();
                        String subCount = element.select("div[class=subtle count]").text();
                        Film article = new Film(SUBSCENE, name, "https://subscene.com"+links, subCount);
                        article.setType(type);
                        listArticle.add(article);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return listArticle;
    }

    @Override
    protected void onPostExecute(ArrayList<Film> films) {
        super.onPostExecute(films);
        if (films.size() == 0) {
            listener.onSearchNoResult();
        } else {
            new SeachSubAsynTask(this).execute(films.get(0).getUrl()+"/"+language);
        }

    }

    @Override
    public void onSearchSubtitleSuccess(String link) {
        if (listener != null)
            listener.onSearchSuccess(link);
    }

    public interface OnSearchFilmListener{
        void onStartSearch();
        void onSearchNoResult();
        void onSearchSuccess(String link);
    }
}