package subscene.datnt.com.subscene.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import subscene.datnt.com.subscene.thread.GetLanguagesAsynTask;
import subscene.datnt.com.subscene.thread.SeachFilmAsynTask;
import subscene.datnt.com.subscene.listener.OnItemClickListener;
import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.utils.AudioFileFilter;
import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.adapter.LocalFileAdapter;
import subscene.datnt.com.subscene.utils.SharePreference;

public class AutoDownloadFragment extends Fragment implements
        OnItemClickListener,
        SeachFilmAsynTask.OnSearchFilmListener,
        AdapterView.OnItemSelectedListener{
    private ProgressBar progressBar;
    private RecyclerView listFilm;
    private ArrayList<File> localFiles = new ArrayList<>();
    private String url = "https://subscene.com/subtitles/title?q=aven&l=";
    private ProgressDialog dialog;
    private Spinner spnLanguage;
    private ArrayList<String> languages = new ArrayList<>();
    private String ownLanguage;
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
        spnLanguage = v.findViewById(R.id.spr_language);
        new GetAllFileInStorage().execute();
        return v;
    }


    private ArrayList<File> getAllMediaFile(File[] file) {
        ArrayList<File> files = new ArrayList<>();
        AudioFileFilter fileFilter = new AudioFileFilter();
        for (int i = 0 ; i < file.length; i++){
            File subFile = file[i];
            if (subFile.isDirectory()) {
                files.addAll(getAllMediaFileInFolder(subFile));
            }
            else if (fileFilter.checkFileExtension(subFile))
                files.add(subFile);
        }
        return files;
    }

    private ArrayList<File> getAllMediaFileInFolder(File file){
        ArrayList<File> files = new ArrayList<>();
        AudioFileFilter fileFilter = new AudioFileFilter();
        for (File f : file.listFiles())
        {
            if (f.isDirectory())
                files.addAll(getAllMediaFileInFolder(f));
            else if (fileFilter.checkFileExtension(f))
                files.add(f);
        }
        return files;
    }


    private void showDialogNoResult() {
        AlertDialog.Builder builder;
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Subtitle Downloader")
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
            fileName = fileName.substring(0,i);
        String url  = "https://subscene.com/subtitles/title?q="+fileName+"&l=";
        new SeachFilmAsynTask(ownLanguage, this).execute(url);
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

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
       //Toast.makeText(getActivity(), languages.get(i), Toast.LENGTH_LONG).show();
        ownLanguage = languages.get(i);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private class GetAllFileInStorage extends AsyncTask<Void, Void, ArrayList<File>>{

        @Override
        protected ArrayList<File> doInBackground(Void... voids) {
            SharePreference preference = new SharePreference(getActivity());
            languages = preference.getLanguage();
            Collections.sort(languages, new Comparator<String>() {
                @Override
                public int compare(String s1, String s2) {
                    return s1.compareToIgnoreCase(s2);
                }
            });
            File[] file = Environment.getExternalStorageDirectory().listFiles(new AudioFileFilter());
            return  getAllMediaFile(file);
        }

        @Override
        protected void onPostExecute(ArrayList<File> files) {
            super.onPostExecute(files);
            progressBar.setVisibility(View.GONE);
            localFiles = files;
            LocalFileAdapter adapter = new LocalFileAdapter(getActivity(),localFiles);
            adapter.setOnItemClickListener(AutoDownloadFragment.this);
            listFilm.setAdapter(adapter);

            spnLanguage.setOnItemSelectedListener(AutoDownloadFragment.this);
            ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,languages);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnLanguage.setAdapter(aa);
        }
    }
}
