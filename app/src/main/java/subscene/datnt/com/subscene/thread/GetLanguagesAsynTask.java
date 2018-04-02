package subscene.datnt.com.subscene.thread;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.utils.Globals;

/**
 * Created by DatNT on 3/30/2018.
 */

public class GetLanguagesAsynTask extends AsyncTask<Void, Void, ArrayList<String>> {
    private OnGetLanguageListener listener;
    private ProgressDialog dialog;
    private Context context;
    public GetLanguagesAsynTask(Context context, OnGetLanguageListener listener) {
        this.context = context;
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("Prepare data...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    protected ArrayList<String> doInBackground(Void... strings) {
        Document document = null;
        ArrayList<String> listLanguage = new ArrayList<>();
        try {
            document = (Document) Jsoup.connect("https://u.subscene.com/filter").get();
            if (document != null) {

                Elements languages = document.select("div.col-md-3 > div.checkbox > label");
                for (Element element : languages) {
                    String input = element.select("input").first().text();
                    String language = element.text().replaceFirst(input, "").trim();
                    listLanguage.add(language);
                }
            }

        } catch (HttpStatusException e) {
            e.printStackTrace();
            return  null;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return listLanguage;
    }

    @Override
    protected void onPostExecute(ArrayList<String> languages) {
        super.onPostExecute(languages);
        dialog.dismiss();
        if (listener != null )
            listener.onGetLanguage(languages);
    }

    public interface OnGetLanguageListener{
        void onGetLanguage(ArrayList<String> languages);
    }
}