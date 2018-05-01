package subscene.datnt.com.subscene.activity;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;

import subscene.datnt.com.subscene.thread.YifySubtitles;
import subscene.datnt.com.subscene.utils.FileUtil;
import subscene.datnt.com.subscene.utils.Globals;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.widget.FilePickerBottomSheet;
import subscene.datnt.com.subscene.widget.SearchEditTextLayout;
import subscene.datnt.com.subscene.adapter.ViewPagerAdapter;
import subscene.datnt.com.subscene.utils.CommonUtils;

public class MainActivity extends AppCompatActivity implements DownloadedFragment.OnDataPass, FilePickerBottomSheet.FilePickerListener {
    private String url = "https://subscene.com/subtitles/title?q=avenger&l=";
    //private RecyclerView listFilm;
    private Context mThis;
   // private ArrayList<Film> arrayFilms = new ArrayList<>();
    private SearchEditTextLayout searchEditTextLayout;
    private EditText mSearchView;
    private boolean isInSearch;
    private String mSearchQuery;
    private SearchHintFragment searchFragment;
    //private FilmHeadersAdapter adapter;
    private ProgressBar progressBar;
    private RelativeLayout layoutSearch;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.download,
            R.drawable.download
    };
    private PopularFragment popularFragment;
    private AutoDownloadFragment autoDownloadFragment;
    private DownloadedFragment downloadedFragment;
    private FilePickerBottomSheet mBottomSheet;
    private BottomSheetBehavior bottomSheetBehavior;
    private RelativeLayout layoutShadow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CommonUtils.dimensionScreen(this);
        mThis = this;
        progressBar = findViewById(R.id.progressBar);
        layoutSearch = findViewById(R.id.layout_search);
        initSearchLayout();
        initViewPager();
        initBottomSheet();
        YifySubtitles yifySubtitles = new YifySubtitles(null);
        yifySubtitles.getMovieSubsByName("avenger","");
    }

    private void initBottomSheet() {
        layoutShadow = findViewById(R.id.shadow);
        mBottomSheet = findViewById(R.id.file_picker_bottom_sheet);
        mBottomSheet.setOnFilePickerListener(this);
        bottomSheetBehavior = BottomSheetBehavior.from(mBottomSheet);
        bottomSheetBehavior.setHideable(true);
        //bottomSheetBehavior.setPeekHeight(126);
        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_COLLAPSED)
                    layoutShadow.setVisibility(View.VISIBLE);
                else if (newState == BottomSheetBehavior.STATE_HIDDEN)
                    layoutShadow.setVisibility(View.GONE);
                else if (newState == BottomSheetBehavior.STATE_EXPANDED)
                    layoutShadow.setVisibility(View.VISIBLE);
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
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
        popularFragment = new PopularFragment();
        autoDownloadFragment = new AutoDownloadFragment();
        downloadedFragment = new DownloadedFragment();

        adapter.addFrag(popularFragment, "Popular Subtitle");
        adapter.addFrag(autoDownloadFragment, "Auto Download");
        adapter.addFrag(downloadedFragment, "Downloaded");
        viewPager.setAdapter(adapter);

        viewPager.setOffscreenPageLimit(3);
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);
        viewPager.setOffscreenPageLimit(limit);
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText("Popular Subs");
        tabLayout.getTabAt(1).setText("Auto Download");
        tabLayout.getTabAt(2).setText("Downloaded");
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
        mSearchView.setOnEditorActionListener(mPhoneSearchClickListener);
    }

    private TextView.OnEditorActionListener mPhoneSearchClickListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                Intent intent = new Intent(mThis, ListSearchResultActivity.class);
                intent.putExtra("Search",mSearchQuery);
                startActivity(intent);
                return true;
            }
            return false;
        }
    };
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
        SearchHintFragment fragment = (SearchHintFragment) getFragmentManager().findFragmentByTag
                (Globals.TAG_REGULAR_SEARCH_FRAGMENT);
        if (animate) {
            transaction.setCustomAnimations(android.R.animator.fade_in, 0);
        } else {
            transaction.setTransition(FragmentTransaction.TRANSIT_NONE);
        }
        if (fragment == null) {
            fragment = new SearchHintFragment();
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
        if (popularFragment.isVisible() && popularFragment.getView() != null && popularFragment.getView().animate() != null)
            popularFragment.getView().animate().alpha(0).withLayer();
        if (autoDownloadFragment.isVisible() && autoDownloadFragment.getView() != null && autoDownloadFragment.getView().animate() != null)
            autoDownloadFragment.getView().animate().alpha(0).withLayer();
        if (downloadedFragment.isVisible() && downloadedFragment.getView() != null && downloadedFragment.getView().animate() != null)
            downloadedFragment.getView().animate().alpha(0).withLayer();
    }

    private void showComponentWhenSearchViewHide() {
        layoutSearch.animate().alpha(1).withLayer();
        tabLayout.animate().alpha(1).withLayer();
        if (popularFragment.isVisible() && popularFragment.getView() != null && popularFragment.getView().animate() != null)
            popularFragment.getView().animate().alpha(1).withLayer();
        if (autoDownloadFragment.isVisible() && autoDownloadFragment.getView() != null && autoDownloadFragment.getView().animate() != null)
            autoDownloadFragment.getView().animate().alpha(1).withLayer();
        if (downloadedFragment.isVisible() && downloadedFragment.getView() != null && downloadedFragment.getView().animate() != null)
            downloadedFragment.getView().animate().alpha(1).withLayer();
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
        if (fragment instanceof SearchHintFragment) {
            searchFragment = (SearchHintFragment) fragment;
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


    @Override
    public void onDataPass(File file) {
        this.moveFile = file;
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

    }
    File moveFile;
    @Override
    public void onCopy(String filename, File path) {
        String extension = FilenameUtils.getExtension(moveFile.getAbsolutePath());
        FileUtil.move(filename + "." + extension, moveFile, path);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public void onCancelCopy() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_HIDDEN) {
                mBottomSheet.onBackPressed();
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }
}
