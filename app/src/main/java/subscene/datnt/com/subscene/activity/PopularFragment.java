package subscene.datnt.com.subscene.activity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import subscene.datnt.com.subscene.adapter.PopularFilmAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.PopularFilm;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.thread.PopularSubtitleAsynTask;

public class PopularFragment extends Fragment implements PopularSubtitleAsynTask.OnSearchPopolarFilmListener, OnItemClickListener {
    private ProgressBar progressBar;
    private RecyclerView listFilm;
    private RelativeLayout root;
    private ArrayList<PopularFilm> listPopularFilm = new ArrayList<>();
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
        root = v.findViewById(R.id.root);
        listFilm = v.findViewById(R.id.list_popular_film);
        listFilm.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listFilm.setLayoutManager(mLayoutManager);
        progressBar.setVisibility(View.VISIBLE);
        new PopularSubtitleAsynTask("",this).execute();
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

    @Override
    public void onSearchSuccess(ArrayList<PopularFilm> link) {
        progressBar.setVisibility(View.GONE);
        if (link == null){
            Snackbar snackbar = Snackbar
                    .make(root, "Oops, Fetching data failure ", Snackbar.LENGTH_LONG)
                    .setAction("Try again", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            progressBar.setVisibility(View.VISIBLE);
                            new PopularSubtitleAsynTask("",PopularFragment.this).execute();
                        }
                    });

            snackbar.show();
            return;
        }
        listPopularFilm = link;
        PopularFilmAdapter adapter = new PopularFilmAdapter(getActivity(),listPopularFilm);
        adapter.setOnItemClickListener(this);
        listFilm.setAdapter(adapter);

    }

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), SubtitleDownloadActivity.class);
        intent.putExtra("PopularFilm", listPopularFilm.get(position));
        startActivity(intent);
    }
}
