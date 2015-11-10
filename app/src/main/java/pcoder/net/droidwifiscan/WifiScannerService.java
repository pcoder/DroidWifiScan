package pcoder.net.droidwifiscan;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class WifiScannerService extends Service {

    private WifiManager wifiManager;
    private ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;
    private ScheduledFuture<?> scheduledFutureAction;
    private ScheduledExecutorService scheduledExecutorService;

    private int initialDelay = 500;
    private int period = 5000;

    public WifiScannerService() {

    }

    @Override
    public void onCreate(){
        // Get the android's wifi manager which will be used in the code
        // to scan the access points
        wifiManager = (WifiManager) this.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        //wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);

        // Create an executor service with a single thread that scans
        // and updates the access points and their signal strengths
        scheduledExecutorService = Executors.newScheduledThreadPool(1);
        scheduledFutureAction = scheduledExecutorService.scheduleAtFixedRate(new ScanThread(), initialDelay, period,
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
                Log.d("WifiScanThread", "Found " + scanResults.size() + " networks.");
                int i=0;
                for(ScanResult s : scanResults){
                    Log.d("WifiScanThread", "     " + (++i) + ". " + s.SSID + " " + s.level + " " + s.capabilities);
                }
            }
        }
    }
}