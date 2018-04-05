package subscene.datnt.com.subscene.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.adapter.DownloadFileAdapter;
import subscene.datnt.com.subscene.adapter.LocalFileAdapter;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.thread.SeachFilmAsynTask;
import subscene.datnt.com.subscene.utils.AudioFileFilter;
import subscene.datnt.com.subscene.utils.SharePreference;
import subscene.datnt.com.subscene.widget.FilePickerBottomSheet;

import static subscene.datnt.com.subscene.utils.Globals.APP_FOLDER;

public class DownloadedFragment extends Fragment implements DownloadFileAdapter.OnDownloadedFileClickListener{
    private ProgressBar progressBar;
    private RecyclerView listFileDownloaded;
    private ArrayList<File> downloadedFiles = new ArrayList<>();
    private DownloadFileAdapter adapter;

    public interface OnDataPass {
        public void onDataPass(File data);
    }

    private OnDataPass dataPasser;
    public DownloadedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_downloaded, container, false);
        progressBar = v.findViewById(R.id.progressBar);
        listFileDownloaded = v.findViewById(R.id.list_download_file);
        listFileDownloaded.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listFileDownloaded.setLayoutManager(mLayoutManager);
        getListDownloadedFile();

        adapter = new DownloadFileAdapter(getActivity(), downloadedFiles);
        adapter.setOnItemClickListener(this);
        listFileDownloaded.setAdapter(adapter);
        progressBar.setVisibility(View.GONE);
        return v;
    }

    private void getListDownloadedFile() {
        downloadedFiles = new ArrayList<>();
        File root = new File(APP_FOLDER);
        for (File file : root.listFiles()){
            downloadedFiles.add(file);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        dataPasser = (OnDataPass) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onFileMove(int position) {
        if (dataPasser != null)
            dataPasser.onDataPass(downloadedFiles.get(position));
    }

    @Override
    public void onFileDelete(final int position) {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Notice")
                .setMessage("Do you want to delete "+downloadedFiles.get(position).getName()+ " ?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                        downloadedFiles.get(position).delete();
                        downloadedFiles.remove(position);
                        adapter.notifyItemRemoved(position);
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();

    }

}
