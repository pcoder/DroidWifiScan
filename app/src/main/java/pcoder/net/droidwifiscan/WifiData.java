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

    List<WifiAccessPoint> accessPoints;
    long timestamp;
    int counter;
    HashMap<String, WifiAccessPoint> accessPointMap = new HashMap<String, WifiAccessPoint>();
    HashMap<String, Integer> bssid_value_map = new HashMap<String, Integer>();

    public WifiData(){
        accessPoints = new ArrayList<WifiAccessPoint>();
        counter = 0;
    }

    public void addAccessPoints(List<ScanResult> results) {
        accessPoints.clear();
        for (ScanResult result : results) {
            WifiAccessPoint w = new WifiAccessPoint(result);
            accessPoints.add(w);
            accessPointMap.put(result.BSSID, w);
            bssid_value_map.put(result.BSSID, result.level);
        }
        timestamp = System.currentTimeMillis();
        Collections.sort(accessPoints);
    }

    public WifiData(Parcel in) {
        in.readTypedList(accessPoints, WifiData.CREATOR);
        timestamp = in.readLong();

        int size = in.readInt();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            Integer value = (Integer)in.readInt();
            bssid_value_map.put(key,value);
        }
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
        dest.writeTypedList(accessPoints);
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
        return "There are " + accessPoints.size() + " access points at " + sdf.format(resultdate);
    }

    public void printAll(){
        Log.d("DroidWifiScanner", this.toString());
        int i=0;
        for(WifiAccessPoint a : accessPoints)
            Log.d("DroidWifiScanner", "     " + (++i) + ". " + a.toString());
    }

    public Number[] getSignalsAsArray(){
        /*Number []ret = new Number[1];
        int i=0;
        for(WifiAccessPoint a : accessPoints)
            if(a.getSSID().equals("Livebox-A172")){
                ret[0] = a.level;
                return ret;
            }

        return null;*/
        Number []ret = new Number[accessPoints.size()];
        int i=0;
        for(WifiAccessPoint a : accessPoints)
            ret[i++] = a.level;
        return ret;
    }

    /*public HashMap getSignalsAsHash(){
        Number []ret = new Number[1];
        int i=0;
        for(WifiAccessPoint a : accessPoints)
            if(a.getSSID().equals("Livebox-A172")){
                values.put(counter + "-" +a.getBSSID(), a.level);
                counter++;
                return values;
            }

        return null;

    }*/

    public Integer getGetValueForBSSID(String bssid){

        return bssid_value_map.get(bssid);
    }

    public Set<String> getAllBSSIDs(){
       return bssid_value_map.keySet();
    }
 }
