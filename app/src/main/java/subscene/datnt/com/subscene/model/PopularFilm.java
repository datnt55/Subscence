package subscene.datnt.com.subscene.model;

/**
 * Created by DatNT on 3/29/2018.
 */

public class PopularFilm extends  Film {
    private String language;
    private String download;

    public PopularFilm(String name, String url, String language, String download) {
        super(name, url);
        this.language = language;
        this.download = download;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDownload() {
        return download;
    }

    public void setDownload(String download) {
        this.download = download;
    }
}
