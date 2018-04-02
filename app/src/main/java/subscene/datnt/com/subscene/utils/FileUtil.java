package subscene.datnt.com.subscene.utils;

/**
 * Created by DatNT on 4/2/2018.
 */

public class FileUtil {
    public static String getFileExtension(String filename){
        return filename.substring(filename.lastIndexOf("."));
    }
}
