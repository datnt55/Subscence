package subscene.datnt.com.subscene.model;

/**
 * Created by DatNT on 3/29/2018.
 */

public class PopularFilm extends  Film {
    private String date;
    private String download;

    public PopularFilm(String name, String url, String date, String download) {
        super(name, url);
        this.date = date;
        this.download = download;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }
}
