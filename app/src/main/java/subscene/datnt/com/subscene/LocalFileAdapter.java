package subscene.datnt.com.subscene;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.util.ArrayList;

/**
 * Adapter for detail of a recent call
 */
public class LocalFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<File> mListFile;
    private DisplayImageOptions options;
    private OnItemClickListener listener;

    public LocalFileAdapter(Context mContext, ArrayList<File> mListFile) {
        this.mContext = mContext;
        this.mListFile = mListFile;

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_local_file, parent, false);
        return new SubtitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SubtitleViewHolder mHolder = (SubtitleViewHolder) holder;
        mHolder.fileName.setText(mListFile.get(position).getName());
        mHolder.path.setText(mListFile.get(position).getPath());

    }



    @Override
    public int getItemCount() {
        return mListFile.size();
    }

    public static class SubtitleViewHolder extends RecyclerView.ViewHolder {

        private final TextView fileName;
        private final TextView path;
        private final RelativeLayout root;
        private final LinearLayout bgItem;
        SubtitleViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.txt_file);
            path = (TextView) itemView.findViewById(R.id.txt_path);
            root =  itemView.findViewById(R.id.root);
            bgItem =  itemView.findViewById(R.id.bg_item);
        }
    }

}
