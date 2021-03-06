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
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import subscene.datnt.com.subscene.model.Language;
import subscene.datnt.com.subscene.thread.DownloadFileFromURL;
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
        AdapterView.OnItemSelectedListener, DownloadFileFromURL.DownloadFileListener {
    private ProgressBar progressBar;
    private RecyclerView listFilm;
    private ArrayList<File> localFiles = new ArrayList<>();
    private String url = "https://subscene.com/subtitles/title?q=aven&l=";
    private ProgressDialog dialog;
    private Spinner spnLanguage;
    private ArrayList<Language> languages = new ArrayList<>();
    private String currentPath;
    private TextView noVideo;

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
        View v = inflater.inflate(R.layout.fragment_auto_download, container, false);
        progressBar = v.findViewById(R.id.progressBar);
        noVideo = v.findViewById(R.id.txt_no_video);
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
        for (int i = 0; i < file.length; i++) {
            File subFile = file[i];
            if (subFile.isDirectory()) {
                files.addAll(getAllMediaFileInFolder(subFile));
            } else if (fileFilter.checkFileExtension(subFile))
                files.add(subFile);
        }
        return files;
    }

    private ArrayList<File> getAllMediaFileInFolder(File file) {
        ArrayList<File> files = new ArrayList<>();
        AudioFileFilter fileFilter = new AudioFileFilter();
        for (File f : file.listFiles()) {
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
        currentPath = FilenameUtils.getFullPath(localFiles.get(position).getAbsolutePath());
        int i = fileName.lastIndexOf('.');
        if (i > 0)
            fileName = fileName.substring(0, i);
        String url = "https://subscene.com/subtitles/title?q=" + fileName + "&l=";
        Language ownLanguage = new SharePreference(getActivity()).getCurrentLanguage();
        new SeachFilmAsynTask(ownLanguage.getName(), this).execute(url);
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
        //Toast.makeText(getActivity(), url, Toast.LENGTH_SHORT).show();
        new DownloadFileFromURL(getActivity(),currentPath,this).execute("https://subscene.com"+url);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        //Toast.makeText(getActivity(), languages.get(i), Toast.LENGTH_LONG).show();
        //ownLanguage = languages.get(i).getName();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onDownloaded(boolean isDownload) {
        if (!isDownload){
            Toast.makeText(getActivity(),"Can't found subtitile",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getActivity(),"Download subtitile success",Toast.LENGTH_SHORT).show();
        }
    }

    private class GetAllFileInStorage extends AsyncTask<Void, Void, ArrayList<File>> {

        @Override
        protected ArrayList<File> doInBackground(Void... voids) {
            SharePreference preference = new SharePreference(getActivity());
            languages = preference.getLanguage();
            Collections.sort(languages, new Comparator<Language>() {
                @Override
                public int compare(Language s1, Language s2) {
                    return s1.getName().compareToIgnoreCase(s2.getName());
                }
            });
            File[] file = Environment.getExternalStorageDirectory().listFiles(new AudioFileFilter());
            return getAllMediaFile(file);
        }

        @Override
        protected void onPostExecute(ArrayList<File> files) {
            super.onPostExecute(files);
            progressBar.setVisibility(View.GONE);
            if (files.size() == 0){
                noVideo.setVisibility(View.VISIBLE);
                listFilm.setVisibility(View.GONE);
                return;
            }
            localFiles = files;
            LocalFileAdapter adapter = new LocalFileAdapter(getActivity(), localFiles);
            adapter.setOnItemClickListener(AutoDownloadFragment.this);
            listFilm.setAdapter(adapter);

            spnLanguage.setOnItemSelectedListener(AutoDownloadFragment.this);
            ArrayList<String> languageName = new ArrayList<>();
            for (Language language : languages)
                languageName.add(language.getName());
            ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item, languageName);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spnLanguage.setAdapter(aa);
            Language lang = new SharePreference(getActivity()).getCurrentLanguage();
            for (int i = 0; i < languages.size(); i++) {
                if (languages.get(i).getId().equals(lang.getId())) {
                    spnLanguage.setSelection(i);
                    break;
                }
            }

        }
    }
}
