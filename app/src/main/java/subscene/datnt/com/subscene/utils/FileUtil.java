package subscene.datnt.com.subscene.utils;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by DatNT on 4/2/2018.
 */

public class FileUtil {
    public static String getFileExtension(String filename){
        return filename.substring(filename.lastIndexOf("."));
    }

    public static boolean move(String filename,File source, File des){
        InputStream inStream = null;
        OutputStream outStream = null;

        try{
            inStream = new FileInputStream(source);
            outStream = new FileOutputStream(new File(des,filename));

            byte[] buffer = new byte[1024];

            int length;
            //copy the file content in bytes
            while ((length = inStream.read(buffer)) > 0){

                outStream.write(buffer, 0, length);

            }

            inStream.close();
            outStream.close();

            //delete the original file
            source.delete();

            System.out.println("File is copied successful!");
            return true;
        }catch(IOException e){
            e.printStackTrace();
            return false;
        }
    }

    public static String getFilename(File file){
        String name = FilenameUtils.getName(file.getAbsolutePath());
        String path = file.getAbsolutePath();
        return path.substring(path.lastIndexOf("/")+1);
    }
}
