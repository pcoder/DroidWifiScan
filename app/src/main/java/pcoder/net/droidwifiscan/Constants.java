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
}
