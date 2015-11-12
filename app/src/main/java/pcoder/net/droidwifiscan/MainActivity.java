package pcoder.net.droidwifiscan;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.SimpleTimeZone;

import com.androidplot.Plot;
import com.androidplot.util.PixelUtils;
import com.androidplot.util.PlotStatistics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;


public class MainActivity extends ActionBarActivity {

    private WifiData wifiData;
    private XYPlot plot;
    private XYPlot dynamicPlot;
    SampleDynamicXYDatasource data;
    private Thread myThread;
    private SimpleXYSeries wifiDataSeries = null;
    private List allDataSeries = null;
    private List dataList = null;
    private List<List<ArrayList>> allDataList = null;


    public MainActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        wifiData = null;
        dataList = new ArrayList();
        allDataSeries = new ArrayList();
        allDataList = new ArrayList<List<ArrayList>>();
        // set receiver
        WifiDataReceiver mReceiver = new WifiDataReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, new IntentFilter("DROID_WIFI_SCANNER"));

        /*setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }*/

        // start the WifiScannerService
        Intent intent = new Intent(this, WifiScannerService.class);
        startService(intent);


        setContentView(R.layout.simple_xy_layout);

        dynamicPlot = (XYPlot) findViewById(R.id.dynamicXYPlot);

        wifiDataSeries = new SimpleXYSeries("Wifi Signal Strength");
        wifiDataSeries.useImplicitXVals();

        dynamicPlot.setRangeBoundaries(-100, -30, BoundaryMode.FIXED);
        dynamicPlot.setDomainBoundaries(0, 30, BoundaryMode.FIXED);

        dynamicPlot.addSeries(wifiDataSeries, new LineAndPointFormatter( Color.RED,Color.GREEN, Color.argb(255,0,0,0), null));
        dynamicPlot.setDomainStepValue(5);
        dynamicPlot.setTicksPerRangeLabel(3);
        dynamicPlot.setDomainLabel("Time");
        dynamicPlot.getDomainLabelWidget().pack();
        dynamicPlot.setRangeLabel("Level");
        dynamicPlot.getRangeLabelWidget().pack();

        //final PlotStatistics wifiStats = new PlotStatistics(1000, true);
        //dynamicPlot.addListener(wifiStats);
        /*

        // get handles to our View defined in layout.xml:
        dynamicPlot = (XYPlot) findViewById(R.id.dynamicXYPlot);

        plotUpdater = new MyPlotUpdater(dynamicPlot);

        // only display whole numbers in domain labels
        dynamicPlot.getGraphWidget().setDomainValueFormat(new DecimalFormat("0"));

        // getInstance and position datasets:
        data = new SampleDynamicXYDatasource();
        SampleDynamicSeries sine1Series = new SampleDynamicSeries(data, 0, "Sine 1");
        SampleDynamicSeries sine2Series = new SampleDynamicSeries(data, 1, "Sine 2");

        LineAndPointFormatter formatter1 = new LineAndPointFormatter(
                Color.rgb(0, 0, 0), null, null, null);
        formatter1.getLinePaint().setStrokeJoin(Paint.Join.ROUND);
        formatter1.getLinePaint().setStrokeWidth(10);
        dynamicPlot.addSeries(sine1Series,
                formatter1);

        LineAndPointFormatter formatter2 =
                new LineAndPointFormatter(Color.rgb(0, 0, 200), null, null, null);
        formatter2.getLinePaint().setStrokeWidth(10);
        formatter2.getLinePaint().setStrokeJoin(Paint.Join.ROUND);

        //formatter2.getFillPaint().setAlpha(220);
        dynamicPlot.addSeries(sine2Series, formatter2);

        // hook up the plotUpdater to the data model:
        data.addObserver(plotUpdater);

        // thin out domain tick labels so they dont overlap each other:
        dynamicPlot.setDomainStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setDomainStepValue(5);

        dynamicPlot.setRangeStepMode(XYStepMode.INCREMENT_BY_VAL);
        dynamicPlot.setRangeStepValue(10);

        dynamicPlot.setRangeValueFormat(new DecimalFormat("###.#"));

        // uncomment this line to freeze the range boundaries:
        dynamicPlot.setRangeBoundaries(-100, 100, BoundaryMode.FIXED);

        // create a dash effect for domain and range grid lines:
        DashPathEffect dashFx = new DashPathEffect(
                new float[] {PixelUtils.dpToPix(3), PixelUtils.dpToPix(3)}, 0);
        dynamicPlot.getGraphWidget().getDomainGridLinePaint().setPathEffect(dashFx);
        dynamicPlot.getGraphWidget().getRangeGridLinePaint().setPathEffect(dashFx);
*/

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
        private static final int HISTORY_SIZE = 30;

        @Override
        public void onReceive(Context context, Intent intent) {
            WifiData data = (WifiData) intent.getParcelableExtra(Constants.WIFI_DATA);

            if (data != null) {
                wifiData = data;

                // TODO use this data to plot the graph
                // and save using the REST API
                wifiData.printAll();

                // update instantaneous data:
                Number[] series1Numbers = wifiData.getSignalsAsArray();

                for(int i=0; i<series1Numbers.length; i++){
                    ArrayList datal;
                    SimpleXYSeries wifiDataSeriesl;
                    if(allDataList.size() <= i ){
                        datal = new ArrayList();
                        datal.add(series1Numbers[i]);
                        allDataList.add(datal);
                        wifiDataSeriesl =  new SimpleXYSeries("" + i);
                        wifiDataSeriesl.useImplicitXVals();
                        dynamicPlot.addSeries(wifiDataSeriesl, new LineAndPointFormatter(Color.RED, Color.GREEN, Color.argb(0,0,0,0), null));
                        allDataSeries.add(wifiDataSeriesl);
                    }else{
                        datal = (ArrayList)allDataList.get(i);
                        datal.add(series1Numbers[i]);
                        wifiDataSeriesl = (SimpleXYSeries)allDataSeries.get(i);
                    }

                    wifiDataSeriesl.setModel(datal, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

                    if (datal.size() > HISTORY_SIZE) {
                        wifiDataSeriesl.removeFirst();
                        datal.remove(0);
                    }

                    wifiDataSeriesl.addLast(null, series1Numbers[i]);
                }
                dynamicPlot.redraw();


                /*

                //for (int i = 0; i<hm.size(); i++)
                {
                    ArrayList a = new ArrayList();
                    a.add(series1Numbers[0]);
                    wifiDataSeries.setModel(dataList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

                    wifiDataSeries = new SimpleXYSeries("" +i);
                    wifiDataSeries.useImplicitXVals();
                    dynamicPlot.addSeries(wifiDataSeries, new LineAndPointFormatter((int)(((i+1) * Math.random())%255), (int)(((i+1) * Math.random())%255), Color.argb(255,0,0,0), null));
                    allDataSeries.add(wifiDataSeries);
                }



                //dataList.add(series1Numbers[0]);
                Log.d("test", "Value = " +series1Numbers[0] );
                dataList.add(series1Numbers[0]);
                wifiDataSeries.setModel(dataList, SimpleXYSeries.ArrayFormat.Y_VALS_ONLY);

                // get rid the oldest sample in history:
                if (dataList.size() > HISTORY_SIZE) {
                    wifiDataSeries.removeFirst();
                    dataList.remove(0);
                }

                // add the latest history sample:
                wifiDataSeries.addLast(null, series1Numbers[0]);

                // redraw the Plots:
                dynamicPlot.redraw();*/
            }
        }
    }

    @Override
    public void onResume() {
        // kick off the data generating thread:
        myThread = new Thread(data);
        myThread.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        if(data != null) {
            data.stopThread();
            super.onPause();
        }else{
            Log.d("test", "There is an error.");
        }

    }


    class OurPlotData {
        private SimpleXYSeries dataSeries;
        private List<Number> dataList;

        
    }


    class SampleDynamicXYDatasource implements Runnable {

        // encapsulates management of the observers watching this datasource for update events:
        class MyObservable extends Observable {
            @Override
            public void notifyObservers() {
                setChanged();
                super.notifyObservers();
            }
        }

        private static final double FREQUENCY = 5; // larger is lower frequency
        private static final int MAX_AMP_SEED = 100;
        private static final int MIN_AMP_SEED = 10;
        private static final int AMP_STEP = 1;
        public static final int SINE1 = 0;
        public static final int SINE2 = 1;
        private static final int SAMPLE_SIZE = 30;
        private int phase = 0;
        private int sinAmp = 1;
        private MyObservable notifier;
        private boolean keepRunning = false;

        {
            notifier = new MyObservable();
        }

        public void stopThread() {
            keepRunning = false;
        }

        //@Override
        public void run() {
            try {
                keepRunning = true;
                boolean isRising = true;
                while (keepRunning) {

                    Thread.sleep(10); // decrease or remove to speed up the refresh rate.
                    phase++;
                    if (sinAmp >= MAX_AMP_SEED) {
                        isRising = false;
                    } else if (sinAmp <= MIN_AMP_SEED) {
                        isRising = true;
                    }

                    if (isRising) {
                        sinAmp += AMP_STEP;
                    } else {
                        sinAmp -= AMP_STEP;
                    }
                    notifier.notifyObservers();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        public int getItemCount(int series) {
            return SAMPLE_SIZE;
        }

        public Number getX(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            return index;
        }

        public Number getY(int series, int index) {
            if (index >= SAMPLE_SIZE) {
                throw new IllegalArgumentException();
            }
            double angle = (index + (phase))/FREQUENCY;
            double amp = sinAmp * Math.sin(angle);
            switch (series) {
                case SINE1:
                    return amp;
                case SINE2:
                    return -amp;
                default:
                    throw new IllegalArgumentException();
            }
        }

        public void addObserver(Observer observer) {
            notifier.addObserver(observer);
        }

        public void removeObserver(Observer observer) {
            notifier.deleteObserver(observer);
        }

    }

    class SampleDynamicSeries implements XYSeries {
        private SampleDynamicXYDatasource datasource;
        private int seriesIndex;
        private String title;

        public SampleDynamicSeries(SampleDynamicXYDatasource datasource, int seriesIndex, String title) {
            this.datasource = datasource;
            this.seriesIndex = seriesIndex;
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int size() {
            return datasource.getItemCount(seriesIndex);
        }

        @Override
        public Number getX(int index) {
            return datasource.getX(seriesIndex, index);
        }

        @Override
        public Number getY(int index) {
            return datasource.getY(seriesIndex, index);
        }
    }

}
