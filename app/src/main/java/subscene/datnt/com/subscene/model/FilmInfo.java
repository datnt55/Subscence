package subscene.datnt.com.subscene.model;

public class FilmInfo {
    private String title;
    private String year;
    private String Released;
    private String runTime;
    private String Genre;
    private String country;
    private String rating;

    public FilmInfo(String title, String year, String released, String runTime, String genre, String country, String rating) {
        this.title = title;
        this.year = year;
        Released = released;
        this.runTime = runTime;
        Genre = genre;
        this.country = country;
        this.rating = rating;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getReleased() {
        return Released;
    }

    public void setReleased(String released) {
        Released = released;
    }

    public String getRunTime() {
        return runTime;
    }

    public void setRunTime(String runTime) {
        this.runTime = runTime;
    }

    public String getGenre() {
        return Genre;
    }

    public void setGenre(String genre) {
        Genre = genre;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }
}
