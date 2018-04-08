package subscene.datnt.com.subscene.activity;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.util.ArrayList;

import subscene.datnt.com.subscene.adapter.HintAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.model.MovieHint;
import subscene.datnt.com.subscene.thread.HttpService;


public class SearchHintFragment extends Fragment implements OnItemClickListener, HttpService.HttpResponseListener {

    private RecyclerView listFilm;
    private ArrayList<MovieHint> arrayFilms = new ArrayList<>();
    private String stringQuery;
    private HintAdapter adapter;
    private RelativeLayout layoutNoFilm;
    private ProgressBar dialog;
    private ImageView imgSearch;

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
                intent.putExtra("Search",stringQuery);
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

    @Override
    public void onItemClick(int position) {
        Intent intent = new Intent(getActivity(), SubDetailActivity.class);
        intent.putExtra("Film", arrayFilms.get(position));
        startActivity(intent);
        ((MainActivity) getActivity()).exitSearchUi();

    }

    @Override
    public void onGetHint(final String query, final ArrayList<MovieHint> listHint) {
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
                    adapter = new HintAdapter(getActivity(), arrayFilms);
                    listFilm.setAdapter(adapter);
                    adapter.setOnItemClickListener(SearchHintFragment.this);
                }
            }
        });

    }

}
