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

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.activity.SubDetailActivity;
import subscene.datnt.com.subscene.activity.SubtitleDownloadActivity;
import subscene.datnt.com.subscene.adapter.YiFyMovieAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.Subtitle;
import subscene.datnt.com.subscene.thread.YifySubtitles;

public class YiFySearchFragment extends Fragment implements OnItemClickListener, OnSceneListener {
    private String query;
    private RecyclerView listFilm;
    private ArrayList<Film> arrayFilms = new ArrayList<>();
    private YiFyMovieAdapter adapter;
    private TextView txtNoFilm;
    private ProgressBar dialog;
    private YifySubtitles yifySubtitles;

    public YiFySearchFragment() {
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
        yifySubtitles = new YifySubtitles(this);
        yifySubtitles.getMovieSubsByName(query,"");
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
        intent.putExtra("Film", film);
        startActivity(intent);
        //yifySubtitles.getLinkDownloadSubtitle(film.getUrl());
    }

    @Override
    public void onFoundFilm(String query, final ArrayList<Film> films) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
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
                    // listFilm.addItemDecoration(new MarginDividerDecoration(mThis));
                    listFilm.setVisibility(View.VISIBLE);
                    adapter = null;
                    adapter = new YiFyMovieAdapter(getActivity(), arrayFilms);
                    listFilm.setAdapter(adapter);
                    adapter.setOnItemClickListener(YiFySearchFragment.this);
                }
            }
        });
    }

    @Override
    public void onFoundLinkDownload(String poster, String linkDownload, String detail, String preview) {

    }

    @Override
    public void onFoundListSubtitle(ArrayList<Subtitle> listSubtitle) {

    }
}
