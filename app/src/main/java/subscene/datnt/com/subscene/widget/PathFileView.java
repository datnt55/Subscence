package subscene.datnt.com.subscene.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.adapter.FileHierarchyAdapter;
import subscene.datnt.com.subscene.adapter.FilePickerListAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;

/**
 * Created by DatNT on 4/2/2018.
 */

public class PathFileView extends LinearLayout {
    private Context mContext;
    private ImageView img;
    private TextView txtName;
    private File file;
    private FilePathSelectListener listener;

    public PathFileView(Context context, File file,FilePathSelectListener listener ) {
        super(context);
        mContext = context;
        this.file = file;
        this.listener = listener;
        initLayout();
    }

    public PathFileView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initLayout();
    }

    public PathFileView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initLayout();
    }


    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.view_path_file, this, true);
        txtName = view.findViewById(R.id.txt_node);
        img = view.findViewById(R.id.imageView);
        txtName.setText(file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("/")+1));
        txtName.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener !=null)
                    listener.onPathSelect(file);
            }
        });
    }

    public void setRoot(){
        img.setVisibility(GONE);
    }

    public void setCurrent(){
        txtName.setTypeface(Typeface.DEFAULT_BOLD);
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public interface FilePathSelectListener{
        void onPathSelect(File file);
    }

}
