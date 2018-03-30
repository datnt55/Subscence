package subscene.datnt.com.subscene.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.util.ArrayList;

import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.model.Subtitle;

/**
 * Adapter for detail of a recent call
 */
public class SubtitleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<Subtitle> mListSubtitle;
    private DisplayImageOptions options;
    private OnItemClickListener listener;

    public SubtitleAdapter(Context mContext, ArrayList<Subtitle> mListSubtitle) {
        this.mContext = mContext;
        this.mListSubtitle = mListSubtitle;

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_subtitle, parent, false);
        return new SubtitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SubtitleViewHolder mHolder = (SubtitleViewHolder) holder;
        mHolder.language.setText(mListSubtitle.get(position).getLanguague());
        mHolder.name.setText(mListSubtitle.get(position).getTitle());
        if (position == 0){
            mHolder.bgItem.setBackgroundColor(ContextCompat.getColor(mContext,R.color.colorPrimary));
            mHolder.language.setTextColor(ContextCompat.getColor(mContext,R.color.white));
            mHolder.name.setTextColor(ContextCompat.getColor(mContext,R.color.white));
        }else {
            mHolder.bgItem.setBackground(null);
            mHolder.language.setTextColor(ContextCompat.getColor(mContext,R.color.black_800));
            mHolder.name.setTextColor(ContextCompat.getColor(mContext,R.color.black_800));
            if (position % 2 == 0)
                mHolder.bgItem.setBackgroundColor(ContextCompat.getColor(mContext,R.color.gray_50));
            else
                mHolder.bgItem.setBackground(null);
        }
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
        return mListSubtitle.size();
    }

    public static class SubtitleViewHolder extends RecyclerView.ViewHolder {

        private final TextView language;
        private final TextView name;
        private final LinearLayout root;
        private final LinearLayout bgItem;
        SubtitleViewHolder(View itemView) {
            super(itemView);
            language = (TextView) itemView.findViewById(R.id.txt_language);
            name = (TextView) itemView.findViewById(R.id.txt_name);
            root =  itemView.findViewById(R.id.root);
            bgItem =  itemView.findViewById(R.id.bg_item);
        }
    }

}
