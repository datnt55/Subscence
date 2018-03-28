package subscene.datnt.com.subscene;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.timehop.stickyheadersrecyclerview.StickyRecyclerHeadersAdapter;

import java.util.ArrayList;

/**
 * Created by DatNT on 3/26/2018.
 */

public class FilmHeadersAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder> {
    private ArrayList<Film> films = new ArrayList<>();
    private Context mContext;
    private OnItemClickListener listener;

    public FilmHeadersAdapter(ArrayList<Film> films, Context mContext) {
        this.films = films;
        this.mContext = mContext;
        setHasStableIds(true);
    }

    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_film, parent, false);
        return (VH) new FilmViewHolder(view) {
        };
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        final FilmViewHolder mHolder = (FilmViewHolder) holder;
        mHolder.mName.setText(films.get(position).getName());
        mHolder.mCount.setText(films.get(position).getSubCount());
        mHolder.root.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null)
                    listener.onItemClick(position);
            }
        });
    }

    @Override
    public long getHeaderId(int position) {
        return films.get(position).getType().charAt(0);
    }

    @Override
    public RecyclerView.ViewHolder onCreateHeaderViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_header, parent, false);
        return new HeaderFilmViewHolder(view) {
        };
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder, int position) {
        final HeaderFilmViewHolder mHolder = (HeaderFilmViewHolder) holder;
        mHolder.mTitle.setText(films.get(position).getType());
        //holder.itemView.setBackgroundColor(getRandomColor());
    }

    @Override
    public long getItemId(int position) {
        return films.get(position).hashCode();
    }

    @Override
    public int getItemCount() {
        return films.size();
    }

    public static class FilmViewHolder extends RecyclerView.ViewHolder {

        private final TextView mName;
        private final TextView mCount;
        private final LinearLayout root;
        FilmViewHolder(View itemView) {
            super(itemView);
            mName = itemView.findViewById(R.id.txt_title);
            mCount = itemView.findViewById(R.id.txt_sub_count);
            root = itemView.findViewById(R.id.root);
        }
    }

    public static class HeaderFilmViewHolder extends RecyclerView.ViewHolder {

        private final TextView mTitle;
        HeaderFilmViewHolder(View itemView) {
            super(itemView);
            mTitle = itemView.findViewById(R.id.itemView);
        }
    }


}