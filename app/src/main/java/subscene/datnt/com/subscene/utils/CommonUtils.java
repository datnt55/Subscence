package subscene.datnt.com.subscene.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;

import java.text.NumberFormat;

import subscene.datnt.com.subscene.R;

/**
 * All miscellaneous common use utility functions defined here
 */
public class CommonUtils {

    // Convert from dpi to pixel
    public static float convertDpToPixel(float dp, Context context) {
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * (metrics.densityDpi / Globals.RATIO_PX_DP);
    }


    public static boolean isInteger(String str) {
        if (str == null) {
            return false;
        }
        int length = str.length();
        if (length == 0) {
            return false;
        }
        int i = 0;
        if (str.charAt(0) == '-') {
            if (length == 1) {
                return false;
            }
            i = 1;
        }
        for (; i < length; i++) {
            char c = str.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    public static String convertCurrency(int currency){
        NumberFormat formatter = NumberFormat.getInstance();
        String moneyString = formatter.format(currency);
        if (moneyString.endsWith(".00")) {
            int centsIndex = moneyString.lastIndexOf(".00");
            if (centsIndex != -1) {
                moneyString = moneyString.substring(0, centsIndex);
            }
        }
        return moneyString;
    }

    public static void setStatusBarBackground(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
            activity.getWindow().setStatusBarColor(Color.parseColor("#88454545"));
        }
    }

    public static void dimensionScreen(Activity activity){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        Globals.APP_SCREEN_HEIGHT = displayMetrics.heightPixels;
        Globals.APP_SCREEN_WIDTH = displayMetrics.widthPixels;
    }

    public static int getStatusBarHeight(Activity activity) {
        int result = 0;
        int resourceId = activity.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = activity.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static int getToolbarHeight(Context context) {
        final TypedArray styledAttributes = context.getTheme().obtainStyledAttributes(
                new int[]{R.attr.actionBarSize});
        int toolbarHeight = (int) styledAttributes.getDimension(0, 0);
        styledAttributes.recycle();

        return toolbarHeight;
    }

    public static void systemUiVisibility(Activity activity) {
//         This work only for android 4.4+
        final View decorView = activity.getWindow().getDecorView();
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            activity.getWindow().getDecorView().setSystemUiVisibility(flagsKitkat);

            decorView.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
                @Override
                public void onSystemUiVisibilityChange(int visibility) {
                    if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                        decorView.setSystemUiVisibility(flagsKitkat);
                    }
                }
            });
        }
    }

    public static int getVideoIcon(String name){
        if (name.toLowerCase().equals("mp4")){
            return R.drawable.mp4;
        }else if (name.toLowerCase().equals("kmv")){
            return R.drawable.kmv;
        }
        return R.drawable.file;
    }

    public static boolean isLollipopCompatible() {
        return  Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
    }

    public static final int flagsKitkat = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
}