package subscene.datnt.com.subscene;

import java.io.Serializable;

/**
 * Created by DatNT on 3/26/2018.
 */

public class Subtitle implements Serializable{
    private String title;
    private String languague;
    private String poster;
    private String link;
    private String year;

    public Subtitle(String title, String languague, String poster,String link, String year) {
        this.title = title;
        this.languague = languague;
        this.poster = poster;
        this.link = link;
        this.year = year;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguague() {
        return languague;
    }

    public void setLanguague(String languague) {
        this.languague = languague;
    }

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getLink() {
        return "https://subscene.com"+link ;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
