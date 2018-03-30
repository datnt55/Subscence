package subscene.datnt.com.subscene.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import subscene.datnt.com.subscene.utils.Globals;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.widget.SearchEditTextLayout;
import subscene.datnt.com.subscene.adapter.ViewPagerAdapter;
import subscene.datnt.com.subscene.utils.CommonUtils;

public class MainActivity extends AppCompatActivity{
    private String url = "https://subscene.com/subtitles/title?q=avenger&l=";
    //private RecyclerView listFilm;
    private Context mThis;
   // private ArrayList<Film> arrayFilms = new ArrayList<>();
    private SearchEditTextLayout searchEditTextLayout;
    private EditText mSearchView;
    private boolean isInSearch;
    private String mSearchQuery;
    private SearchFragment searchFragment;
    //private FilmHeadersAdapter adapter;
    private ProgressBar progressBar;
    private RelativeLayout layoutSearch;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.download,
            R.drawable.download
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonUtils.dimensionScreen(this);
        mThis = this;
        progressBar = findViewById(R.id.progressBar);
        layoutSearch = findViewById(R.id.layout_search);
       // listFilm = findViewById(R.id.list_film);
        // Set layout manager
        int orientation = getLayoutManagerOrientation(getResources().getConfiguration().orientation);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this, orientation, false);
       // listFilm.setLayoutManager(layoutManager);
       // adapter = new FilmHeadersAdapter(arrayFilms, mThis);
      //  listFilm.setAdapter(adapter);
      //  final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(adapter);
      //  listFilm.addItemDecoration(headersDecor);
     //   adapter.setOnItemClickListener(MainActivity.this);
     //   new LoadData().execute(url);
        initSearchLayout();
        initViewPager();
    }

    private void initViewPager() {
        viewPager       =   (ViewPager) findViewById(R.id.lists_pager);
        setupViewPager(viewPager);

        tabLayout       = (TabLayout)   findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        android.support.v4.app.Fragment popular = new PopularFragment();


        adapter.addFrag(popular, "Popular Subtitle");
        adapter.addFrag(new AutoDownloadFragment(), "Auto Download");
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
//        tabLayout.getTabAt(0).getIcon().setColorFilter(ContextCompat.getColor(mThis, R.color.blue_light), PorterDuff.Mode.SRC_IN);
//        tabLayout.getTabAt(1).getIcon().setColorFilter(ContextCompat.getColor(mThis, R.color.grey_1), PorterDuff.Mode.SRC_IN);
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                tab.getIcon().setColorFilter(ContextCompat.getColor(mThis, R.color.blue_light), PorterDuff.Mode.SRC_IN);
//
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//                tab.getIcon().setColorFilter(ContextCompat.getColor(mThis, R.color.grey_1), PorterDuff.Mode.SRC_IN);
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }


    private void initSearchLayout() {
        searchEditTextLayout =  findViewById(R.id.search_view_container);
        mSearchView = (EditText) searchEditTextLayout.findViewById(R.id.search_view);
        searchEditTextLayout.findViewById(R.id.search_box_start_search).setOnClickListener(mSearchViewOnClickListener);
        searchEditTextLayout.setOnClickListener(mSearchViewOnClickListener);
        searchEditTextLayout.setCallback(searchEditTextActionListener);
        mSearchView.addTextChangedListener(mPhoneSearchQueryTextListener);
    }

    // Action when click to components of search view
    private SearchEditTextLayout.Callback searchEditTextActionListener = new SearchEditTextLayout.Callback() {
        @Override
        public void onBackButtonClicked() {
            onBackPressed();
        }

        @Override
        public void onSearchViewClicked() {
            // Do nothing
        }
    };

    private final View.OnClickListener mSearchViewOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!isInSearch) {
                searchEditTextLayout.expand(true, true);
                isInSearch = true;
                enterSearchUi(false /* smartDialSearch */, true /* animate */);
            }
        }
    };

    private final TextWatcher mPhoneSearchQueryTextListener = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // Do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            final String newText = s.toString();
            if (newText.equals(mSearchQuery)) {
                return;
            }
            mSearchQuery = newText;
            if (searchFragment != null && searchFragment.isVisible()) {
                searchFragment.setOnQueryString(mSearchQuery);
            }
        }

        @Override
        public void afterTextChanged(Editable s) {
            // Do nothing
        }
    };

    // Show search fragment
    private void enterSearchUi(boolean smartDialSearch, boolean animate) {
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (isInSearch && searchFragment != null) {
            transaction.remove(searchFragment);
        }
        isInSearch = !smartDialSearch;
        SearchFragment fragment = (SearchFragment) getFragmentManager().findFragmentByTag
                (Globals.TAG_REGULAR_SEARCH_FRAGMENT);
        if (animate) {
            transaction.setCustomAnimations(android.R.animator.fade_in, 0);
        } else {
            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        }
        if (fragment == null) {
            fragment = new SearchFragment();
            transaction.add(R.id.dialtacts_frame, fragment, Globals.TAG_REGULAR_SEARCH_FRAGMENT);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();
        hideComponentWhenSearchViewShow();
    }

    private void hideComponentWhenSearchViewShow() {
        layoutSearch.animate().alpha(0).withLayer();
        tabLayout.animate().alpha(0).withLayer();
    }

    private void showComponentWhenSearchViewHide() {
        layoutSearch.animate().alpha(1).withLayer();
        tabLayout.animate().alpha(1).withLayer();
    }

    private int getLayoutManagerOrientation(int activityOrientation) {
        if (activityOrientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {
            return LinearLayoutManager.VERTICAL;
        } else {
            return LinearLayoutManager.HORIZONTAL;
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (fragment instanceof SearchFragment) {
            searchFragment = (SearchFragment) fragment;
            //searchFragment.setOnScrollListener(this);
            //searchFragment.setOnCallListener(this);
        }
    }


    @Override
    public void onBackPressed() {
        if (isInSearch) {
            exitSearchUi();
        } else {
            super.onBackPressed();
        }
    }

    public void exitSearchUi() {
        mSearchView.setText(null);
        isInSearch = false;
        final FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (searchFragment != null) {
            transaction.remove(searchFragment);
        }
        transaction.commit();
        showComponentWhenSearchViewHide();
        if (searchEditTextLayout.isExpanded()) {
            searchEditTextLayout.collapse(true /* animate */);
        }
        if (searchEditTextLayout.isFadedOut()) {
            searchEditTextLayout.fadeIn();
        }
    }


}
