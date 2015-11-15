package pcoder.net.droidwifiscan;

/**
 * Created by lpkc4220 on 11/10/15.
 */
public class Constants {
    public static String WIFI_DATA = "WIFI_DATA";

    /**
     * A static variable to specify how frequent we need to scan the wifi signals
     * Time in seconds
     */
    public static int SCAN_FREQUENCY = 5;

    /**
     * This is the size of history (in seconds) that is kept in the plot.
     * Preferred history size is a multiple of SCAN_FREQUENCY in seconds.
     */
    public static final int HISTORY_SIZE = 90;


    public static final String NO_SELECTION_TXT = "Please select a plot.";

    public static String POST_URL = "http://192.168.0.11:8080/signals/bob";

    public static final String SERVER_URL_KEY = "pcoder.net.droidwifiscan.server_url";

    public static final String APP_KEY_KEY = "pcoder.net.droidwifiscan.app_key";

    public static final String DEFAULT_NS = "pcoder.net.droidwifiscan";

    /**
     * Do not allow more than MAX_FILES to be saved on the device
     */
    public static final int MAX_FILES = 500;


}
