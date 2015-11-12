package pcoder.net.droidwifiscan;

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pcoder on 11/10/15.
 */
public class WifiAccessPoint implements Parcelable, Comparable<WifiAccessPoint>{
    private String BSSID;
    private String SSID;
    public int level;
    private String capabilities;
    private int frequency;


    public WifiAccessPoint(ScanResult result) {
        BSSID = result.BSSID;
        SSID = result.SSID;
        capabilities = result.capabilities;
        frequency = result.frequency;
        level = result.level;
    }

    public WifiAccessPoint(Parcel in) {
        BSSID = in.readString();
        SSID = in.readString();
        capabilities = in.readString();
        frequency = in.readInt();
        level = in.readInt();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(BSSID);
        dest.writeString(SSID);
        dest.writeString(capabilities);
        dest.writeInt(frequency);
        dest.writeInt(level);
    }

    public String getSSID(){
        return SSID;
    }

    public String getBSSID(){
        return BSSID;
    }

    @Override
    public String toString() {
        return "SSID: " + SSID + " BSSID:" + BSSID + " Signal:" + level + "dBm Freq:" + frequency + "MHz Capabilities:" + capabilities;
    }

    @Override
    public int compareTo(WifiAccessPoint another) {
        return another.level - this.level;
    }
}
