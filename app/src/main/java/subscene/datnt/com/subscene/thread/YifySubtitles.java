package subscene.datnt.com.subscene.thread;

import android.os.Handler;
import android.os.HandlerThread;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.YiFyFilm;

/**
 * Created by DatNT on 4/5/2018.
 */

public class YifySubtitles extends SubServer {
    private final Handler handler;
    private static final String TAG = "YifySubtitles";
    private OnSceneListener listener;
    public YifySubtitles(OnSceneListener listener) {
        this.listener = listener;
        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void getMovieSubsByName(final String movieName, final String language) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                innerGetMovieSubsByName(movieName,language);
            }
        });
    }

    private void innerGetMovieSubsByName(String movieName, String language) {
        Document document = null;
        ArrayList<Film> listFilm = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect("https://www.yifysubtitles.com/search?q="+movieName).get();
            if (document != null) {
                Elements result = document.select("div.col-sm-12> ul > li");
                for (Element element : result){
                    Element elink = element.select("div[class=media-left media-middle] > a").first();
                    String link = elink.attr("href");
                    Element eposter = element.select("div[class=media-left media-middle] > a > img").first();
                    String poster = eposter.attr("src");
                    Element content = element.select("div.media-body >a").first();
                    String name = content.select("div.col-xs-12 > h3").text();
                    Elements infos = content.select("div[class=col-sm-6 col-xs-12 movie-genre]");
                    String year ="", duration ="";
                    for (Element info : infos){
                        String a = info.select("span").text();
                        if (!a.equals("")){
                            Elements section = info.select("span");
                            String suffix = section.get(0).select("span > small").text();
                            year = section.get(0).text().replaceAll(suffix,"");
                            suffix = section.get(1).select("span > small").text();
                            duration = section.get(1).text().replaceAll(suffix,"");
                        }
                    }
                    Elements extraInfo = content.select("div.col-xs-12");
                    String actor = extraInfo.get(3).select("span").first().text();
                    String description = extraInfo.get(4).select("span").first().text();
                    listFilm.add(new YiFyFilm(name,link, duration, year, actor, description));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (listener != null)
            listener.onFoundFilm(movieName, listFilm);
    }

    @Override
    public void getLinkDownloadSubtitle(String url) {

    }

    @Override
    public void searchSubsFromMovieName(String url, String language) {

    }
}
