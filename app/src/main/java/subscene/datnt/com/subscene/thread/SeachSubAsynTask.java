package subscene.datnt.com.subscene.thread;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.activity.SubDetailActivity;
import subscene.datnt.com.subscene.adapter.SubtitleAdapter;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.Subtitle;

/**
 * Created by DatNT on 3/30/2018.
 */

public class SeachSubAsynTask extends AsyncTask<String, Void, String> {
    private OnSearchSubListener listener;

    public SeachSubAsynTask(OnSearchSubListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        Document document = null;
        ArrayList<Subtitle> listSubtitle = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {

                Elements table = document.select("table > tbody > tr > td");
                for (Element element : table) {
                    Element links = element.select("a").first();
                    if (links != null) {
                        String link = links.attr("href");
//                        Elements contents = element.select("span");
//                        String language = "";
//                        String filmName = "";
//                        if (contents.size() != 0) {
//                            if (contents.get(0) != null)
//                                language = contents.get(0).text();
//                            if (contents.get(1) != null)
//                                filmName = contents.get(1).text();
//                            Subtitle subtitle = new Subtitle(filmName,language,"",link, "");
//                            listSubtitle.add(subtitle);
//                        }
                        return link;

                    }


                }
            }

        } catch (HttpStatusException e) {
            e.printStackTrace();
            return  null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String subtitles) {
        super.onPostExecute(subtitles);
        if (listener != null)
            listener.onSearchSubtitleSuccess(subtitles);
    }

    public interface OnSearchSubListener{
        void onSearchSubtitleSuccess(String link);
    }
}