package subscene.datnt.com.subscene.adapter;

/**
 * Created by DatNT on 3/26/2018.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.MovieHint;
import subscene.datnt.com.subscene.utils.CommonUtils;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public class HintAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<MovieHint> mListHint;
    private String query;
    private OnItemClickListener listener;

    public HintAdapter(Context mContext, ArrayList<MovieHint> mListFile) {
        this.mContext = mContext;
        this.mListHint = mListFile;

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_hint, parent, false);
        return new HintViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final HintViewHolder mHolder = (HintViewHolder) holder;
        mHolder.txtMovie.setText(mListHint.get(position).getName());
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
        return mListHint.size();
    }

    public static class HintViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtMovie;
        private final RelativeLayout root;
        HintViewHolder(View itemView) {
            super(itemView);
            txtMovie = (TextView) itemView.findViewById(R.id.txt_movie);
            root =  itemView.findViewById(R.id.root);
        }
    }

}
