package subscene.datnt.com.subscene.model;

import java.io.Serializable;

/**
 * Created by DatNT on 3/26/2018.
 */

public class Film implements Serializable{
    protected String name;
    protected String url;
    protected String subCount;
    protected String type;

    public Film() {
    }
    public Film(String name, String url) {
        this.name = name;
        this.url = url;
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