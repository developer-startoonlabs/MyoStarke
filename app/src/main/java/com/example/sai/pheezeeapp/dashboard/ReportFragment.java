package com.example.sai.pheezeeapp.dashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sai.pheezeeapp.R;
import com.example.sai.pheezeeapp.services.MqttHelper;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.BubbleChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.warkiz.widget.IndicatorSeekBar;
import com.warkiz.widget.OnSeekChangeListener;
import com.warkiz.widget.SeekParams;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Objects;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class ReportFragment extends Fragment {

    private MqttAndroidClient client;

    BubbleChart bubbleChart;
    String patientId,phizioemail;
    ProgressBar graphLoader;
    BarChart barChart,barChartReps;
    BarDataSet barChartDataSet,barChartRepsDataSet;
    JSONObject json_phizio;
    BarData barChartData,barChartRepsData;
    SharedPreferences preferences;
    ArrayList<BarEntry> dataPoints,repsDataPoints;

    JSONArray array;
    MqttHelper mqttHelper;
    String mqtt_publish_getpatientReport = "patient/generate/report";
    String mqtt_publish_getpatientReport_response = "patient/generate/report/response";
    SwipeRefreshLayout swipeRefreshLayout;
    BarGraphSeries<DataPoint> series;
    Context context;
    IndicatorSeekBar barChartScroller;
    View view;
    PopupWindow report;
    JSONArray summary;
    private static final String ARG_COLUMN_COUNT = "column-count";

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ReportFragment() {
    }

    @SuppressWarnings("unused")
    public static ReportFragment newInstance(int columnCount) {
        ReportFragment fragment = new ReportFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mqtt_publish_getpatientReport_response = "patient/generate/report/response";


        view = inflater.inflate(R.layout.fragment_report, container, false);
        context = getContext();
        graphLoader = view.findViewById(R.id.graphLoader);
        barChart = view.findViewById(R.id.reportEmgGraph);
        barChartReps = view.findViewById(R.id.reportRepsGraph);
        swipeRefreshLayout = view.findViewById(R.id.swiperefresh);
        barChartScroller = view.findViewById(R.id.barChartScroller);
        patientId = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("patientId");
        phizioemail = Objects.requireNonNull(getActivity()).getIntent().getStringExtra("phizioemail");
        barChartScroller.setMax(0);
        dataPoints = new ArrayList<>();
        repsDataPoints=new ArrayList<>();
        mqttHelper = new MqttHelper(getActivity());
        barChartDataSet=new BarDataSet(dataPoints, "Sessions Vs Emg Graph");
        barChartRepsDataSet  = new BarDataSet(repsDataPoints,"Sessions vs Reps Graph");
        preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        //barChartDataSet.setValueTextSize(0);
        //barChartDataSet.setDrawValues(false);
        barChartDataSet.setColor(Color.parseColor("#4b7080"));
        barChartData = new BarData(barChartDataSet);
        barChartData.setBarWidth(0.1f);




        barChartRepsDataSet.setColor(Color.parseColor("#4b7080"));
        barChartRepsData = new BarData(barChartRepsDataSet);
        barChartRepsData.setBarWidth(0.1f);
        //barChartDataSet.setFillAlpha(80);
        //BarChart.setVisibleXRangeMaximum(1000);
        //BarChart.getXAxis().setAxisMinimum(0f);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            swipeRefreshLayout.setTooltipText("swipe down to refresh");
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
                try {

                    MqttMessage message = new MqttMessage();
                    JSONObject object = new JSONObject();
                    object.put("patientid",patientId);
                    object.put("phizioemail",phizioemail);
                    message.setPayload(object.toString().getBytes());
                    if(mqttHelper!=null)
                        mqttHelper.publishMqttTopic(mqtt_publish_getpatientReport,message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


        setEmgChartCharacteristics();
        setRepsChartCharacteristics();

        ;
        /*barChart.getXAxis().setAvoidFirstLastClipping(true);
//        barChart.getAxisLeft().setDrawGridLines(false);
        barChart.getXAxis().setDrawGridLines(false);
        barChart.setTouchEnabled(true);
        barChart.setHorizontalScrollBarEnabled(true);
        barChart.setScrollBarSize(100);
        barChart.setScrollContainer(true);
        barChart.setScrollBarFadeDuration(100000);
        barChart.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        barChart.animateXY(3000, 3000);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getDescription().setEnabled(false);
        barChart.enableScroll();
        barChart.getXAxis().setAxisMinimum(0f);*/
        barChart.setData(barChartData);
        barChartReps.setData(barChartRepsData);

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener()
        {
            @Override
            public void onValueSelected(Entry e, Highlight h)
            {
                float x=e.getX();
                try {
                    initiatePopupWindow((int) x-1,view);
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }


            }

            @Override
            public void onNothingSelected()
            {

            }
        });
        barChartScroller.setOnSeekChangeListener(new OnSeekChangeListener() {
            @Override
            public void onSeeking(SeekParams seekParams) {
                barChart.moveViewToX(seekParams.progress);
            }

            @Override
            public void onStartTrackingTouch(IndicatorSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(IndicatorSeekBar seekBar) {

            }

        });
        final DataPoint[] datapoints=new DataPoint[]{};
        series = new BarGraphSeries<>(datapoints);


        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean reconnect, String serverURI) {

            }

            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                Log.i("MESSAGE",topic);
                if(topic.equals(mqtt_publish_getpatientReport_response)){
                    Log.i("message","Recieved");
                    graphLoader.setVisibility(View.GONE);
                    swipeRefreshLayout.setRefreshing(false);
                    barChartDataSet=new BarDataSet(dataPoints, "Sessions Vs EMG Graph");
                    array = new JSONArray(message.toString());
                    int j=0;
                    for (int i=0;i<array.length();i++){
                        String maxemg = array.getJSONObject(i).get("maxemg").toString();
                        //maxemg = maxemg.substring(0,maxemg.length());
                        barChartData.addEntry(new BarEntry(++j,Integer.parseInt(maxemg)),0);
                        barChart.moveViewToX(j);
                        barChartScroller.setDecimalScale(j);
                    }

                    barChart.notifyDataSetChanged();
                    barChart.invalidate();
                    /*if(summary.length()>5) {
                        barChart.setVisibleXRangeMaximum(6f);
                        barChart.setVisibleXRangeMinimum(3f);
                    }
                    barChartData.setBarWidth(1f);

                    barChart.getXAxis().setDrawGridLines(false);
                    barChart.setTouchEnabled(true);
                    barChart.setHorizontalScrollBarEnabled(true);

                    barChart.setScrollBarSize(1000);
                    barChart.setScrollContainer(true);
                    barChart.setScrollBarFadeDuration(100000);
                    barChart.setScrollbarFadingEnabled(true);
                    barChart.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
                    barChart.animateXY(3000, 3000);
                    barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                    barChart.getDescription().setEnabled(false);
                    barChart.enableScroll();
                    barChart.setScaleEnabled(false);
                    //Toast.makeText(context,Integer.toString(j),Toast.LENGTH_SHORT).show();
                    barChartScroller.setMax(summary.length());*/

                    setEmgChartCharacteristics();

                    barChartRepsDataSet=new BarDataSet(repsDataPoints, "Sessions Vs Reps Graph");
                    int k=0;
                    for (int i=0;i<array.length();i++){
                        String maxemg = array.getJSONObject(i).get("numofreps").toString();
                        //maxemg = maxemg.substring(0,maxemg.length());
                        barChartRepsData.addEntry(new BarEntry(++k,Integer.parseInt(maxemg)),0);
                        /*barChartReps.moveViewToX(j);
                        barChartScroller.setDecimalScale(j);*/
                    }

                    barChartReps.notifyDataSetChanged();
                    barChartReps.invalidate();

                    setRepsChartCharacteristics();

                }


            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
        try {
//            if(client!=null)
//                client.publish("newapk/getreport", patientId.getBytes(), 0, false);

            MqttMessage message = new MqttMessage();
            JSONObject object = new JSONObject();
            object.put("patientid",patientId);
            object.put("phizioemail",phizioemail);
            message.setPayload(object.toString().getBytes());
            Log.i("message object",message.toString()+mqtt_publish_getpatientReport);
            mqttHelper=new MqttHelper(getActivity());
            if(mqttHelper!=null)
                mqttHelper.publishMqttTopic(mqtt_publish_getpatientReport,message);

        } catch (JSONException e) {
            e.printStackTrace();
        }


        return view;
    }

    private void setRepsChartCharacteristics() {
        barChartReps.setDrawGridBackground(true);
        barChartReps.setDrawBarShadow(true);
        barChartRepsData.setBarWidth(0.9f);

        //mChart.setHighlightFullBarEnabled(false);
        barChartReps.setPinchZoom(true);
        barChartReps.animateXY(1500, 2000);
        barChartReps.setHorizontalScrollBarEnabled(true);

        Legend l = barChartReps.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        YAxis rightAxis = barChartReps.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = barChartReps.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        //XAxis
        XAxis xAxis = barChartReps.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);

    }

    private void setEmgChartCharacteristics() {
        barChart.setDrawGridBackground(true);
        barChart.setDrawBarShadow(true);
        barChartData.setBarWidth(0.9f);

        //mChart.setHighlightFullBarEnabled(false);
        barChart.setPinchZoom(true);

        barChart.animateXY(1500, 2000);
        barChart.setHorizontalScrollBarEnabled(true);

        Legend l = barChart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);

        YAxis rightAxis = barChart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        YAxis leftAxis = barChart.getAxisLeft();
        leftAxis.setDrawGridLines(false);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)


        //XAxis
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setAxisMinimum(0f);
        xAxis.setGranularity(1f);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
//            if(client!=null)
//                client.publish("newapk/getreport", patientId.getBytes(), 0, false);

            MqttMessage message = new MqttMessage();
            JSONObject object = new JSONObject();
            object.put("patientid",patientId);
            object.put("phizioemail",phizioemail);
            message.setPayload(object.toString().getBytes());
            if(mqttHelper!=null)
                mqttHelper.publishMqttTopic(mqtt_publish_getpatientReport,message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (!(context instanceof OnListFragmentInteractionListener)){
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        if (client!=null){
            client.unregisterResources();
            client.close();
        }
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    interface OnListFragmentInteractionListener {
    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(networkReceiver);
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkReceiver, filter);
    }

    public void startMqtt(){
        final String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(context, "tcp://52.66.113.37:1883", clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                static final String TAG = "Mqtt Message";

                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Log.d(TAG, "onSuccess");
                    try {
                        client.subscribe("newapk/report/"+patientId, 0, null, new IMqttActionListener() {
                            @Override
                            public void onSuccess(IMqttToken asyncActionToken) {
//                                try {
//                                    client.publish("newapk/getreport",patientId.getBytes(),0,false);
//                                } catch (MqttException e) {
//                                    e.printStackTrace();
//                                }
                            }

                            @Override
                            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                                Toast.makeText(getActivity(),"Your request is failed to initiate",Toast.LENGTH_LONG).show();
                            }
                        });
                    } catch (MqttException e) {
                        e.printStackTrace();
                    }

                    client.setCallback(new MqttCallback() {
                        public void messageArrived(String topic, MqttMessage message) throws JSONException {
                            if(topic.equals("newapk/report/"+patientId)){
                                graphLoader.setVisibility(View.GONE);
                                swipeRefreshLayout.setRefreshing(false);
                                barChartDataSet=new BarDataSet(dataPoints, "Sessions Vs Repetitions Graph");
                                String data = message.toString();
                                summary = new JSONArray(data);
                                int dataLength = data.length();
                                int j=0;
                                for (int i=0;i<summary.length();i++) {
                                    barChartData.addEntry(new BarEntry(++j,summary.getJSONObject(i).getInt("repetition")),0);
                                    barChart.moveViewToX(j);
                                    barChartScroller.setDecimalScale(j);
                                }

                                barChart.notifyDataSetChanged();
                                barChart.invalidate();
                                /*if(summary.length()>5) {
                                    barChart.setVisibleXRangeMaximum(6f);
                                    barChart.setVisibleXRangeMinimum(3f);
                                }
                                barChartData.setBarWidth(1f);

                                barChart.getXAxis().setDrawGridLines(false);
                                barChart.setTouchEnabled(true);
                                barChart.setHorizontalScrollBarEnabled(true);

                                barChart.setScrollBarSize(1000);
                                barChart.setScrollContainer(true);
                                barChart.setScrollBarFadeDuration(100000);
                                barChart.setScrollbarFadingEnabled(true);
                                barChart.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
                                barChart.animateXY(3000, 3000);
                                barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
                                barChart.getXAxis().setAxisMaximum(barChartData.getXMax() + 0.25f);
                                barChart.getDescription().setEnabled(false);
                                barChart.enableScroll();
                                barChart.setScaleEnabled(false);
                                //Toast.makeText(context,Integer.toString(j),Toast.LENGTH_SHORT).show();
                                barChartScroller.setMax(summary.length());*/
                            }
                        }
                        public void connectionLost(Throwable cause) {}
                        public void deliveryComplete(IMqttDeliveryToken token) {}
                    });
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Log.d(TAG, "onFailure");
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver networkReceiver = new BroadcastReceiver (){
        @Override
        public void onReceive(final Context context, Intent intent) {
            /* super.onReceive(context, intent); */
            if(intent.getExtras()!=null) {
                ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                assert connectivityManager != null;
                NetworkInfo ni = connectivityManager.getActiveNetworkInfo();
                if (ni != null && ni.getState() == NetworkInfo.State.CONNECTED) {
//                    if (client != null) {
//                        client.unregisterResources();
//                        client.close();
//                    }
//                    startMqtt();
                    try {

                        MqttMessage message = new MqttMessage();
                        JSONObject object = new JSONObject();
                        object.put("patientid",patientId);
                        object.put("phizioemail",phizioemail);
                        message.setPayload(object.toString().getBytes());
                        if(mqttHelper!=null)
                            mqttHelper.publishMqttTopic(mqtt_publish_getpatientReport,message);
                        else {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }
    };


    private void initiatePopupWindow(int n,View view) throws JSONException {

        View layout = getLayoutInflater().inflate(R.layout.session_analysis, null);

        report = new PopupWindow(layout, ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT);
        report.setOutsideTouchable(true);
        report.setContentView(layout);
        report.setFocusable(true);
        report.showAtLocation(view, Gravity.CENTER, 0, 0);
        LinearLayout cancelbtn = layout.findViewById(R.id.cancel_action);
        LinearLayout shareIcon = layout.findViewById(R.id.shareIcon);
        TextView popUpPatientId = layout.findViewById(R.id.patientId);
        TextView popUpPatientName = layout.findViewById(R.id.patientName);
        final ConstraintLayout summaryView = layout.findViewById(R.id.summaryView);
        TextView totalReps = layout.findViewById(R.id.totalReps);
        TextView maxAngleView = layout.findViewById(R.id.maxAngle);
        TextView minAngleView  = layout.findViewById(R.id.minAngle);
        TextView maxEmgView  = layout.findViewById(R.id.maxEmg);
        TextView holdTimeView  = layout.findViewById(R.id.holdtime);
        TextView sessionTime = layout.findViewById(R.id.sessionTime);
        TextView heldTime = layout.findViewById(R.id.heldtime);
        popUpPatientId.setText(getActivity().getIntent().getStringExtra("patientId"));
        popUpPatientName.setText(getActivity().getIntent().getStringExtra("patientName"));
        maxAngleView.setText(array.getJSONObject(n).getString("maxangle")+"°");
        minAngleView.setText(array.getJSONObject(n).getString("minangle")+"°");
        maxEmgView.setText(array.getJSONObject(n).getString("maxemg")+"μA");
        String tempHoldTimeValue = array.getJSONObject(n).getString("holdtime");
        holdTimeView.setText(tempHoldTimeValue.substring(0,1)+"m"+tempHoldTimeValue.substring(2)+"s");
        totalReps.setText(array.getJSONObject(n).getString("numofreps"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tempSessionTime = array.getJSONObject(n).getString("sessiontime");
        tempSessionTime = tempSessionTime.substring(0,2)+"m"+tempSessionTime.substring(3,7)+"s";
        sessionTime.setText(tempSessionTime);
        heldTime.setText(array.getJSONObject(n).getString("heldon"));
        cancelbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                barChart.highlightValue(null);
                report.dismiss();

            }
        });
        shareIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OnClickShare(summaryView);
            }
        });
    }
    public void OnClickShare(View view){

        Bitmap bitmap =getBitmapFromView(view);
        try {
            File file = new File(context.getExternalCacheDir(),"logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap getBitmapFromView(View view) {
        Bitmap returnedBitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(returnedBitmap);
        Drawable bgDrawable =view.getBackground();
        if (bgDrawable!=null) {
            //has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        }   else{
            //does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        }
        view.draw(canvas);
        return returnedBitmap;
    }

}
