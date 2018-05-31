package subscene.datnt.com.subscene.activity.search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.activity.MainActivity;
import subscene.datnt.com.subscene.activity.SubDetailActivity;
import subscene.datnt.com.subscene.adapter.OpenSubtitleFilmAdapter;
import subscene.datnt.com.subscene.adapter.YiFyMovieAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.thread.OpenSubtitle;
import subscene.datnt.com.subscene.thread.YifySubtitles;

public class OpenSubtitlesFragment extends Fragment implements OnItemClickListener, OnSceneListener {
    private String query;
    private RecyclerView listFilm;
    private ArrayList<Film> arrayFilms = new ArrayList<>();
    private OpenSubtitleFilmAdapter adapter;
    private TextView txtNoFilm;
    private ProgressBar dialog;
    private OpenSubtitle openSubtitle;
    private ArrayList<Subtitle> subtitleArrayList = new ArrayList<>();
    public OpenSubtitlesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        query = getArguments().getString("Search");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscene_search, container, false);
        initComponents(view);
        return view;
    }

    private void initComponents(View rootView) {
        listFilm = (RecyclerView) rootView.findViewById(R.id.list_film);
        txtNoFilm = rootView.findViewById(R.id.txt_no_film);
        dialog = rootView.findViewById(R.id.progressBar);
        setupListFilm();
        search();
    }

    private void setupListFilm() {
        listFilm.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listFilm.setLayoutManager(mLayoutManager);
    }

    private void search() {
        openSubtitle = new OpenSubtitle(this);
        openSubtitle.getMovieSubsByName(query,"");
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(int position) {
        Film film = arrayFilms.get(position);
        Intent intent = new Intent(getActivity(), SubDetailActivity.class);
        intent.putExtra("Film", arrayFilms.get(position));
        ArrayList<Subtitle> subtitles = new ArrayList<>();
        for (Subtitle subtitle : subtitleArrayList)
            if (subtitle.getFilm().equals(film.getName())) {
                subtitles.add(subtitle);
            }
        Collections.sort(subtitles, new Comparator<Subtitle>() {
            @Override
            public int compare(Subtitle s1, Subtitle s2) {
                return s1.getLanguague().compareToIgnoreCase(s2.getLanguague());
            }
        });
        intent.putExtra("Subtitle", subtitles);
        startActivity(intent);
    }

    @Override
    public void onFoundFilm(String query, final ArrayList<Film> films) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                dialog.setVisibility(View.GONE);
                if (films == null || films.size() == 0) {
                    txtNoFilm.setVisibility(View.VISIBLE);
                    listFilm.setVisibility(View.GONE);
                    arrayFilms.clear();
                } else {
                    txtNoFilm.setVisibility(View.GONE);
                    listFilm.setVisibility(View.VISIBLE);
                    arrayFilms = films;
                    // Add decoration for dividers between list items
                    // listFilm.addItemDecoration(new MarginDividerDecoration(mThis));
                    listFilm.setVisibility(View.VISIBLE);
                    adapter = null;
                    adapter = new OpenSubtitleFilmAdapter(getActivity(), arrayFilms);
                    listFilm.setAdapter(adapter);
                    adapter.setOnItemClickListener(OpenSubtitlesFragment.this);
                }
            }
        });
    }

    @Override
    public void onFoundLinkDownload(String poster,String imdb, String linkDownload, String detail, String preview) {

    }

    @Override
    public void onFoundListSubtitle(ArrayList<Subtitle> listSubtitle) {
        this.subtitleArrayList = listSubtitle;

    }
}
