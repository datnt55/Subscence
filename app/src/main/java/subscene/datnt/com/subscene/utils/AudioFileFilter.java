package subscene.datnt.com.subscene.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;

/**
 * Created by DatNT on 3/29/2018.
 */

public class AudioFileFilter implements FileFilter {

    protected static final String TAG = "AudioFileFilter";
    /**
     * allows Directories
     */
    private final boolean allowDirectories;

    public AudioFileFilter( boolean allowDirectories) {
        this.allowDirectories = allowDirectories;
    }

    public AudioFileFilter() {
        this(true);
    }

    @Override
    public boolean accept(File f) {
        if ( f.isHidden() || !f.canRead() ) {
            return false;
        }

        if ( f.isDirectory() ) {
            return checkDirectory( f );
        }
        return checkFileExtension( f );
    }

    public boolean checkFileExtension( File f ) {
        String ext = getFileExtension(f);
        if ( ext == null) return false;
        try {
            if ( SupportedFileFormat.valueOf(ext.toUpperCase()) != null ) {
                return true;
            }
        } catch(IllegalArgumentException e) {
            //Not known enum value
            return false;
        }
        return false;
    }

    private boolean checkDirectory( File dir ) {
        if ( !allowDirectories ) {
            return false;
        } else {
            final ArrayList<File> subDirs = new ArrayList<File>();
            int songNumb = dir.listFiles( new FileFilter() {

                @Override
                public boolean accept(File file) {
                    if ( file.isFile() ) {
                        if ( file.getName().equals( ".nomedia" ) )
                            return false;

                        return checkFileExtension( file );
                    } else if ( file.isDirectory() ){
                        subDirs.add( file );
                        return false;
                    } else
                        return false;
                }
            } ).length;

            if ( songNumb > 0 ) {
                return true;
            }

            for( File subDir: subDirs ) {
                if ( checkDirectory( subDir ) ) {
                    return true;
                }
            }
            return false;
        }
    }

    private boolean checkFileExtension( String fileName ) {
        String ext = getFileExtension(fileName);
        if ( ext == null) return false;
        try {
            if ( SupportedFileFormat.valueOf(ext.toUpperCase()) != null ) {
                return true;
            }
        } catch(IllegalArgumentException e) {
            //Not known enum value
            return false;
        }
        return false;
    }

    public String getFileExtension( File f ) {
        return getFileExtension( f.getName() );
    }

    public String getFileExtension( String fileName ) {
        int i = fileName.lastIndexOf('.');
        if (i > 0) {
            return fileName.substring(i+1);
        } else
            return null;
    }

    /**
     * Files formats currently supported by Library
     */
    public enum SupportedFileFormat
    {
        KMV("kmv"),
        MP4("mp4");

        private String filesuffix;

        SupportedFileFormat( String filesuffix ) {
            this.filesuffix = filesuffix;
        }

        public String getFilesuffix() {
            return filesuffix;
        }
    }

}