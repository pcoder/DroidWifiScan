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
import java.util.List;

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

    public WifiData(){
        accessPoints = new ArrayList<WifiAccessPoint>();
    }

    public void addAccessPoints(List<ScanResult> results) {
        accessPoints.clear();
        for (ScanResult result : results) {
            accessPoints.add(new WifiAccessPoint(result));
        }
        timestamp = System.currentTimeMillis();
        Collections.sort(accessPoints);
    }


    public WifiData(Parcel in) {
        in.readTypedList(accessPoints, WifiData.CREATOR);
        timestamp = in.readLong();
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
}
