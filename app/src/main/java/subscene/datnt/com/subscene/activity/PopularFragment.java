package subscene.datnt.com.subscene.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.model.PopularFilm;
import subscene.datnt.com.subscene.R;

public class PopularFragment extends Fragment {
    private ProgressBar progressBar;
    private RecyclerView listFilm;
    private String popularUrl = "https://subscene.com/browse/popular/all/1";
    public PopularFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_popular, container, false);
        progressBar = v.findViewById(R.id.progressBar);
        listFilm = v.findViewById(R.id.list_popular_film);
        new LoadData().execute(popularUrl);
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    private class LoadData extends AsyncTask<String, Void, ArrayList<PopularFilm>> {
        @Override
        protected ArrayList<PopularFilm> doInBackground(String... strings) {
            Document document = null;
            ArrayList<PopularFilm> listFilm = new ArrayList<>();
            try {
                document = (Document) Jsoup.connect(strings[0]).get();
                if (document != null) {
                    Elements result = document.select("table > tbody > tr");
                    for (Element element : result){
                        Elements info = element.select("td");
                        String link ="";
                        String language ="";
                        String download = "";
                        String name = "";
                        String year = "";
                        for (Element ele : info){
                            String id = ele.attr("class");
                            if (id .equals("a1")){
                                link = ele.select("a").attr("href");
                                Elements languages = ele.select("span");
                                for (Element lang : languages){
                                    if (lang.attr("class").equals("l r positive-icon")){
                                        language = lang.text();
                                    }else if (lang.attr("class").equals("new")){
                                        name = lang.text();
                                    }
                                }
                            }else if (id.equals("a7")){
                                download = ele.text();
                            }
                        }
                        listFilm.add(new PopularFilm(name,link,language,download));

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return listFilm;
        }

        @Override
        protected void onPostExecute(ArrayList<PopularFilm> films) {
            super.onPostExecute(films);


        }
    }
}
