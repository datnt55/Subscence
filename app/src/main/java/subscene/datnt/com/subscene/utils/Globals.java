package subscene.datnt.com.subscene.utils;

import android.media.AudioManager;
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

    // Splash delay in milliseconds
    public static final int SPLASH_DELAY_IN_MILLISECONDS = 2000;

    // Request code to android Contacts using Intent
    public static final int PICK_CONTACT_REQUEST = 1;   // pick contact request

    // Main activity Tab positions
    public static final int TAB_POS_SPEED_DIAL = 0;
    public static final int TAB_POS_RECENTS = 1;
    public static final int TAB_POS_CONTACTS = 2;

    // Dial FAB bottom right margins
    public static final int FAB_DIAL_BOTTOM_MARGIN_IN_DP = 15;
    public static final int FAB_DIAL_RIGHT_MARGIN_IN_DP = 15;

    // Main activity FAB animation
    public static final float FAB_ANIMATION_FROMX_VALUE = 0.0f;
    public static final float FAB_ANIMATION_FROMY_VALUE = 1.6f;
    public static final float FAB_ANIMATION_TOX_VALUE = 0.0f;
    public static final float FAB_ANIMATION_TOY_VALUE = 0.0f;
    public static final int FAB_ANIMATION_DURATION_MILLISECONDS = 200;

    // Main activity Search Fragment
    public static final String TAG_REGULAR_SEARCH_FRAGMENT = "search";

    // DialPad Fragment
    public static final String TAG_DIALPAD_FRAGMENT = "dialpad";
    public static final int SYSTEM_ENABLE_TONE = 1;
    public static final int LENGTH_OF_DIGITS = 0;
    public static final int NO_SELECTION_OR_CURSOR = -1;
    public static final int FIRST_DIGIT_POSSITION = 0;

    // Define Time
    public static final int HOUR_OF_YESTERDAY = 36;
    public static final int DAY_OF_WEEK = 7;
    public static final int HOUR_OF_DAY = 24;
    public static final int MIN_OF_HOUR = 60;
    public static final int SEC_OF_MIN = 60;
    public static final int MILI_OF_SEC = 1000;

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

    // Missed call Fragment
    public static final int MIN_ARR_MISS_CALL_SIZE = 0;

    // Speed dial Fragment
    public static final int NUMBER_COLUNM_GRIDVIEW = 2;

    // The velocity for each pointer id
    public static final int VELOCITY_CURRENT = 1000;

    // Contact helper
    public static final int INCOMING_TYPE = 1;
    public static final int OUTGOING_TYPE = 2;
    public static final int MISSED_TYPE = 3;
    public static final int PHONE_TYPE_NULL = -1;
    public static final int TODAY = 0;
    public static final int YESTERDAY = 1;
    public static final int NO_CONTACT_FROM_NUMBER = -1;

    // Request permission
    public static final int REQUEST_CODE_TELEPHONE_PERMISSIONS = 10;

    // Bundle action
    public static final String INTENT_CALL_ACTION_DETAIL = "100";

    // Bundle key for saving previously selected search result item
    public static final String STATE_PREVIOUSLY_SELECTED_KEY = "com.example.android.contactslist.ui.SELECTED_ITEM";

    // Define of query contact information
    public static final String QUERY_PERSONAL_INFO_CONDITION = ContactsContract.Data.CONTACT_ID + " = ? AND " +
            ContactsContract.Data.MIMETYPE + " = ?";
    public static final String QUERY_SELECTION_ID = ContactsContract.Contacts._ID + " = ?";
    public static final String SELECTION = "%1$s <>'' AND " + ContactsContract.Contacts.IN_VISIBLE_GROUP + "=1" + " " +
            "AND " + ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1";
    public static final String STARRED_SELECTION = ContactsContract.CommonDataKinds.Phone.STARRED + "=1";

    // Using for Log utility
    public static boolean isError = false;

    // Miscellaneous defines
    public static final String TEL = "tel:";
    public static final String KEY_PUSH_EXTRA_TO_CONTACTS_CONTRACT = "finishActivityOnSaveCompleted";
    public static final String EMPTY = "";
    public static final int DEFAULT_VALUE_SEARCH_ITEM = 0;
    public static final String PATTEN_FORMAT_DATE_TIME_JP = "EEEE, MMMMdd日 yyyy, HH:mm a";
    public static final String PATTEN_FORMAT_DATE_TIME_GL = "EEEE, MMMM dd, yyyy, HH:mm a";

    public static final String PATTEN_FORMAT_RECENT_DATE_JP = "MMMMdd日 yyyy";
    public static final String PATTEN_FORMAT_RECENT_DATE_GL = "MMMM dd, yyyy";

    // Contact fragment fix margin or not
    public static final boolean ARE_CONTACT_FRAGMENT_MARGIN_FIXED = true;

    // Set alpha
    public static final float ALPHA_FULL = 1.0f;

    // Set view type for contact list
    public static final int VIEW_TYPE_HEADER = 0x01;
    public static final int VIEW_TYPE_CONTENT = 0x00;

    // Max item  can view in list
    public static final int MAXIMUM_LIST_ITEMS_VIEWABLE = 199;

    // URL setting
    public static final String MYPAGE_URL = "https://mypage.f-phone.ne.jp/user/";

    // Empty number when press call button from Dialpad
    public static final String EMPTY_NUMBER = "";

    // Key in popup menu
    public static final char PAUSE = ',';
    public static final char WAIT = ';';

    // The length of DTMF tones in milliseconds
    public static final int TONE_LENGTH_MS = 150;
    public static final int TONE_LENGTH_INFINITE = -1;

    // The DTMF tone volume relative to other sounds in the stream
    public static final int TONE_RELATIVE_VOLUME = 80;

    // Stream type used to play the DTMF tones off call, and mapped to the volume control keys
    public static final int DIAL_TONE_STREAM_TYPE = AudioManager.STREAM_DTMF;

    public static final String PREF_DIGITS_FILLED_BY_INTENT = "pref_digits_filled_by_intent";

    // Send message intent
    public static final String type = "text/plain";
    public static final String messageType = "vnd.android-dir/mms-sms";
    public static final String messageAddress = "address";

    // Permission Id
    public static final int PHONE_CONTACTS_PERMISSION = 24;
    public static final int PHONE_CALL_PERMISSION = 25;
    public static final int PHONE_STORAGE_PERMISSION = 26;
    public static final int CODE_WRITE_SETTINGS_PERMISSION = 27;

    // Contact detail defines
    public static final int PERSONAL_INFORMATION_FITTED_SCREEN_FIELD_COUNT = 4;
    public static final int PERSONAL_INFORMATION_MAX_BRIEF_ITEMS_COUNT = 3;
    public static final int PERSONAL_INFORMATION_STARRED = 1;
    public static final int PERSONAL_INFORMATION_NOT_STARRED = 0;

    // Setting ringtone
    public static final String BUNDLE_IS_RINGTONE_SETTING = "ringtone";
    public static final int DURATION = 230;
    public static final String RINGTONE_TYPE = "audio/m4a";
    public static final int RINGTONE_SIZE = 215454;
    public static final String m4aPattern = ".m4a";
    public static final String packageSetting = "package:";
    // Pattern for Contact group and sort
    public static final Pattern PATTERN_LATIN = Pattern.compile("[a-zA-Z]");
    public static final Pattern PATTERN_NUMBER = Pattern.compile("[0-9]");
    public static final HashMap<String, Pattern> PATTERN_MAP_JPKANA = new HashMap<>();

    static {
        PATTERN_MAP_JPKANA.put("あ", Pattern.compile("[ぁ-おァ-オｧ-ｫｱ-ｵ]+"));
        PATTERN_MAP_JPKANA.put("か", Pattern.compile("[か-ごカ-ゴｶ-ｺ]+"));
        PATTERN_MAP_JPKANA.put("さ", Pattern.compile("[さ-ぞサ-ゾｻ-ｿ]+"));
        PATTERN_MAP_JPKANA.put("た", Pattern.compile("[た-どタ-ドｯﾀ-ﾄ]+"));
        PATTERN_MAP_JPKANA.put("な", Pattern.compile("[な-のナ-ノﾅ-ﾉ]+"));
        PATTERN_MAP_JPKANA.put("は", Pattern.compile("[は-ぽハ-ポﾊ-ﾎ]+"));
        PATTERN_MAP_JPKANA.put("ま", Pattern.compile("[ま-もマ-モﾏ-ﾓ]+"));
        PATTERN_MAP_JPKANA.put("や", Pattern.compile("[ゃ-よャ-ヨｬ-ｮﾔ-ﾖ]+"));
        PATTERN_MAP_JPKANA.put("ら", Pattern.compile("[ら-ろラ-ロﾗ-ﾛ]+"));
        PATTERN_MAP_JPKANA.put("わ", Pattern.compile("[ゎ-んヮ-ンｦﾜ-ﾝ]+"));
    }

    public static final int CAMERA_STATE_PERMISSION = 26;
    public static final String EXTRA_IMAGE = "image";
    public static final String URL = "https://subscene.com/";
}
