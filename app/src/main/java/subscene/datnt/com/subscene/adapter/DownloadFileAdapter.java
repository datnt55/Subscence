package subscene.datnt.com.subscene.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.util.ArrayList;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.utils.CommonUtils;

/**
 * Adapter for detail of a recent call
 */
public class DownloadFileAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<File> mListFile;
    private DisplayImageOptions options;
    private OnDownloadedFileClickListener listener;

    public DownloadFileAdapter(Context mContext, ArrayList<File> mListFile) {
        this.mContext = mContext;
        this.mListFile = mListFile;

    }

    public void setOnItemClickListener(OnDownloadedFileClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_downloaded, parent, false);
        return new SubtitleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final SubtitleViewHolder mHolder = (SubtitleViewHolder) holder;
        mHolder.fileName.setText(mListFile.get(position).getName());
        mHolder.path.setText(mListFile.get(position).getParent());
        mHolder.txtMoveToVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onFileMove(position);
            }
        });
        mHolder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onFileDelete(position);
            }
        });
    }

    public String getFileExtension( String fileName ) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        } else
            return null;
    }

    @Override
    public int getItemCount() {
        return mListFile.size();
    }

    public static class SubtitleViewHolder extends RecyclerView.ViewHolder {

        private final TextView fileName;
        private final TextView path;
        private final RelativeLayout root;
        private final TextView txtMoveToVideo;
        private final TextView txtDelete;
        SubtitleViewHolder(View itemView) {
            super(itemView);
            fileName = (TextView) itemView.findViewById(R.id.txt_file_name);
            path = (TextView) itemView.findViewById(R.id.txt_path);
            root =  itemView.findViewById(R.id.root);
            txtMoveToVideo =  itemView.findViewById(R.id.txt_move_to_video);
            txtDelete = itemView.findViewById(R.id.txt_delete);
        }
    }

    public interface OnDownloadedFileClickListener{
        void onFileMove(int position);
        void onFileDelete(int position);
    }
}
