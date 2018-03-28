package subscene.datnt.com.subscene;

import java.io.Serializable;

/**
 * Created by DatNT on 3/26/2018.
 */

public class Film implements Serializable{
    private String name;
    private String url;
    private String subCount;
    private String type;

    public Film() {
    }

    public Film(String name, String url, String subCount) {
        this.name = name;
        this.url = url;
        this.subCount = subCount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return "https://subscene.com"+url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getSubCount() {
        return subCount;
    }

    public void setSubCount(String subCount) {
        this.subCount = subCount;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
