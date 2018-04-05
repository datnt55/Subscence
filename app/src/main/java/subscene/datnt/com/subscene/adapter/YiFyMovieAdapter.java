package subscene.datnt.com.subscene.adapter;

/**
 * Created by DatNT on 3/26/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.YiFyFilm;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public class YiFyMovieAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Film> mListFilm;
    private String query;
    private OnItemClickListener listener;

    public YiFyMovieAdapter(Context mContext, ArrayList<Film> mListFile) {
        this.mContext = mContext;
        this.mListFilm = mListFile;

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_yify_movie, parent, false);
        return new HintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HintViewHolder mHolder = (HintViewHolder) holder;
        YiFyFilm yiFyFilm = (YiFyFilm) mListFilm.get(position);
        mHolder.txtMovie.setText(yiFyFilm.getName());
        mHolder.txtActor.setText(yiFyFilm.getActor());
        mHolder.txtYear.setText("Year "+yiFyFilm.getYear());
        mHolder.txtDuration.setText(yiFyFilm.getDuration()+ " min");
        mHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onItemClick(position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mListFilm.size();
    }

    public static class HintViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtMovie;
        private final TextView txtActor;
        private final TextView txtYear;
        private final TextView txtDuration;
        private final RelativeLayout root;
        HintViewHolder(View itemView) {
            super(itemView);
            txtMovie = itemView.findViewById(R.id.txt_movie);
            txtYear = itemView.findViewById(R.id.txt_year);
            txtActor = itemView.findViewById(R.id.txt_actor);
            txtDuration = itemView.findViewById(R.id.txt_duration);
            root =  itemView.findViewById(R.id.root);
        }
    }

}
