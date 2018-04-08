package subscene.datnt.com.subscene.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.model.YiFyFilm;

import static subscene.datnt.com.subscene.utils.ServerType.YIFYSUBTITLE;

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
    @Override
    public void getLinkDownloadSubtitle(final String url) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                innerGetLinkDownloadSubtitle(url);
            }
        });
    }

    private void innerGetLinkDownloadSubtitle(String url) {
        Document document = null;
        try {
            Log.e("TAG",url);
            document = (Document) Jsoup.connect(url).get();
            if (document != null) {
                Element result = document.select("div.col-xs-12> a.btn-icon.download-subtitle").first();
                String download = result.attr("href");
                if (listener != null)
                    listener.onFoundLinkDownload("",download,"","");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void searchSubsFromMovieName(final String url, final String language) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                innerSearchSubsFromMovieName(url, language);
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
                    listFilm.add(new YiFyFilm(YIFYSUBTITLE, name,"https://www.yifysubtitles.com"+link, duration, year, actor, description,"https://"+poster));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (listener != null)
            listener.onFoundFilm(movieName, listFilm);
    }

    private void innerSearchSubsFromMovieName(String url,String language) {
        Document document = null;
        ArrayList<Subtitle> listSub = new ArrayList<>();
        try {
            Log.e("TAG",url);
            document = (Document) Jsoup.connect(url).get();
            if (document != null) {
                String year = document.select("div#circle-score-year").first().attr("data-text");
                String poster = document.select("a.slide-item-wrap > img").first().attr("src");
                Elements result = document.select("div.table-responsive> table > tbody > tr");
                for (Element element : result){
                    String lang = "";;
                    String name = "";;
                    String download = "";
                    Elements subs = element.select("td");
                    for (Element sub : subs){
                        String a = sub.attr("class");
                        Log.e("TD",a);
                        if (sub.attr("class").equals("flag-cell")){
                            lang = sub.select("span").get(1).text();
                        }else if (sub.attr("class").equals("download-cell")){
                            download = element.select("a").first().attr("href");
                        }else if (sub.attr("class").equals("")){
                            String ag =  download = sub.select("a").text();
                            String span = element.select("a > span").first().text();
                            name = sub.select("a").text().replaceAll(span,"");
                        }
                    }
                    listSub.add(new Subtitle(name, lang, "https://"+poster,"https://www.yifysubtitles.com"+download, year));
                }
                if (listener != null)
                    listener.onFoundListSubtitle(listSub);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }



    @Override
    public void release() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.getLooper().quit();
            }
        });
    }
}
