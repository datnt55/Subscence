package subscene.datnt.com.subscene.thread;

import subscene.datnt.com.subscene.listener.OnSceneListener;
import subscene.datnt.com.subscene.utils.ServerType;

/**
 * Created by DatNT on 4/5/2018.
 */

public abstract class SubServer {
    public abstract void getMovieSubsByName(String moviename, String language);
    public abstract void getLinkDownloadSubtitle(String url);
    public abstract void searchSubsFromMovieName(String url,String language);
    public abstract void release();
}
