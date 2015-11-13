package pcoder.net.droidwifiscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import com.androidplot.LineRegion;
import com.androidplot.ui.AnchorPosition;
import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.ui.TextOrientationType;
import com.androidplot.ui.XLayoutStyle;
import com.androidplot.ui.YLayoutStyle;
import com.androidplot.ui.widget.TextLabelWidget;
import com.androidplot.util.PixelUtils;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;


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

    private Pair<Integer, XYSeries> selection;

    private TextLabelWidget selectionWidget;

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

        dynamicPlot.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
                    onPlotClicked(new PointF(motionEvent.getX(), motionEvent.getY()));
                }
                return true;
            }
        });


        selectionWidget = new TextLabelWidget(dynamicPlot.getLayoutManager(), Constants.NO_SELECTION_TXT,
                new SizeMetrics(
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE,
                        PixelUtils.dpToPix(100), SizeLayoutType.ABSOLUTE),
                TextOrientationType.HORIZONTAL);

        selectionWidget.getLabelPaint().setTextSize(PixelUtils.dpToPix(16));

        // add a dark, semi-transparent background to the selection label widget:
        Paint p = new Paint();
        p.setARGB(100, 0, 0, 0);
        selectionWidget.setBackgroundPaint(p);

        selectionWidget.position(
                0, XLayoutStyle.RELATIVE_TO_CENTER,
                PixelUtils.dpToPix(45), YLayoutStyle.ABSOLUTE_FROM_TOP,
                AnchorPosition.TOP_MIDDLE);
        selectionWidget.pack();

    }

    private void onPlotClicked(PointF point) {
        // make sure the point lies within the graph area.  we use gridrect
        // because it accounts for margins and padding as well. 
        if (dynamicPlot.getGraphWidget().getGridRect().contains(point.x, point.y)) {
            Number x = dynamicPlot.getXVal(point);
            Number y = dynamicPlot.getYVal(point);


            selection = null;
            double xDistance = 0;
            double yDistance = 0;

            // find the closest value to the selection:
            for (XYSeries series : dynamicPlot.getSeriesSet()) {
                for (int i = 0; i < series.size(); i++) {
                    Number thisX = series.getX(i);
                    Number thisY = series.getY(i);
                    if (thisX != null && thisY != null) {
                        double thisXDistance =
                                LineRegion.measure(x, thisX).doubleValue();
                        double thisYDistance =
                                LineRegion.measure(y, thisY).doubleValue();
                        if (selection == null) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance < xDistance) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        } else if (thisXDistance == xDistance &&
                                thisYDistance < yDistance &&
                                thisY.doubleValue() >= y.doubleValue()) {
                            selection = new Pair<Integer, XYSeries>(i, series);
                            xDistance = thisXDistance;
                            yDistance = thisYDistance;
                        }
                    }
                }
            }

        } else {
            // if the press was outside the graph area, deselect:
            selection = null;
        }

        if(selection == null) {
            selectionWidget.setText(Constants.NO_SELECTION_TXT);
        } else {
            selectionWidget.setText("Selected: " + selection.second.getTitle() +
                    " Value: " + selection.second.getY(selection.first));
        }
        dynamicPlot.redraw();
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
                        xySeries =  new SimpleXYSeries(wifiData.getSSIDFromBSSID(bssid));
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
