package pcoder.net.droidwifiscan;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class WifiScannerService extends Service {

    /**
     * instance of the android WifiManager class for access to scan results
     */
    private WifiManager wifiManager;

    /**
     * A variable to schedule the future scanning tasks
     */
    private ScheduledFuture<?> scheduledFutureAction;

    /**
     * The executor service that schedules the future tasks
     */
    private ScheduledExecutorService scheduledExecutorService;

    /**
     * The variable that is filled with the scan results. This is sent
     * to the activities that listen to this service.
     */
    private WifiData wifiData;

    /**
     * Initial delay before starting the scan service
     */
    private int initialDelay = 500;

    public WifiScannerService() {

    }

    @Override
    public void onCreate(){

        wifiData = new WifiData();
        // Get the android's wifi manager which will be used in the code
        // to scan the access points
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        // Create an executor service with a single thread that scans
        // and updates the access points and their signal strengths
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledFutureAction = scheduledExecutorService.scheduleAtFixedRate(new ScanThread(), initialDelay, Constants.SCAN_FREQUENCY * 1000,
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void onDestroy(){
        scheduledFutureAction.cancel(true);
        scheduledExecutorService.shutdown();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     *  A thread that is responsible for scanning
     *  at regular intervals (5 seconds, say)
     */

    class ScanThread implements Runnable{
        @Override
        public void run() {
            if (wifiManager.isWifiEnabled()) {
                // get the scan results of the available networks
                List<ScanResult> scanResults = wifiManager.getScanResults();

                // save the scanResults
                wifiData.addAccessPoints(scanResults);

                // send data to MyActivity
                Intent intent = new Intent("DROID_WIFI_SCANNER");
                intent.putExtra(Constants.WIFI_DATA, wifiData);
                LocalBroadcastManager.getInstance(WifiScannerService.this).sendBroadcast(intent);
            }else{
                Log.d("WifiScanThread", "Wifi seems to be deactivated. Please activate it to see the results.");
            }
        }
    }
}
