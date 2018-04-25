package subscene.datnt.com.subscene.adapter;

/**
 * Created by DatNT on 3/26/2018.
 */

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.BackgroundColorSpan;
import android.text.style.ForegroundColorSpan;
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
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.MovieHint;
import subscene.datnt.com.subscene.utils.CommonUtils;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public class HintAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Film> mListHint;
    private String query;
    private OnItemClickListener listener;

    public HintAdapter(Context mContext, ArrayList<Film> mListFile, String query) {
        this.mContext = mContext;
        this.mListHint = mListFile;
        this.query = query;
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
        //highlightText( mHolder.txtMovie, query);
        mHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onItemClick(position);
                }
            }
        });
    }
    private void highlightText(TextView mTextView, String s) {
        SpannableString spannableString = new SpannableString(mTextView.getText());
        BackgroundColorSpan[] backgroundColorSpan =
                spannableString.getSpans(0, spannableString.length(), BackgroundColorSpan.class);
        for (BackgroundColorSpan bgSpan : backgroundColorSpan) {
            spannableString.removeSpan(bgSpan);
        }
        int indexOfKeyWord = spannableString.toString().indexOf(s);
        while (indexOfKeyWord > 0) {
            spannableString.setSpan(new ForegroundColorSpan(Color.YELLOW), indexOfKeyWord,
                    indexOfKeyWord + s.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            indexOfKeyWord = spannableString.toString().indexOf(s, indexOfKeyWord + s.length());
        }
        mTextView.setText(spannableString);
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
