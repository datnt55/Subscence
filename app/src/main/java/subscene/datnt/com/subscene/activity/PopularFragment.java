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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import subscene.datnt.com.subscene.adapter.PopularFilmAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.Language;
import subscene.datnt.com.subscene.model.PopularFilm;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.thread.PopularSubtitleAsynTask;
import subscene.datnt.com.subscene.utils.SharePreference;

public class PopularFragment extends Fragment implements PopularSubtitleAsynTask.OnSearchPopolarFilmListener, OnItemClickListener, AdapterView.OnItemSelectedListener {
    private ProgressBar progressBar;
    private RecyclerView listFilm;
    private RelativeLayout root;
    private ArrayList<PopularFilm> listPopularFilm = new ArrayList<>();
    private Spinner spnLanguage;
    private Spinner spnFilter;
    private ArrayList<Language> languages = new ArrayList<>();
    private Language ownLanguage;
    private String currentFilter = "all";

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
        spnLanguage = v.findViewById(R.id.spr_language);
        spnFilter = v.findViewById(R.id.spr_filter);
        ownLanguage = new SharePreference(getActivity()).getCurrentLanguage();

        SharePreference preference = new SharePreference(getActivity());
        languages = preference.getLanguage();
        Collections.sort(languages, new Comparator<Language>() {
            @Override
            public int compare(Language s1, Language s2) {
                return s1.getName().compareToIgnoreCase(s2.getName());
            }
        });

        ArrayList<String> arrayFilter = new ArrayList<>();
        arrayFilter.add("All");
        arrayFilter.add("Movies");
        arrayFilter.add("TV-Series");
        arrayFilter.add("Music Videos");
        ArrayAdapter adapter = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, arrayFilter);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnFilter.setAdapter(adapter);
        spnFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progressBar.setVisibility(View.VISIBLE);
                listFilm.setVisibility(View.GONE);

                switch (position){
                    case 0:
                        currentFilter = "all";
                        break;
                    case 1:
                        currentFilter = "film";
                        break;
                    case 2:
                        currentFilter = "series";
                        break;
                    case 3:
                        currentFilter = "music";
                        break;
                }
                new PopularSubtitleAsynTask(ownLanguage.getId(),currentFilter,PopularFragment.this).execute();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayList<String> languageName = new ArrayList<>();
        for (Language language : languages)
            languageName.add(language.getName());

        spnLanguage.setOnItemSelectedListener(PopularFragment.this);
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, languageName);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnLanguage.setAdapter(aa);
        Language lang = new SharePreference(getActivity()).getCurrentLanguage();
        for (int i = 0; i < languages.size(); i++) {
            if (languages.get(i).getId().equals(lang.getId())) {
                spnLanguage.setSelection(i);
                break;
            }
        }
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
        listFilm.setVisibility(View.VISIBLE);
        if (link == null){
            Snackbar snackbar = Snackbar
                    .make(root, "Oops, Fetching data failure ", Snackbar.LENGTH_LONG)
                    .setAction("Try again", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            progressBar.setVisibility(View.VISIBLE);
                            new PopularSubtitleAsynTask(ownLanguage.getId(),currentFilter,PopularFragment.this).execute();
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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ownLanguage = languages.get(position);
        new SharePreference(getActivity()).saveCurrentLanguage(ownLanguage);
        progressBar.setVisibility(View.VISIBLE);
        listFilm.setVisibility(View.GONE);
        new PopularSubtitleAsynTask(ownLanguage.getId(),currentFilter, this).execute();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
