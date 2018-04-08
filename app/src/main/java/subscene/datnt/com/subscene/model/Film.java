package subscene.datnt.com.subscene.model;

import java.io.Serializable;

import subscene.datnt.com.subscene.utils.ServerType;

/**
 * Created by DatNT on 3/26/2018.
 */

public class Film implements Serializable{
    protected String name;
    protected String url;
    protected String subCount;
    protected String type;
    protected ServerType server;
    public Film() {
    }
    public Film(ServerType server, String name, String url) {
        this.server = server;
        this.name = name;
        this.url = url;
    }

    public Film(ServerType server,String name, String url, String subCount) {
        this.server = server;
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
        return url;
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

    public ServerType getServer() {
        return server;
    }

    public void setServer(ServerType server) {
        this.server = server;
    }
}
