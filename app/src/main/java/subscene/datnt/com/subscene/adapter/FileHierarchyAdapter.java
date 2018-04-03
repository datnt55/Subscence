package subscene.datnt.com.subscene.adapter;

/**
 * Created by DatNT on 3/26/2018.
 */

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.listener.OnItemClickListener;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public class FileHierarchyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<File> mListFile = new ArrayList<>();
    private OnFileClickListener listener;

    public FileHierarchyAdapter(Context mContext, File root) {
        this.mContext = mContext;
        mListFile.add(root);
        String a = root.getName();
        while (!root.getName().equals("sdcard")){
            mListFile.add(0,root.getParentFile());
            root = root.getParentFile();
        }

    }

    public void updateHierarchy(File root){
        mListFile.clear();
        mListFile.add(root);
        while (!root.getName().equals("sdcard")){
            mListFile.add(0,root.getParentFile());
            root = root.getParentFile();
        }
        notifyDataSetChanged();
    }
    public void setOnItemClickListener(OnFileClickListener listener){
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_file_hierarchy, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FileViewHolder mHolder = (FileViewHolder) holder;
        if (position == mListFile.size() -1 ){
            mHolder.txtName.setTypeface(Typeface.DEFAULT_BOLD);
        }else
            mHolder.txtName.setTypeface(Typeface.DEFAULT);
        if (position == 0)
            mHolder.img.setVisibility(View.GONE);
        else
            mHolder.img.setVisibility(View.VISIBLE);
        mHolder.txtName.setText(mListFile.get(position).getName());
        mHolder.txtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onFilePathClick(mListFile.get(position));
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

    public static class FileViewHolder extends RecyclerView.ViewHolder {
        private final ImageView img;
        private final TextView txtName;
        FileViewHolder(View itemView) {
            super(itemView);
            txtName = itemView.findViewById(R.id.txt_node);
            img = itemView.findViewById(R.id.imageView);
        }
    }
    public interface  OnFileClickListener{
        void onFilePathClick(File directory);

    }
}
