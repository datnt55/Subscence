package subscene.datnt.com.subscene.widget;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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

public class FilePickerBottomSheet extends RelativeLayout implements OnItemClickListener, FileHierarchyAdapter.OnFileClickListener, View.OnClickListener {
    private Context mContext;
    public final static String EXTRA_FILE_PATH = "file_path";

    public final static String EXTRA_SHOW_HIDDEN_FILES = "show_hidden_files";

    public final static String EXTRA_ACCEPTED_FILE_EXTENSIONS = "accepted_file_extensions";

    private final static String DEFAULT_INITIAL_DIRECTORY = "/sdcard/";

    protected File mDirectory;
    protected ArrayList<File> mFiles;
    protected FilePickerListAdapter mAdapter;
    private FileHierarchyAdapter adapter;
    protected boolean mShowHiddenFiles = false;
    protected String[] acceptedFileExtensions;
    private RecyclerView listFolder, listHierarchy;
    private TextView txtNoFiles, txtOk, txtCancel;
    private FilePickerListener listener;
    private String fileName;

    public FilePickerBottomSheet(Context context) {
        super(context);
        mContext = context;
        initLayout();
    }

    public FilePickerBottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initLayout();
    }

    public FilePickerBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initLayout();
    }

    public void setOnFilePickerListener(FilePickerListener listener){
        this.listener = listener;
    }
    private void initLayout() {
        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View view = mInflater.inflate(R.layout.file_picker_empty_view, this, true);
        txtNoFiles = view.findViewById(R.id.txt_no_file);
        txtOk = view.findViewById(R.id.txt_ok);
        txtOk.setOnClickListener(this);
        txtCancel = view.findViewById(R.id.txt_cancel);
        txtCancel.setOnClickListener(this);
        listFolder = view.findViewById(R.id.list_folder);
        listFolder.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        listFolder.setLayoutManager(mLayoutManager);
        MarginDividerDecoration dividerItemDecoration = new MarginDividerDecoration(mContext);
        listFolder.addItemDecoration(dividerItemDecoration);

        listHierarchy = view.findViewById(R.id.list_hierarchy);
        LinearLayoutManager hLayoutManager = new LinearLayoutManager(mContext,LinearLayoutManager.HORIZONTAL, false);
        listHierarchy.setLayoutManager(hLayoutManager);

        mDirectory = new File(DEFAULT_INITIAL_DIRECTORY);
        mFiles = new ArrayList<File>();
        mAdapter = new FilePickerListAdapter(mContext, mFiles);
        mAdapter.setOnItemClickListener(this);
        listFolder.setAdapter(mAdapter);

        adapter = new FileHierarchyAdapter(mContext, mDirectory);
        adapter.setOnItemClickListener(this);
        listHierarchy.setAdapter(adapter);

        acceptedFileExtensions = new String[] {};
        mShowHiddenFiles = false;
        refreshFilesList();
    }

    /**
     * Updates the list view to the current directory
     */
    protected void refreshFilesList() {
        mFiles.clear();
        ExtensionFilenameFilter filter = new ExtensionFilenameFilter(acceptedFileExtensions);

        File[] files = mDirectory.listFiles(filter);
        if(files != null && files.length > 0) {
            for(File f : files) {
                if(f.isHidden() && !mShowHiddenFiles) {
                    continue;
                }
                mFiles.add(f);
            }

            Collections.sort(mFiles, new FileComparator());
            mAdapter.notifyDataSetChanged();
            txtNoFiles.setVisibility(GONE);
            listFolder.setVisibility(VISIBLE);
        }else {
            txtNoFiles.setVisibility(VISIBLE);
            listFolder.setVisibility(GONE);
        }
        adapter.updateHierarchy(mDirectory);
    }

    public void onBackPressed() {
        if (mDirectory.getAbsolutePath().equals("/sdcard"))
            return;
        if(mDirectory.getParentFile() != null) {
            mDirectory = mDirectory.getParentFile();
            refreshFilesList();
            return;
        }
    }

    @Override
    public void onItemClick(int position) {
        File newFile = mFiles.get(position);
        if(newFile.isFile()) {
            if (listener != null)
                listener.onCopy(FilenameUtils.getBaseName(newFile.getAbsolutePath()), mDirectory);
        } else {
            mDirectory = newFile;
            // Update the files list
            refreshFilesList();
        }
    }

    @Override
    public void onFilePathClick(File directory) {
        mDirectory = directory;
        refreshFilesList();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.txt_cancel:
                if (listener != null)
                    listener.onCancelCopy();
                break;
            case R.id.txt_ok:
                break;
        }
    }

    private class FileComparator implements Comparator<File> {
        @Override
        public int compare(File f1, File f2) {
            if(f1 == f2) {
                return 0;
            }
            if(f1.isDirectory() && f2.isFile()) {
                return -1;
            }
            if(f1.isFile() && f2.isDirectory()) {
                return 1;
            }
            return f1.getName().compareToIgnoreCase(f2.getName());
        }
    }

    private class ExtensionFilenameFilter implements FilenameFilter {
        private String[] mExtensions;

        public ExtensionFilenameFilter(String[] extensions) {
            super();
            mExtensions = extensions;
        }

        @Override
        public boolean accept(File dir, String filename) {
            if(new File(dir, filename).isDirectory()) {
                return true;
            }
            if(mExtensions != null && mExtensions.length > 0) {
                for(int i = 0; i < mExtensions.length; i++) {
                    if(filename.endsWith(mExtensions[i])) {
                        return true;
                    }
                }
                return false;
            }
            return true;
        }
    }

    public interface FilePickerListener{
        void onCopy(String filename, File path);
        void onCancelCopy();
    }
}
