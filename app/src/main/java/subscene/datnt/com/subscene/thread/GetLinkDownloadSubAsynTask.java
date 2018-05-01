package subscene.datnt.com.subscene.thread;

import android.os.AsyncTask;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.model.SubtitleDetail;

/**
 * Created by DatNT on 3/30/2018.
 */

public class GetLinkDownloadSubAsynTask extends AsyncTask<String, Void, String> {
    private OnDownloadSubtitleListener listener;

    public GetLinkDownloadSubAsynTask(OnDownloadSubtitleListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... strings) {
        Document document = null;
        String linkDownload = null;
        ArrayList<SubtitleDetail> subDetails = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect(strings[0]).get();
            if (document != null) {
                //Get link download
                Elements download = document.select("div.download");
                linkDownload = download.select("a").attr("href");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return linkDownload;
    }

    @Override
    protected void onPostExecute(String linkDownload) {
        super.onPostExecute(linkDownload);
        if (listener != null)
            listener.onGetLinkSuccess(linkDownload);
    }

    public interface OnDownloadSubtitleListener{
        void onGetLinkSuccess(String link);
    }
}