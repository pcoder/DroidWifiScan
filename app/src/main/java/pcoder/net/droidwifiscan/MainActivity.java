package pcoder.net.droidwifiscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;


public class MainActivity extends ActionBarActivity {

    private WifiData wifiData;
    private XYPlot dynamicPlot;
    private HashMap<String, OurPlotData> mapPlotData;
    private int xstep;
    int color_counter = 0;
    int startx = 0;
    String[] colorValues = new String[] {
            "FF0000", "00FF00", "0000FF", "FFFF00", "FF00FF", "00FFFF", "000000",
            "800000", "008000", "000080", "808000", "800080", "008080", "808080",
            "C00000", "00C000", "0000C0", "C0C000", "C000C0", "00C0C0", "C0C0C0",
            "400000", "004000", "000040", "404000", "400040", "004040", "404040",
            "200000", "002000", "000020", "202000", "200020", "002020", "202020",
            "600000", "006000", "000060", "606000", "600060", "006060", "606060",
            "A00000", "00A000", "0000A0", "A0A000", "A000A0", "00A0A0", "A0A0A0",
            "E00000", "00E000", "0000E0", "E0E000", "E000E0", "00E0E0", "E0E0E0",
    };


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        xstep = 0;
        wifiData = null;
        mapPlotData = new HashMap<String, OurPlotData>();
        // set receiver
        WifiDataReceiver mReceiver = new WifiDataReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("DROID_WIFI_SCANNER"));

        // start the WifiScannerService
        Intent intent = new Intent(this, WifiScannerService.class);
        startService(intent);


        setContentView(R.layout.simple_xy_layout);

        dynamicPlot = (XYPlot) findViewById(R.id.dynamicXYPlot);

        dynamicPlot.setRangeBoundaries(-100, -30, BoundaryMode.FIXED);
        dynamicPlot.setDomainBoundaries(0, Constants.HISTORY_SIZE, BoundaryMode.FIXED);

        dynamicPlot.setDomainStepValue(Constants.SCAN_FREQUENCY);
        dynamicPlot.setTicksPerRangeLabel(3);
        dynamicPlot.setDomainLabel("Time");
        dynamicPlot.getDomainLabelWidget().pack();
        dynamicPlot.setRangeLabel("Level");
        dynamicPlot.getRangeLabelWidget().pack();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }

    /**
     * A Broadcast receiver to listen to the data about the access points sent
     * by the WifiScannerService.
     *
     */
    public class WifiDataReceiver extends BroadcastReceiver {


        @Override
        public void onReceive(Context context, Intent intent) {
            WifiData data = (WifiData) intent.getParcelableExtra(Constants.WIFI_DATA);

            if (data != null) {
                wifiData = data;

                // TODO use this data to plot the graph
                // and save using the REST API

                OurPlotData opd = null;
                SimpleXYSeries xySeries = null;
                List<Number> dataList = null;
                int signalValue= 0;
                Set<String> allBSSIDS = wifiData.getAllBSSIDs();
                dynamicPlot.setTitle("Wifi Signal Plot (" + allBSSIDS.size() + " aps)");
                for(String bssid : allBSSIDS) {

                    signalValue = wifiData.getGetValueForBSSID(bssid);

                    if ((opd = mapPlotData.get(bssid)) != null){
                        xySeries = opd.getDataSeries();
                        dataList = opd.getDataList();

                        dataList.add(xstep * 5);
                        dataList.add(signalValue);
                    }else{
                        // create OurPlotData and store it for future references
                        dataList = new ArrayList();
                        dataList.add(xstep * 5);
                        dataList.add(signalValue);
                        xySeries =  new SimpleXYSeries("t");
                        int c = (int)Long.parseLong("FF" + colorValues[(color_counter++)%56], 16);
                        dynamicPlot.addSeries(xySeries, new LineAndPointFormatter(c, c, Color.TRANSPARENT, null));
                        mapPlotData.put(bssid, new OurPlotData(xySeries, dataList));
                    }

                    xySeries.setModel(dataList, SimpleXYSeries.ArrayFormat.XY_VALS_INTERLEAVED);
                    if (dataList.size() * 2 > Constants.HISTORY_SIZE) {
                        xySeries.removeFirst();
                        dataList.remove(0);
                        dataList.remove(0);
                    }

                    xySeries.addLast(xstep * 5, signalValue);
                }
                if((xstep * 5) >= Constants.HISTORY_SIZE){
                    startx+=5;
                    dynamicPlot.setDomainBoundaries(startx, Constants.HISTORY_SIZE + startx, BoundaryMode.FIXED);
                }
                xstep++;
                dynamicPlot.redraw();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d("test", "App stopped");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d("test", "App destoryed");

        super.onDestroy();
    }


    class OurPlotData {
        private SimpleXYSeries dataSeries;
        private List<Number> dataList;

        public OurPlotData(SimpleXYSeries s, List<Number> l){
            this.dataSeries = s;
            this.dataList = l;
        }

        public List<Number> getDataList(){
            return dataList;
        }

        public SimpleXYSeries getDataSeries(){
            return dataSeries;
        }
    }
}
