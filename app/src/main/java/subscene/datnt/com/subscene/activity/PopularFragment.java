package subscene.datnt.com.subscene.activity;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
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

import subscene.datnt.com.subscene.adapter.PopularFilmAdapter;
import subscene.datnt.com.subscene.model.PopularFilm;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.thread.PopularSubtitleAsynTask;

public class PopularFragment extends Fragment implements PopularSubtitleAsynTask.OnSearchPopolarFilmListener{
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
        PopularFilmAdapter adapter = new PopularFilmAdapter(getActivity(),link);
        listFilm.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
    }
}
