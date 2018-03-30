package subscene.datnt.com.subscene.model;

/**
 * Created by DatNT on 3/29/2018.
 */

public class LocalFile {
    private String name;
    private String path;

    public LocalFile(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
