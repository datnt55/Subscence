package subscene.datnt.com.subscene.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.adapter.FilmAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.R;


public class SearchFragment extends Fragment implements OnItemClickListener {

    private RecyclerView listFilm;
    private ArrayList<Film> arrayFilms = new ArrayList<>();
    private String stringQuery;
    private FilmAdapter adapter;
    private TextView txtNoFilm;
    private ProgressBar dialog;

    public SearchFragment() {
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
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        initComponents(rootView);
        return rootView;
    }

    private void initComponents(View rootView) {
        listFilm = (RecyclerView) rootView.findViewById(R.id.list_film);
        txtNoFilm = rootView.findViewById(R.id.txt_no_film);
        dialog = rootView.findViewById(R.id.progressBar);
        stringQuery = "";
        setupListFilm();
    }

    private void setupListFilm() {
        listFilm.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listFilm.setLayoutManager(mLayoutManager);
    }

    public void setOnQueryString(String query) {
        this.stringQuery = query;
        txtNoFilm.setVisibility(View.GONE);
        listFilm.setVisibility(View.GONE);
        if (query.equals("")) {
            dialog.setVisibility(View.GONE);
            arrayFilms.clear();
        } else {
            if (dialog.getVisibility() == View.GONE)
                dialog.setVisibility(View.VISIBLE);
            searchProcess(query);
        }
    }

    private void searchProcess(final String query) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (query.equals(stringQuery)) {
                    String url = "https://subscene.com/subtitles/title?q=" + query + "&l=";
                    Log.e("URL",url);
                    new LoadData(query).execute(url);
                }
            }
        },2000);
    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), SubDetailActivity.class);
        intent.putExtra("Film", arrayFilms.get(position));
        startActivity(intent);
        ((MainActivity) getActivity()).exitSearchUi();
    }

    private class LoadData extends AsyncTask<String, Void, ArrayList<Film>> {
        private String query;

        public LoadData(String query) {
            this.query = query;
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
                            Film article = new Film(name, links, subCount);
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
            if (!stringQuery.equals(query))
                return;
            dialog.setVisibility(View.GONE);
            if (films.size() == 0) {
                txtNoFilm.setVisibility(View.VISIBLE);
                listFilm.setVisibility(View.GONE);
                arrayFilms.clear();
            } else {
                txtNoFilm.setVisibility(View.GONE);
                listFilm.setVisibility(View.VISIBLE);
                arrayFilms = films;
                // Add decoration for dividers between list items
                // listFilm.addItemDecoration(new DividerDecoration(mThis));
                listFilm.setVisibility(View.VISIBLE);
                adapter = null;
                adapter = new FilmAdapter(arrayFilms, getActivity());
                listFilm.setAdapter(adapter);
                adapter.setOnItemClickListener(SearchFragment.this);
            }

        }
    }
}
