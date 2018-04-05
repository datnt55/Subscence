package subscene.datnt.com.subscene.model;

import java.io.Serializable;

/**
 * Created by DatNT on 4/5/2018.
 */

public class MovieHint implements Serializable{
    private String name;
    private String url;

    public MovieHint(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
