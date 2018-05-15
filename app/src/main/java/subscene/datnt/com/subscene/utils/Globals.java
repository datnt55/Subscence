package subscene.datnt.com.subscene.utils;

import android.media.AudioManager;
import android.os.Environment;
import android.provider.ContactsContract;

import java.util.HashMap;
import java.util.regex.Pattern;

/**
 * Define all public Global Variables can be accessed any where
 */
public class Globals {

    // Main Screen Dimension, will be set when app startup
    public static int APP_SCREEN_HEIGHT = 0;
    public static int APP_SCREEN_WIDTH = 0;

    public static String APP_FOLDER = Environment.getExternalStoragePublicDirectory("Subtitle").getAbsolutePath();
    // Main activity Search Fragment
    public static final String TAG_REGULAR_SEARCH_FRAGMENT = "search";

    // Search Edit Text Layout
    public static final int TOP_MARGIN = 8;
    public static final int BOTTOM_MARGIN = 8;
    public static final int LEFT_MARGIN = 8;
    public static final int RIGHT_MARGIN = 8;

    // Rate of Screen
    public static final int WIDTH_SCREEN = 9;
    public static final int HEIGHT_SCREEN = 16;

    // Ratio switch pixel and dp
    public static final float RATIO_PX_DP = 160f;

    // The velocity for each pointer id
    public static final int VELOCITY_CURRENT = 1000;

    public static final String URL = "https://subscene.com/";
    public static final String URL_OMDB = "http://www.omdbapi.com/";
}
