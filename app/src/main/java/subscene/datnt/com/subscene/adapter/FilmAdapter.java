package subscene.datnt.com.subscene.adapter;

/**
 * Created by DatNT on 3/26/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.R;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public class FilmAdapter extends SectionedRecyclerViewAdapter<RecyclerView.ViewHolder> {

    private ArrayList<String> mSectionLetters;
    private ArrayList<Integer> mSectionIndices;
    private List<Film> mArrayFilm;
    private OnItemClickListener listener;
    private Context mContext;

    private static final int IS_FOOTER = 3;

    public FilmAdapter(ArrayList<Film> films, Context mContext) {
        this.mArrayFilm = films;
        getPointChangeHeader();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getSectionCount() {
        return mSectionIndices.size();
    }

    @Override
    public int getListCount() {
        int count = 0;
        for (int s = 0; s < mSectionIndices.size(); s++) {
            count += mSectionIndices.get(s) + 1;
        }
        return count;
    }

    @Override
    public int getItemCount(int section) {
        return mSectionIndices.get(section);
    }

    @Override
    public boolean isHaveFooter() {
        return false;
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int section) {
        HeaderFilmViewHolder sectionViewHolder = (HeaderFilmViewHolder) holder;
        sectionViewHolder.mTitle.setText(mSectionLetters.get(section));
    }

    @Override
    public void onBindFooterViewHolder(RecyclerView.ViewHolder holder) {

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int section, final int relativePosition, final int position) {
        FilmViewHolder mHolder = (FilmViewHolder) holder;
        mHolder.mName.setText(mArrayFilm.get(position).getName());
        mHolder.mCount.setText(mArrayFilm.get(position).getSubCount());
        mHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemClick(position);
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, boolean header, boolean footer) {
        View v = null;
        if (header) {
            v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.view_header, parent, false);
            return new HeaderFilmViewHolder(v);
        } else {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film, parent, false);
            return new FilmViewHolder(v);
        }
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final TextView mCount;
        private final LinearLayout root;

        FilmViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.txt_title);
            mCount = itemView.findViewById(R.id.txt_sub_count);
            root = itemView.findViewById(R.id.root);
        }
    }

    public static class HeaderFilmViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTitle;

        HeaderFilmViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.itemView);
        }
    }

    // Group list recent call to 3 groups : today, yesterday and older
    private void getPointChangeHeader() {
        mSectionLetters = new ArrayList<>();
        mSectionIndices = new ArrayList<>();
        String titleHeader = mArrayFilm.get(0).getType();
        int sum = 0;
        mSectionLetters.add(mArrayFilm.get(0).getType());
        for (int i = 0; i < mArrayFilm.size(); i++) {
            if (!mArrayFilm.get(i).getType().equals(titleHeader)) {
                mSectionIndices.add(sum);
                sum = 1;
                mSectionLetters.add(mArrayFilm.get(i).getType());
                titleHeader = mArrayFilm.get(i).getType();
                if (i == mArrayFilm.size() - 1)
                    mSectionIndices.add(sum);
            } else {
                sum++;
                if (i == mArrayFilm.size() - 1)
                    mSectionIndices.add(sum);
            }
        }
    }
}