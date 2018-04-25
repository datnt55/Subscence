package subscene.datnt.com.subscene.activity;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.activity.search.OpenSubtitlesFragment;
import subscene.datnt.com.subscene.activity.search.SubsceneSearchFragment;
import subscene.datnt.com.subscene.activity.search.YiFySearchFragment;
import subscene.datnt.com.subscene.adapter.ViewPagerAdapter;

public class ListSearchResultActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private int[] tabIcons = {
            R.drawable.download,
            R.drawable.download
    };
    private SubsceneSearchFragment subsceneSearchFragment;
    private YiFySearchFragment yiFySearchFragment;
    private OpenSubtitlesFragment openSubtitlesFragment;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_search_result);
        initViewPager();
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Search result");
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
    private void initViewPager() {
        viewPager = findViewById(R.id.lists_pager);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();
    }
    private void setupViewPager(ViewPager viewPager) {
        String query = getIntent().getStringExtra("Search");
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Bundle bundle = new Bundle();
        bundle.putString("Search", query);
        subsceneSearchFragment = new SubsceneSearchFragment();
        subsceneSearchFragment.setArguments(bundle);

        yiFySearchFragment = new YiFySearchFragment();
        yiFySearchFragment.setArguments(bundle);

        openSubtitlesFragment = new OpenSubtitlesFragment();
        openSubtitlesFragment.setArguments(bundle);

        adapter.addFrag(subsceneSearchFragment, "SubScene");
        adapter.addFrag(yiFySearchFragment, "YifySubtitles");
        adapter.addFrag(openSubtitlesFragment, "OpenSubtitles");
        viewPager.setAdapter(adapter);
    }
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setText("SubScene");
        tabLayout.getTabAt(1).setText("YifySubtitles");
        tabLayout.getTabAt(2).setText("OpenSubtitles");
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
}
