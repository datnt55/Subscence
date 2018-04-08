package subscene.datnt.com.subscene.model;

import subscene.datnt.com.subscene.utils.ServerType;

/**
 * Created by DatNT on 4/5/2018.
 */

public class YiFyFilm extends Film {
    private String duration;
    private String year;
    private String actor;
    private String description;
    private String poster;

    public YiFyFilm(ServerType server, String name, String url, String duration, String year, String actor, String description, String poster) {
        super(server, name, url);
        this.duration = duration;
        this.year = year;
        this.actor = actor;
        this.description = description;
        this.poster = poster;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }
}
