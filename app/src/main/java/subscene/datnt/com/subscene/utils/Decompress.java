package subscene.datnt.com.subscene.utils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import subscene.datnt.com.unrar.Archive;
import subscene.datnt.com.unrar.exception.RarException;
import subscene.datnt.com.unrar.rarfile.FileHeader;

/**
 * Created by DatNT on 4/2/2018.
 */

public class Decompress {
    public static String TAG = "Subscene";

    public static void extractArchive(String archive, String destination) {
        if (archive == null || destination == null) {
            throw new RuntimeException("archive and destination must me set");
        }
        File arch = new File(archive);
        if (!arch.exists()) {
            throw new RuntimeException("the archive does not exit: " + archive);
        }
        File dest = new File(destination);
        if (!dest.exists() || !dest.isDirectory()) {
            throw new RuntimeException(
                    "the destination must exist and point to a directory: "
                            + destination);
        }
        extractArchive(arch, dest);
    }

    public static void main(String[] args) {
        if (args.length == 2) {
            extractArchive(args[0], args[1]);
        } else {
            System.out
                    .println("usage: java -jar extractArchive.jar <thearchive> <the destination directory>");
        }
    }

    public static void extractArchive(File archive, File destination) {
        Archive arch = null;
        try {
            arch = new Archive(archive);
        } catch (RarException e) {
            //logger.error(e);
        } catch (IOException e1) {
            //logger.error(e1);
        }
        if (arch != null) {
            if (arch.isEncrypted()) {
                //logger.warn("archive is encrypted cannot extreact");
                return;
            }
            FileHeader fh = null;
            while (true) {
                fh = arch.nextFileHeader();
                if (fh == null) {
                    break;
                }
                if (fh.isEncrypted()) {
                    Log.e(TAG, "file is encrypted cannot extract: " + fh.getFileNameString());
                    continue;
                }
                Log.e(TAG, "extracting: " + fh.getFileNameString());
                try {
                    if (fh.isDirectory()) {
                        createDirectory(fh, destination);
                    } else {
                        File f = createFile(fh, destination);
                        OutputStream stream = new FileOutputStream(f);
                        arch.extractFile(fh, stream);
                        stream.close();
                    }
                } catch (IOException e) {
                    Log.e(TAG, "error extracting the file", e);
                } catch (RarException e) {
                    Log.e(TAG, "error extraction the file", e);
                }
            }
        }
    }

    private static File createFile(FileHeader fh, File destination) {
        File f = null;
        String name = null;
        if (fh.isFileHeader() && fh.isUnicode()) {
            name = fh.getFileNameW();
        } else {
            name = fh.getFileNameString();
        }
        f = new File(destination, name);
        if (!f.exists()) {
            try {
                f = makeFile(destination, name);
            } catch (IOException e) {
                Log.e(TAG, "error creating the new file: " + f.getName(), e);
            }
        }
        return f;
    }

    private static File makeFile(File destination, String name)
            throws IOException {
        String[] dirs = name.split("\\\\");
        if (dirs == null) {
            return null;
        }
        String path = "";
        int size = dirs.length;
        if (size == 1) {
            return new File(destination, name);
        } else if (size > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                path = path + File.separator + dirs[i];
                new File(destination, path).mkdir();
            }
            path = path + File.separator + dirs[dirs.length - 1];
            File f = new File(destination, path);
            f.createNewFile();
            return f;
        } else {
            return null;
        }
    }

    private static void createDirectory(FileHeader fh, File destination) {
        File f = null;
        if (fh.isDirectory() && fh.isUnicode()) {
            f = new File(destination, fh.getFileNameW());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameW());
            }
        } else if (fh.isDirectory() && !fh.isUnicode()) {
            f = new File(destination, fh.getFileNameString());
            if (!f.exists()) {
                makeDirectory(destination, fh.getFileNameString());
            }
        }
    }

    private static void makeDirectory(File destination, String fileName) {
        String[] dirs = fileName.split("\\\\");
        if (dirs == null) {
            return;
        }
        String path = "";
        for (String dir : dirs) {
            path = path + File.separator + dir;
            new File(destination, path).mkdir();
        }

    }

    public static void unzip(String zipFile, String targetDirectory) throws IOException {
        File arch = new File(zipFile);
        if (!arch.exists()) {
            throw new RuntimeException("the archive does not exit: " + zipFile);
        }
        File dest = new File(targetDirectory);
        if (!dest.exists() || !dest.isDirectory()) {
            throw new RuntimeException(
                    "the destination must exist and point to a directory: "
                            + targetDirectory);
        }

        ZipInputStream zis = new ZipInputStream(new BufferedInputStream(new FileInputStream(arch)));
        try {
            ZipEntry ze;
            int count;
            byte[] buffer = new byte[8192];
            while ((ze = zis.getNextEntry()) != null) {
                File file = new File(dest, ze.getName());
                File dir = ze.isDirectory() ? file : file.getParentFile();
                if (!dir.isDirectory() && !dir.mkdirs())
                    throw new FileNotFoundException("Failed to ensure directory: " +
                            dir.getAbsolutePath());
                if (ze.isDirectory())
                    continue;
                FileOutputStream fout = new FileOutputStream(file);
                try {
                    while ((count = zis.read(buffer)) != -1)
                        fout.write(buffer, 0, count);
                } catch (IOException ex) {

                } finally {
                    fout.close();
                }
            /* if time should be restored as well
            long time = ze.getTime();
            if (time > 0)
                file.setLastModified(time);
            */
            }
        } catch (IOException ex) {

        } finally {
            zis.close();
        }
    }
}
