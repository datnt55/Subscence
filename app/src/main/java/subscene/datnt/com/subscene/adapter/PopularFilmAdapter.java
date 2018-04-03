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
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.PopularFilm;
import subscene.datnt.com.subscene.utils.CommonUtils;


/**
 * Adapter holding a list of animal names of type String. Note that each item must be unique.
 */
public class PopularFilmAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final ArrayList<PopularFilm> mListFile;
    private OnItemClickListener listener;

    public PopularFilmAdapter(Context mContext, ArrayList<PopularFilm> mListFile) {
        this.mContext = mContext;
        this.mListFile = mListFile;

    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_popular_film, parent, false);
        return new FileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FileViewHolder mHolder = (FileViewHolder) holder;
        mHolder.txtName.setText(mListFile.get(position).getName());
        mHolder.txtDate.setText(mListFile.get(position).getDate());
        mHolder.txtDownload.setText(mListFile.get(position).getDownload()+ " downloads");
    }

    public String getFileExtension(String fileName) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i + 1);
        } else
            return null;
    }

    @Override
    public int getItemCount() {
        return mListFile.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtName;
        private final TextView txtDownload;
        private final TextView txtDate;
        private final RelativeLayout root;

        FileViewHolder(View itemView) {
            super(itemView);
            txtDownload = itemView.findViewById(R.id.txt_download);
            txtName = itemView.findViewById(R.id.txt_name);
            txtDate = itemView.findViewById(R.id.txt_date);
            root = itemView.findViewById(R.id.root);
        }
    }
}