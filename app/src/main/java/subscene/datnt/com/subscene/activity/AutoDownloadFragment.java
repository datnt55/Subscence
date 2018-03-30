package subscene.datnt.com.subscene.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import subscene.datnt.com.subscene.thread.SeachFilmAsynTask;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.utils.AudioFileFilter;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.adapter.LocalFileAdapter;

public class AutoDownloadFragment extends Fragment implements OnItemClickListener, SeachFilmAsynTask.OnSearchFilmListener{
    private ProgressBar progressBar;
    private RecyclerView listFilm;
    private ArrayList<File> localFiles = new ArrayList<>();
    private String url = "https://subscene.com/subtitles/title?q=aven&l=";
    private ProgressDialog dialog;
    public AutoDownloadFragment() {
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
        View v =  inflater.inflate(R.layout.fragment_auto_download, container, false);
        progressBar = v.findViewById(R.id.progressBar);
        listFilm = v.findViewById(R.id.list_local_file);
        listFilm.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        listFilm.setLayoutManager(mLayoutManager);

        File[] file = Environment.getExternalStorageDirectory().listFiles(new AudioFileFilter());
        //localFiles = new ArrayList<>(Arrays.asList(file));
        getAllMediaFile(file);
        LocalFileAdapter adapter = new LocalFileAdapter(getActivity(),localFiles);
        adapter.setOnItemClickListener(this);
        listFilm.setAdapter(adapter);
        return v;
    }

    private void getAllMediaFile(File[] file) {
        AudioFileFilter fileFilter = new AudioFileFilter();
        for (int i = 0 ; i < file.length; i++){
            File subFile = file[i];
            if (subFile.isDirectory())
                getAllMediaFileInFolder(subFile);
            else if (fileFilter.checkFileExtension(subFile))
                localFiles.add(subFile);
        }
    }

    private void getAllMediaFileInFolder(File file){
        AudioFileFilter fileFilter = new AudioFileFilter();
        for (File f : file.listFiles())
        {
            if (f.isDirectory())
                getAllMediaFileInFolder(f);
            else if (fileFilter.checkFileExtension(f))
                localFiles.add(f);
        }
    }


    private void showDialogNoResult() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(getActivity());
        }
        builder.setTitle("ISub")
                .setMessage("Do not found subtitle fit your file")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    @Override
    public void onItemClick(int position) {
        String fileName = localFiles.get(position).getName();
        int i = fileName.lastIndexOf('.');
        if (i > 0)
            fileName = fileName.substring(0,i-1);
        String url  = "https://subscene.com/subtitles/title?q="+fileName+"&l=";
        new SeachFilmAsynTask(url, this).execute(url);
    }

    @Override
    public void onStartSearch() {
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Searching subtitle...");
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    @Override
    public void onSearchNoResult() {
        dialog.dismiss();
        showDialogNoResult();
    }

    @Override
    public void onSearchSuccess(String url) {
        dialog.dismiss();
        Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
    }
}
