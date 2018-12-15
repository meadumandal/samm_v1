package com.umandalmead.samm_v1;

import android.*;
import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.util.SortedList;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.umandalmead.samm_v1.EntityObjects.Eloop;
import com.umandalmead.samm_v1.EntityObjects.SummaryReport;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncEcoloopKMTraveled extends AsyncTask<String, Void, ArrayList<SummaryReport>>{
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    String progressMessage;
    ArrayList<SummaryReport> _listReport;
    ArrayList<BarDataSet> dataSet;
    ArrayList<String> xAxis;
    Constants _constants;
    final ArrayList<PieEntry> AL_TopEntries = new ArrayList<>();
    final ArrayList<PieEntry> AL_OtherEntries = new ArrayList<>();
    final ArrayList<PieEntry> AL_TopEntriesMaxSpeed = new ArrayList<>();
    final ArrayList<PieEntry> AL_OtherEntriesMaxSpeed = new ArrayList<>();
    private Boolean _BOOL_IsMaxSpeed = false;
    public static String STR_fileName="";


    /**
     *This is the generic format in accessing data from mySQL
     * @param context
     * @param activity
     */
    public asyncEcoloopKMTraveled(Context context, Activity activity)
    {
        this._context = context;
        this._activity = activity;
        this._constants = new Constants();
        this.dataSet = new ArrayList<>();
        this.xAxis = new ArrayList<>();

        _LoaderDialog = new LoaderDialog(this._activity,"", progressMessage);
    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _LoaderDialog = new LoaderDialog(this._activity,"Fetching Data", "Report is being generated");
            _LoaderDialog.show();
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
    }

    /**
     *
     * @return A hashmap with column names and values
     */
    @Override
    protected ArrayList<SummaryReport> doInBackground(String... params)
    {
        Helper helper = new Helper();

        if (helper.isConnectedToInternet(this._context))
        {
            try{
                SimpleDateFormat parser =new SimpleDateFormat("MM/dd/yy");
                SimpleDateFormat date_format = new SimpleDateFormat("yyyy-MM-dd");
                NumberFormat formatter  = new DecimalFormat("#00.00");
                Date fromDate, toDate;
                fromDate = parser.parse(params[0]);
                toDate = parser.parse(params[1]);
                try {
                    STR_fileName += params[0] + "-" + params[1];
                }catch (Exception ex){
                    Helper.logger(ex);
                }

//                Calendar cal = Calendar.getInstance();
//                cal.setTime(fromDate);
//                cal.add(cal.DATE, -1);
//                fromDate = cal.getTime();

                String fromDateStr = date_format.format(fromDate);
                String toDateStr = date_format.format(toDate);
                String link = _constants.WEB_API_URL+ _constants.REPORTS_API_FOLDER + "getEcoloopSummary.php?fromDate="+fromDateStr+"&toDate="+toDateStr;
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
//                conn.setRequestProperty("Authorization", "Basic " + BasicAuth.encode(_constants.TRACCAR_USERNAME, _constants.TRACCAR_PASSWORD));
//                conn.setRequestProperty("Content-Type", "application/json");



                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                ArrayList<SummaryReport> listReport = new ArrayList<>();
                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    int deviceId = jsonobject.getInt("deviceId");
                    String deviceName = jsonobject.getString("deviceName");
                    double distance = Double.parseDouble(formatter.format(jsonobject.getDouble("distance") * 0.001));
                    double averageSpeed = jsonobject.getDouble("averageSpeed");
                    double maxSpeed = Double.parseDouble(formatter.format(jsonobject.getDouble("maxSpeed")));
                    double spentFuel = jsonobject.getDouble("spentFuel");
                    int engineHours = jsonobject.getInt("engineHours");
                    String plateNumber = "";
                    for (Eloop eloop : MenuActivity._eloopList)
                    {
                        if (eloop.DeviceId == deviceId || eloop.DeviceName.toLowerCase().equals(deviceName.toLowerCase()))
                        {
                            plateNumber = eloop.PlateNumber;
                            break;
                        }

                    }
                    listReport.add(new SummaryReport(deviceId, deviceName, plateNumber, distance, averageSpeed, maxSpeed, spentFuel, engineHours));
                }

                return listReport;
            }
            catch(Exception ex)
            {
                Helper.logger(ex,true);
                return null;
            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            return null;
        }

    }

    @Override
    protected void onPostExecute(ArrayList<SummaryReport> listReport)
    {
        Helper helper = new Helper(_activity,_context);
        try
        {
            Collections.sort(listReport, SummaryReport.SummaryReportComparator.DISTANCE_TRAVELED);
            InitializePieChart("KM Traveled",ReportsActivity._PC_EcoLoopMain);
            ReportsActivity._PC_EcoLoopMain.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                @Override
                public void onValueSelected(Entry e, Highlight h) {
                    PieEntry PE = (PieEntry)e;
                    if(PE!=null && PE.getLabel().toLowerCase().equals("others")){
                        Toggle(ReportsActivity._BTN_BACK_EcoLoop_Others);
                        STR_fileName+="others";
                       if(_BOOL_IsMaxSpeed) {
                           InitializePieChart("Others Max Speed", ReportsActivity._PC_EcoLoopMain);
                           SetPieChartData(AL_OtherEntriesMaxSpeed,null, ReportsActivity._PC_EcoLoopMain, false);
                           ReportsActivity._BTN_BACK_EcoLoop_Others.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   InitializePieChart("KM Traveled", ReportsActivity._PC_EcoLoopMain);
                                   SetPieChartData(AL_TopEntriesMaxSpeed, null, ReportsActivity._PC_EcoLoopMain, false);
                                   Toggle(ReportsActivity._BTN_BACK_EcoLoop_Others);
                                   STR_fileName.replace("others","");
                               }
                           });
                       }
                       else {
                           InitializePieChart("Others KM Traveled", ReportsActivity._PC_EcoLoopMain);
                           SetPieChartData(AL_OtherEntries,null, ReportsActivity._PC_EcoLoopMain, true);
                           ReportsActivity._BTN_BACK_EcoLoop_Others.setOnClickListener(new View.OnClickListener() {
                               @Override
                               public void onClick(View view) {
                                   InitializePieChart("KM Traveled", ReportsActivity._PC_EcoLoopMain);
                                   SetPieChartData(AL_TopEntries, null, ReportsActivity._PC_EcoLoopMain, true);
                                   Toggle(ReportsActivity._BTN_BACK_EcoLoop_Others);
                                   STR_fileName.replace("others","");
                               }
                           });
                       }

                    }
                }

                @Override
                public void onNothingSelected() {

                }
            });
            double D_KM_accumulatedValues = 0.0, D_MaxSpeed_accumulatedValues=0.0 ;
            int INT_Splitter = (listReport.size()%2==0) ? listReport.size()/2 : (int)((listReport.size()/2)+0.5);
            for (int i = 0; i < listReport.size() ; i++) {
                if(i<=INT_Splitter) {
                    AL_TopEntries.add(new PieEntry((float) listReport.get(i).distance, String.valueOf(listReport.get(i).plateNumber)));
                    AL_TopEntriesMaxSpeed.add(new PieEntry((float) listReport.get(i).maxSpeed, String.valueOf(listReport.get(i).plateNumber)));
                }
                else{
                    //accumulate values
                    AL_OtherEntries.add(new PieEntry((float) listReport.get(i).distance, String.valueOf(listReport.get(i).plateNumber)));
                    AL_OtherEntriesMaxSpeed.add(new PieEntry((float) listReport.get(i).maxSpeed, String.valueOf(listReport.get(i).plateNumber)));
                    D_KM_accumulatedValues+=listReport.get(i).distance;
                    D_MaxSpeed_accumulatedValues+=listReport.get(i).maxSpeed;
                }
            }

            if(AL_OtherEntries.size()!=0 && D_KM_accumulatedValues!=0.0) {
                AL_TopEntries.add(new PieEntry((float) D_KM_accumulatedValues, "Others"));
                AL_TopEntriesMaxSpeed.add(new PieEntry((float) D_MaxSpeed_accumulatedValues, "Others"));
            }

            SetPieChartData(AL_TopEntries, null, ReportsActivity._PC_EcoLoopMain, true);
            InflateTabLayout();
            if (ReportsActivity._TL_EcoloopTraveled.getVisibility()==View.INVISIBLE)
                Toggle(ReportsActivity._TL_EcoloopTraveled);
            ReportsActivity._IV_ExportReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(_activity,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);


                    ReportsActivity._PC_EcoLoopMain.saveToGallery(GenerateReportImageFileName(),100);//,"/DCIM/Camera");
                    Handler HND_PostExportMessage = new Handler();
                    HND_PostExportMessage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InfoDialog dialog = new InfoDialog(_activity, "Image has been exported.\n FileName: "+ GenerateReportImageFileName());
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        }
                    }, 2000);

                }
            });
//            for(SummaryReport summary:listReport)
//            {
//                TableRow row = new TableRow(_context);
//                row.setGravity(Gravity.CENTER_HORIZONTAL);
//                TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
//                row.setLayoutParams(tableRowParams);
//
//                TextView plateNumber = new TextView(_context);
//                plateNumber.setText(summary.plateNumber);
//                plateNumber.setTextColor(Color.BLACK);
//                plateNumber.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorWhite));
//                plateNumber.setPadding(helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context));
//                plateNumber.setTypeface(null, Typeface.BOLD);
//                TableRow.LayoutParams textViewParams = new TableRow.LayoutParams(helper.dpToPx(120,_context), ActionBar.LayoutParams.WRAP_CONTENT);
//                textViewParams.setMargins(helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context));
//
//                plateNumber.setLayoutParams(textViewParams);
//
//
//                TextView distance = new TextView(_context);
//                distance.setText(Double.toString(summary.distance) + " km");
//                distance.setGravity(Gravity.RIGHT);
//                distance.setTextColor(Color.BLACK);
//                distance.setBackgroundResource(R.color.colorWhite);
//                distance.setPadding(helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context));
//                textViewParams = new TableRow.LayoutParams(helper.dpToPx(110,_context), ActionBar.LayoutParams.WRAP_CONTENT);
//                textViewParams.setMargins(helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context));
//                distance.setLayoutParams(textViewParams);
//
//
//                TextView maxSpeed = new TextView(_context);
//                maxSpeed.setText(Double.toString(summary.maxSpeed) + " kn");
//                maxSpeed.setGravity(Gravity.RIGHT);
//                maxSpeed.setTextColor(Color.BLACK);
//                maxSpeed.setBackgroundResource(R.color.colorWhite);
//                maxSpeed.setPadding(helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context));
//                textViewParams = new TableRow.LayoutParams(helper.dpToPx(100,_context), ActionBar.LayoutParams.WRAP_CONTENT);
//                textViewParams.setMargins(helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context));
//                maxSpeed.setLayoutParams(textViewParams);
//
//
//                row.addView(plateNumber);
//                row.addView(distance);
//                row.addView(maxSpeed);
//
//                ReportsActivity._vehicleReportTable.addView(row);
//
//            }
            _LoaderDialog.dismiss();
        }
        catch(Exception ex)
        {
            _LoaderDialog.dismiss();
            Helper.logger(ex,true);

        }

    }
    private void InitializePieChart(String PieCenterText, PieChart PC){
       // ReportsActivity._vehicleReportTable.setVisibility(View.VISIBLE);
        PC.setEntryLabelColor(Color.BLACK);
        PC.setEntryLabelTypeface(Helper.FONT_RUBIK_REGULAR);
        PC.setEntryLabelTextSize(12f);
        PC.animateY(1400, Easing.EaseInOutQuad);
        PC.setCenterText(PieCenterText);
        PC.setCenterTextTypeface(Helper.FONT_RUBIK_BOLD);
        PC.setHoleRadius(30f);
        PC.setCenterTextTypeface(Helper.FONT_RUBIK_REGULAR);
        PC.setEntryLabelTypeface(Helper.FONT_RUBIK_BOLD);
        InitializePieLegend(PC);
    }
    private void InitializePieLegend(PieChart PC){
        Legend legend = PC.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setEnabled(true);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(0f);
        legend.setYOffset(0f);
    }
    private void SetPieChartData(ArrayList<PieEntry> AL_PieEntries, String LegendTitle, PieChart PC, final Boolean IsDistance){
        PieDataSet dataSet = new PieDataSet(AL_PieEntries, LegendTitle);

        ArrayList<Integer> colors = new ArrayList<>();
        for (int c : ColorTemplate.VORDIPLOM_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.PASTEL_COLORS)
            colors.add(c);
        for (int c : ColorTemplate.MATERIAL_COLORS)
            colors.add(c);

        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);
        //dataSet.ico(false);
        dataSet.setSliceSpace(3f);
        //dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setValueFormatter(new IValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                return String.valueOf(value) + (IsDistance ? " km" : " kn");
            }
        });
        dataSet.setSelectionShift(10);
        //dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        //dataSet.setValueLinePart1OffsetPercentage(100f); /** When valuePosition is OutsideSlice, indicates offset as percentage out of the slice size */
        //dataSet.setValueLinePart1Length(0.6f); /** When valuePosition is OutsideSlice, indicates length of first half of the line */
        //dataSet.setValueLinePart2Length(0.6f); /** When valuePosition is OutsideSlice, indicates length of second half of the line */
        //PC.setExtraOffsets(0.f, 5.f, 0.f, 5.f);

        PieData data = new PieData(dataSet);
        data.setValueTextSize(11f);
        data.setValueTextColor(Color.BLACK);


        data.setValueTypeface(Helper.FONT_RUBIK_REGULAR);
        PC.setData(data);
        PC.highlightValues(null);
        PC.invalidate();
        ReportsActivity._IV_ExportReport.setVisibility(View.VISIBLE);
    }

    private void Toggle(View v){
        v.setVisibility(v.getVisibility()==View.INVISIBLE? View.VISIBLE:View.INVISIBLE);
    }
    private void InflateTabLayout(){
        ReportsActivity._TL_EcoloopTraveled.removeAllTabs();
        ReportsActivity._TL_EcoloopTraveled.addTab(ReportsActivity._TL_EcoloopTraveled.newTab().setText("Distance Traveled"));
        ReportsActivity._TL_EcoloopTraveled.addTab(ReportsActivity._TL_EcoloopTraveled.newTab().setText("Max Speed"));
        ReportsActivity._TL_EcoloopTraveled.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if(tab.getPosition() == 1){
                    InitializePieChart("Max Speed",ReportsActivity._PC_EcoLoopMain);
                    SetPieChartData(AL_TopEntriesMaxSpeed, null, ReportsActivity._PC_EcoLoopMain, false);
                    _BOOL_IsMaxSpeed =true;
                   ReportsActivity._BTN_BACK_EcoLoop_Others.setVisibility(View.INVISIBLE);
                    STR_fileName.replace("others","");
                }
                else if(tab.getPosition()==0){
                    InitializePieChart("KM Traveled",ReportsActivity._PC_EcoLoopMain);
                    SetPieChartData(AL_TopEntries, null, ReportsActivity._PC_EcoLoopMain, true);
                    _BOOL_IsMaxSpeed =false;
                    ReportsActivity._BTN_BACK_EcoLoop_Others.setVisibility(View.INVISIBLE);
                    STR_fileName.replace("others","");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        TabLayout.Tab tab = ReportsActivity._TL_EcoloopTraveled.getTabAt(0);
        tab.select();

    }

    public static String GenerateReportImageFileName(){
        String STR_Result = "";
        try{
            switch(ReportsActivity._TL_EcoloopTraveled.getSelectedTabPosition()){
                case 0: STR_Result="Distance Traveled";break;
                case 1: STR_Result="Max Speed"; break;
                default: STR_Result="Unknown"; break;
            }
            STR_Result+=" ("+STR_fileName.replace("/","-")+")";
        }
        catch (Exception ex){
            Helper.logger(ex);
        }
        return  STR_Result;
    }



}
