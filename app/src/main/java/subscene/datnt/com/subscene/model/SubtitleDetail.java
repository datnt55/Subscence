package subscene.datnt.com.subscene.model;

import java.util.HashMap;

/**
 * Created by DatNT on 3/27/2018.
 */

public class SubtitleDetail {
    private String id;
    private String content;

    public SubtitleDetail(String id, String content) {
        this.id = id;
        this.content = content;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
