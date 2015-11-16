package pcoder.net.droidwifiscan;

/**
 * Created by pcoder on 11/10/15.
 */

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A Class to represent the type of data that is going to
 * be sent by the WifiScannerService to the application activities.
 *
 * It stores basically a list of Access Points found by the wifi scanner service,
 * at this point in time.
 *
 */

public class WifiData implements Parcelable{

    /**
     * The timestamp when all the data of the APs were obtained
     */
    long timestamp;

    /**
     * A BSSID - WifiAccessPoint HashMap : to obtain data of an AP based on its BSSID
     * as and when required
     */
    HashMap<String, WifiAccessPoint> accessPointMap = new HashMap<String, WifiAccessPoint>();

    /**
     * For this project, we are only concerned with BSSID - signal strength
     */
    HashMap<String, Integer> bssid_value_map = new HashMap<String, Integer>();

    public WifiData(){
    }

    /**
     * This function is called whenever new scan results are available
     * @param results The scan results
     */

    public void addAccessPoints(List<ScanResult> results) {

        // We clear the bssid_value_map and refill it with the fresh results
        bssid_value_map.clear();
        for (ScanResult result : results) {
            WifiAccessPoint w = new WifiAccessPoint(result);
            accessPointMap.put(result.BSSID, w);
            bssid_value_map.put(result.BSSID, result.level);
        }
        timestamp = System.currentTimeMillis();
    }

    public WifiData(Parcel in) {
        timestamp = in.readLong();

        int size = in.readInt();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            Integer value = (Integer)in.readInt();
            bssid_value_map.put(key,value);
        }
    }

    public long getTimeStamp(){
        return timestamp;
    }

    public static final Parcelable.Creator<WifiAccessPoint> CREATOR = new Parcelable.Creator<WifiAccessPoint>() {
        public WifiAccessPoint createFromParcel(Parcel in) {
            return new WifiAccessPoint(in);
        }

        public WifiAccessPoint[] newArray(int size) {
            return new WifiAccessPoint[size];
        }
    };




    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        //dest.writeTypedList(accessPoints);
        dest.writeLong(timestamp);
        dest.writeInt(bssid_value_map.size());
        for(Map.Entry<String,Integer> entry : bssid_value_map.entrySet()){
            dest.writeString(entry.getKey());
            dest.writeInt((int) entry.getValue());
        }
    }

    @Override
    public String toString() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
        Date resultdate = new Date(timestamp);
        return "There are " + bssid_value_map.size() + " access points at " + sdf.format(resultdate);
    }

    /**
     * Utility function to print all access points
     * and their characteristics.
     */

    public void printAll(){
        Log.d("DroidWifiScanner", this.toString());
        int i=0;
        for(String a : getAllBSSIDs())
            Log.d("DroidWifiScanner", "     " + (++i) + ". " + bssid_value_map.get(a));
    }

    /**
     * Returns the signal value of a given bssid
     * @param bssid
     * @return the value in dbm of the signal strength of the BSSID if
     * it is found in the hashmap, else null
     */

    public Integer getGetValueForBSSID(String bssid){

        return bssid_value_map.get(bssid);
    }

    /**
     * Function to obtain all BSSIDs found in the current timestamp
     * @return Set of strings representing the BSSIDs
     */

    public Set<String> getAllBSSIDs(){
       return bssid_value_map.keySet();
    }

    /**
     * Function to obtain the SSID from a given BSSID
     * @param bssid
     */

    public String getSSIDFromBSSID(String bssid){
        WifiAccessPoint w = (WifiAccessPoint)accessPointMap.get(bssid);
        if(w== null){
            return "";
        }
        return w.getSSID();
    }
 }
