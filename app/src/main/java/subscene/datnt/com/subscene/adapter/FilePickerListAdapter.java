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

import com.nostra13.universalimageloader.core.DisplayImageOptions;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.utils.CommonUtils;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public class FilePickerListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<File> mListFile;
    private OnItemClickListener listener;

    public FilePickerListAdapter(Context mContext, ArrayList<File> mListFile) {
        this.mContext = mContext;
        this.mListFile = mListFile;

    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.file_picker_list_item, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FileViewHolder mHolder = (FileViewHolder) holder;
        if(mListFile.get(position).isFile()) {
            mHolder.imageView.setImageResource(CommonUtils.getVideoIcon(getFileExtension(mListFile.get(position).getName())));
            mHolder.txtCount.setVisibility(View.GONE);
        } else {
            mHolder.txtCount.setVisibility(View.VISIBLE);
            mHolder.imageView.setImageResource(R.drawable.folder);
            File[] a = mListFile.get(position).listFiles();
            int count = mListFile.get(position).listFiles().length;
            if (count <= 1)
                mHolder.txtCount.setText(count+" item");
            else
                mHolder.txtCount.setText(count+" items");
        }
        mHolder.txtName.setText(mListFile.get(position).getName());
        mHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null){
                    listener.onItemClick(position);
                }
            }
        });

    }

    public String getFileExtension( String fileName ) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        } else
            return "";
    }

    @Override
    public int getItemCount() {
        return mListFile.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName;
        private final TextView txtCount;
        private final ImageView imageView;
        private final RelativeLayout root;
        FileViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.file_picker_image);
            txtName = itemView.findViewById(R.id.file_picker_text);
            txtCount = itemView.findViewById(R.id.txt_sub_count);
            root = itemView.findViewById(R.id.root);
        }
    }

}
