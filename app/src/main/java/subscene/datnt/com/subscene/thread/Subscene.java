package subscene.datnt.com.subscene.thread;

import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UncheckedIOException;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.model.SubtitleDetail;

/**
 * Created by DatNT on 4/5/2018.
 */

public class Subscene extends SubServer{
    private final Handler handler;
    private static final String TAG = "Subscene";
    private OnSceneListener listener;
    public Subscene(OnSceneListener listener) {
        this.listener = listener;
        final HandlerThread handlerThread = new HandlerThread(TAG);
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    @Override
    public void getMovieSubsByName(final String moviename, final String language){
        handler.post(new Runnable() {
            @Override
            public void run() {
                innerGetMovieSubsByName(moviename,language);
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

    @Override
    public void searchSubsFromMovieName(final String url, final String language) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                innerSearchSubsFromMovie(url,language);
            }
        });
    }

    private void innerSearchSubsFromMovie(String url ,String language) {
        Document document = null;
        ArrayList<Subtitle> listSubtitle = new ArrayList<>();
        try {
            Log.e("URL", url);
            if (!language.equals("")){
                url = url +"/"+language;
            }
            document = (Document) Jsoup.connect(url).get();
            if (document != null) {
                //Get poster
                Element posterElement = document.select("div[class=top left]").first();
                String poster = posterElement.select("a").attr("href");

                //Get year
                Element yearElement = document.select("div.header > ul > li").first();
                String textYear = yearElement.select("strong").text();
                String year = yearElement.text().replaceFirst(textYear, "").trim();

                Elements table = document.select("table > tbody > tr > td");
                for (Element element : table) {
                    Element links = element.select("a").first();
                    if (links != null) {
                        String link = links.attr("href");
                        Elements contents = element.select("span");
                        String lang = "";
                        String filmName = "";
                        if (contents.size() != 0) {
                            if (contents.get(0) != null)
                                lang = contents.get(0).text();
                            if (contents.get(1) != null)
                                filmName = contents.get(1).text();
                            Subtitle subtitle = new Subtitle(filmName, lang, poster, link, year);
                            listSubtitle.add(subtitle);
                        }

                    }


                }
            }

        } catch (HttpStatusException e) {
            e.printStackTrace();
            if (listener != null)
                listener.onFoundListSubtitle(listSubtitle);
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null)
                listener.onFoundListSubtitle(listSubtitle);
        } catch (UncheckedIOException e) {
            e.printStackTrace();
            if (listener != null)
                listener.onFoundListSubtitle(listSubtitle);
        }
        if (listener != null)
            listener.onFoundListSubtitle(listSubtitle);
    }

    private void innerGetMovieSubsByName(String movieName, String language) {
        Document document = null;
        ArrayList<Film> listArticle = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect("https://subscene.com/subtitles/title?q=" + movieName + "&l=").get();
            if (document != null) {
                Elements result = document.select("div.search-result");
                Elements titles = result.select("h2");
                if (titles.first().text().equals("No results found")) {
                    if (listener != null)
                        listener.onFoundFilm(movieName, listArticle);
                    return;
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
                        Film article = new Film(name, links, subCount);
                        article.setType(type);
                        listArticle.add(article);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (listener != null)
            listener.onFoundFilm(movieName, listArticle);
    }

    private void innerGetLinkDownloadSubtitle(String url) {
        Document document = null;
        String poster = "";
        String linkDownload = "";
        String detail = "";
        String preview = "";
        ArrayList<SubtitleDetail> subDetails = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect(url).get();
            if (document != null) {
                //Get poster
                Element posterElement = document.select("div[class=top left]").first();
                poster = posterElement.select("a").attr("href");
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
                            for (Element d : details) {
                                detail += d.html() + "<br>";
                            }
                        } else if (attribute.equals("preview")) {
                            preview = windows.select("p").html();
                        }
                    }
                }

            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (listener != null)
            listener.onFoundLinkDownload(poster, linkDownload,detail,preview);
    }


    public void release(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                handler.getLooper().quit();
            }
        });
    }
}
