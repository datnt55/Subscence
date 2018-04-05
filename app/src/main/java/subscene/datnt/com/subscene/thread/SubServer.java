package subscene.datnt.com.subscene.thread;

/**
 * Created by DatNT on 4/5/2018.
 */

public abstract class SubServer {
    public abstract void getMovieSubsByName(String moviename, String language);
    public abstract void getLinkDownloadSubtitle(String url);
    public abstract void searchSubsFromMovieName(String url,String language);
}
