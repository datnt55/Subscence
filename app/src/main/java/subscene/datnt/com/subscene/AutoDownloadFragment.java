package subscene.datnt.com.subscene;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class AutoDownloadFragment extends Fragment {
    private ProgressBar progressBar;
    private RecyclerView listFilm;
    private ArrayList<File> localFiles = new ArrayList<>();
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


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }
    private class LoadData extends AsyncTask<String, Void, ArrayList<PopularFilm>> {
        @Override
        protected ArrayList<PopularFilm> doInBackground(String... strings) {
            File[] file = Environment.getExternalStorageDirectory().listFiles(new AudioFileFilter());
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<PopularFilm> films) {
            super.onPostExecute(films);


        }
    }
}
