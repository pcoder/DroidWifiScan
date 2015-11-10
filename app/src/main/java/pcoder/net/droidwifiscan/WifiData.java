package pcoder.net.droidwifiscan;

/**
 * Created by pcoder on 11/10/15.
 */

import android.net.wifi.ScanResult;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
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

    public WifiData(){
        accessPoints = new ArrayList<WifiAccessPoint>();
    }

    public void addAccessPoints(List<ScanResult> results) {
        accessPoints.clear();
        for (ScanResult result : results) {
            accessPoints.add(new WifiAccessPoint(result));
        }
        Collections.sort(accessPoints);
    }


    public WifiData(Parcel in) {
        in.readTypedList(accessPoints, WifiData.CREATOR);
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
    }
}
