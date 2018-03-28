package subscene.datnt.com.subscene;

import android.support.annotation.Nullable;
import android.support.v4.util.ArrayMap;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by DatNT on 01/12/2017.
 * Base RecyclerView Adapter with header and footer
 */
public abstract class SectionedRecyclerViewAdapter<VH extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<VH> {

    public abstract int getSectionCount();
    public abstract int getListCount();
    public abstract int getItemCount(int section);
    public abstract boolean isHaveFooter ();
    public abstract VH onCreateViewHolder(ViewGroup parent, boolean header, boolean footer);
    public abstract void onBindHeaderViewHolder(VH holder, int section);
    public abstract void onBindFooterViewHolder(VH holder);
    public abstract void onBindViewHolder(VH holder, int section, int relativePosition, int absolutePosition);
    private final static int VIEW_TYPE_HEADER = 0;
    private final static int VIEW_TYPE_ITEM = 1;
    private final static int VIEW_TYPE_FOOTER = 2;
    private final ArrayMap<Integer, Integer> mHeaderLocationMap;
    private GridLayoutManager mLayoutManager;


    public SectionedRecyclerViewAdapter() {
        mHeaderLocationMap = new ArrayMap<>();
    }

    public final boolean isHeader(int position) {
        return mHeaderLocationMap.get(position) != null;
    }

    public final void setLayoutManager(@Nullable GridLayoutManager lm) {
        mLayoutManager = lm;
        if (lm == null) return;
        lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (isHeader(position))
                    return mLayoutManager.getSpanCount();
                return 1;
            }
        });
    }

    @Override
    public final int getItemCount() {
        int count = 0;
        mHeaderLocationMap.clear();
        for (int s = 0; s < getSectionCount(); s++) {
            mHeaderLocationMap.put(count, s);
            count += getItemCount(s) + 1;
        }
        if (isHaveFooter()) {
            count += 1;
            return count;
        }else
            return count;
    }

    /**
     * @hide
     * @deprecated
     */
    @Override
    @Deprecated
    public final VH onCreateViewHolder(ViewGroup parent, int viewType) {
        return onCreateViewHolder(parent, viewType == VIEW_TYPE_HEADER,viewType == VIEW_TYPE_FOOTER);
    }

    /**
     * @hide
     * @deprecated
     */
    @Deprecated
    @Override
    public final int getItemViewType(int position) {
        if (isHeader(position))
            return VIEW_TYPE_HEADER;
        else if (position == getListCount()-1 && isHaveFooter())
            return VIEW_TYPE_FOOTER;
        else
            return VIEW_TYPE_ITEM;
    }

    /**
     * @hide
     * @deprecated
     */
    @Override
    @Deprecated
    public final void onBindViewHolder(VH holder, int position) {
        StaggeredGridLayoutManager.LayoutParams layoutParams = null;
        if (holder.itemView.getLayoutParams() instanceof GridLayoutManager.LayoutParams)
            layoutParams = new StaggeredGridLayoutManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        else if (holder.itemView.getLayoutParams() instanceof StaggeredGridLayoutManager.LayoutParams)
            layoutParams = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
        if (isHeader(position)) {
            if (layoutParams != null) layoutParams.setFullSpan(true);
            onBindHeaderViewHolder(holder, mHeaderLocationMap.get(position));
        }else if (position == getListCount() - 1 && isHaveFooter()) {
            if (layoutParams != null) layoutParams.setFullSpan(true);
            onBindFooterViewHolder(holder);
        } else {
            if (layoutParams != null) layoutParams.setFullSpan(false);
            final int[] sectionAndPos = getSectionIndexAndRelativePosition(position);
            onBindViewHolder(holder, sectionAndPos[0],
                    // offset section view positions
                    sectionAndPos[1],
                    position - (sectionAndPos[0] + 1));
        }
        if (layoutParams != null)
            holder.itemView.setLayoutParams(layoutParams);
    }

    /**
     * @hide
     * @deprecated
     */
    @Deprecated
    @Override
    public final void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    // Returns section along with offset position
    private int[] getSectionIndexAndRelativePosition(int itemPosition) {
        synchronized (mHeaderLocationMap) {
            Integer lastSectionIndex = -1;
            for (final Integer sectionIndex : mHeaderLocationMap.keySet()) {
                if (itemPosition > sectionIndex) {
                    lastSectionIndex = sectionIndex;
                } else {
                    break;
                }
                lastSectionIndex = sectionIndex;
            }
            return new int[]{mHeaderLocationMap.get(lastSectionIndex), itemPosition - lastSectionIndex - 1};
        }
    }
}