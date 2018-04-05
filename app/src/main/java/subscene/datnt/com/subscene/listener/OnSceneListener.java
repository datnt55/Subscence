package subscene.datnt.com.subscene.listener;

import java.util.ArrayList;

import subscene.datnt.com.subscene.model.Film;
import subscene.datnt.com.subscene.model.Subtitle;

/**
 * Created by DatNT on 4/5/2018.
 */

public interface OnSceneListener {
    void onFoundFilm(String query, ArrayList<Film> films);
    void onFoundLinkDownload(String poster, String linkDownload, String detail, String preview);
    void onFoundListSubtitle(ArrayList<Subtitle> listSubtitle);
}
