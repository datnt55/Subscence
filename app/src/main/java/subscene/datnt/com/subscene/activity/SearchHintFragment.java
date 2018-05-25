package subscene.datnt.com.subscene.activity;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;

import subscene.datnt.com.subscene.adapter.HintAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.MovieHint;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.thread.HttpService;
import subscene.datnt.com.subscene.thread.Subscene;
import subscene.datnt.com.subscene.thread.YifySubtitles;


public class SearchHintFragment extends Fragment implements OnItemClickListener, HttpService.HttpResponseListener, OnSceneListener {

    private RecyclerView listFilm;
    private ArrayList<Film> arrayFilms = new ArrayList<>();
    private String stringQuery;
    private HintAdapter adapter;
    private RelativeLayout layoutNoFilm;
    private ProgressBar dialog;
    private ImageView imgSearch;
    private Subscene subscene;
    private String imdb = "";
    private int position;
    private ArrayList<Subtitle> arraySubscene = new ArrayList<>();
    private ArrayList<Subtitle> arrayYify = new ArrayList<>();

    public SearchHintFragment() {
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
        layoutNoFilm = rootView.findViewById(R.id.layout_no_result);
        dialog = rootView.findViewById(R.id.progressBar);
        imgSearch = rootView.findViewById(R.id.img_search);
        imgSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ListSearchResultActivity.class);
                intent.putExtra("Search", stringQuery);
                startActivity(intent);
            }
        });
        stringQuery = "";
        setupListFilm();
    }

    private void setupListFilm() {
        listFilm.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listFilm.setLayoutManager(mLayoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        listFilm.addItemDecoration(dividerItemDecoration);

    }

    public void setOnQueryString(String query) {
        this.stringQuery = query;
        layoutNoFilm.setVisibility(View.GONE);
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
        stringQuery = query;
        if (query.length() > 1) {
            DateTimeFormatter formatter = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm:ss.SSS");

            HttpService service = new HttpService(formatter.print(new DateTime()), this);
            service.getHintData(query);
        }
    }

    ProgressDialog prepareDialog;

    @Override
    public void onItemClick(final int position) {
        this.position = position;
        filmPos = 0;

        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            view.clearFocus();
        }
        prepareDialog = new ProgressDialog(getActivity());
        prepareDialog.setTitle("Searching");
        prepareDialog.setMessage("Preparing subtitles...");
        prepareDialog.setCanceledOnTouchOutside(false);
        prepareDialog.setCancelable(false);
        prepareDialog.show();
        YifySubtitles yifySubtitles = new YifySubtitles(new OnSceneListener() {
            @Override
            public void onFoundFilm(String query, ArrayList<Film> films) {

            }

            @Override
            public void onFoundLinkDownload(String poster, String linkDownload, String detail, String preview) {

            }

            @Override
            public void onFoundListSubtitle(ArrayList<Subtitle> listSubtitle) {
                getListSubscene(arrayFilms.get(position).getUrl());
                if (listSubtitle != null)
                    arrayYify = listSubtitle;
            }
        });
        yifySubtitles.searchSubsFromMovieName("https://www.yifysubtitles.com/movie-imdb/" + arrayFilms.get(position).getUrl(), "");
    }

    @Override
    public void onGetHint(final String query, final ArrayList<Film> listHint) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (!stringQuery.equals(query))
                    return;
                dialog.setVisibility(View.GONE);
                if (listHint.size() == 0) {
                    layoutNoFilm.setVisibility(View.VISIBLE);
                    listFilm.setVisibility(View.GONE);
                    arrayFilms.clear();
                } else {
                    layoutNoFilm.setVisibility(View.GONE);
                    listFilm.setVisibility(View.VISIBLE);
                    arrayFilms = listHint;
                    // Add decoration for dividers between list items
                    // listFilm.addItemDecoration(new MarginDividerDecoration(mThis));
                    listFilm.setVisibility(View.VISIBLE);
                    adapter = null;
                    adapter = new HintAdapter(getActivity(), arrayFilms, query);
                    listFilm.setAdapter(adapter);
                    adapter.setOnItemClickListener(SearchHintFragment.this);
                }
            }
        });

    }

    private ArrayList<Film> subsceneFilm = new ArrayList<>();
    private int filmPos = 0;

    private void getListSubscene(String imdb) {
        this.imdb = imdb;
        subscene = new Subscene(this);
        subscene.getMovieSubsByName(stringQuery, "");
    }

    @Override
    public void onFoundFilm(String query, ArrayList<Film> films) {
        for (Film film : films)
            if (!film.getType().equals("Popular"))
                subsceneFilm.add(film);
        if (subsceneFilm.size() >= 0)
            subscene.searchSubsFromMovieName(subsceneFilm.get(filmPos).getUrl(), "");
        else {
            prepareDialog.dismiss();
            if (arrayYify.size() > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), SubSearchDetailActivity.class);
                        intent.putExtra("film", arrayFilms.get(position));
                        intent.putExtra("YifyFilm", arrayYify);
                        intent.putExtra("SubScene", arraySubscene);
                        startActivity(intent);
                        ((MainActivity) getActivity()).exitSearchUi();
                    }
                });
            } else {
                Intent intent = new Intent(getActivity(), ListSearchResultActivity.class);
                intent.putExtra("Search", stringQuery);
                startActivity(intent);
            }
        }
    }

    @Override
    public void onFoundLinkDownload(String poster, String linkDownload, String detail, String preview) {

    }

    @Override
    public void onFoundListSubtitle(final ArrayList<Subtitle> listSubtitle) {
        if (listSubtitle != null && listSubtitle.size() > 0) {
            if (listSubtitle.get(0).getImdb().equals(imdb)) {
                arraySubscene = listSubtitle;
                prepareDialog.dismiss();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), SubSearchDetailActivity.class);
                        intent.putExtra("film", arrayFilms.get(position));
                        intent.putExtra("YifyFilm", arrayYify);
                        intent.putExtra("SubScene", arraySubscene);
                        startActivity(intent);
                        ((MainActivity) getActivity()).exitSearchUi();
                    }
                });
                return;
            }
        }
        filmPos++;
        if (filmPos < subsceneFilm.size()) {
            subscene.searchSubsFromMovieName(subsceneFilm.get(filmPos).getUrl(), "");
        } else {
            prepareDialog.dismiss();
            if (arrayYify.size() > 0) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(getActivity(), SubSearchDetailActivity.class);
                        intent.putExtra("film", arrayFilms.get(position));
                        intent.putExtra("YifyFilm", arrayYify);
                        intent.putExtra("SubScene", arraySubscene);
                        startActivity(intent);
                        ((MainActivity) getActivity()).exitSearchUi();
                    }
                });
            } else {
                Intent intent = new Intent(getActivity(), ListSearchResultActivity.class);
                intent.putExtra("Search", stringQuery);
                startActivity(intent);
            }
        }
    }
}
