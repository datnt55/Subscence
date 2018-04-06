package subscene.datnt.com.subscene.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.Film;

/**
 * Adapter for detail of a recent call
 */
public class OpenSubtitleFilmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Film> mListFilm;
    private DisplayImageOptions options;
    private OnItemClickListener listener;

    public OpenSubtitleFilmAdapter(Context mContext, ArrayList<Film> mListSubtitle) {
        this.mContext = mContext;
        this.mListFilm = mListSubtitle;

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_open_subtitle_film, parent, false);
        return new OpenSubtitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final OpenSubtitleViewHolder mHolder = (OpenSubtitleViewHolder) holder;
        mHolder.name.setText(mListFilm.get(position).getName());
        mHolder.language.setText(mListFilm.get(position).getUrl());
        mHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (position == 0)
                    return;
                if (listener != null)
                    listener.onItemClick(position);
            }
        });
    }



    @Override
    public int getItemCount() {
        return mListFilm.size();
    }

    public static class OpenSubtitleViewHolder extends RecyclerView.ViewHolder {

        private final TextView name;
        private final LinearLayout root;
        private final TextView language;
        OpenSubtitleViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.txt_movie);
            language = (TextView) itemView.findViewById(R.id.txt_language);
            root =  itemView.findViewById(R.id.root);
        }
    }

}
