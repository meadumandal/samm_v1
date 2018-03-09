package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.umandalmead.samm_v1.EntityObjects.PassengerCountReport;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InterfaceAddress;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class mySQLPassengerCountReport extends AsyncTask<String, Void, ArrayList<PassengerCountReport>>{
    Context _context;
    Activity _activity;
    ProgressDialog progDialog;
    String progressMessage;
    ArrayList<PassengerCountReport> _listReport;
    ArrayList<BarDataSet> dataSet;
    ArrayList<String> xAxis;

    /**
     *This is the generic format in accessing data from mySQL
     * @param context
     * @param activity
     */
    public mySQLPassengerCountReport(Context context, Activity activity)
    {
        this._context = context;
        this._activity = activity;

        this.dataSet = new ArrayList<>();
        this.xAxis = new ArrayList<>();
        progDialog = new ProgressDialog(this._activity);
        progDialog.setMessage(progressMessage);
    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            progDialog.setIndeterminate(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setCancelable(false);
            progDialog.setMessage("Report is being generated");
            progDialog.setTitle("Fetching Data");
            progDialog.show();
        }
        catch(Exception e)
        {
            Toast.makeText(this._context, e.getMessage(), Toast.LENGTH_LONG).show();
        }


    }

    /**
     *
     * @return A hashmap with column names and values
     */
    @Override
    protected ArrayList<PassengerCountReport> doInBackground(String... params)
    {
        Helper helper = new Helper();

        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String reportDate = params[0];
//                String reportTerminal = params[1];
                String link = "http://meadumandal.website/sammAPI/getPassengerCountReport.php?reportDate="
                        + reportDate.toString()
                        + "&reportTerminal=";
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);
                ArrayList<PassengerCountReport> listReport = new ArrayList<>();
                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    String terminal = jsonobject.getString("TERMINAL");
                    int count = jsonobject.getInt("COUNT");
                    int hour = jsonobject.getInt("HOUR");

                    listReport.add(new PassengerCountReport(count, hour, terminal));
                }
                return listReport;
            }
            catch(Exception e)
            {
                Toast.makeText(this._context,e.getMessage(), Toast.LENGTH_LONG).show();
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
    protected void onPostExecute(ArrayList<PassengerCountReport> listReport)
    {
        try
        {
            BarChart chart = (BarChart) this._activity.findViewById(R.id.chart);

            getDataSet(listReport);
            BarData data = new BarData(this.xAxis, this.dataSet);
            chart.setData(data);
            chart.setDescription("Passenger Peak and Lean Hours");
            chart.setDescriptionTextSize(15);
            chart.setVisibleXRange(5);

            chart.animateXY(2000, 2000);
            chart.invalidate();
            progDialog.dismiss();
        }
        catch(Exception e)
        {
            Toast.makeText(this._context, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

    }

    private void getDataSet(ArrayList<PassengerCountReport> listReport) {
        ArrayList<BarDataSet> dataSets = null;
        ArrayList<String> xAxis = new ArrayList<>();


        ArrayList<BarEntry> valueSet1 = new ArrayList<>();

        dataSets = new ArrayList<>();

        int i = 0;
//        for(PassengerCountReport report:listReport)
//        {
//
//            BarEntry v1e1 = new BarEntry(report.count, i); // Jan
//            BarEntry v2e2 = new BarEntry(report.count, i); // Jan
//            xAxis.add(report.hour + ":00H");
//            valueSet1.add(v1e1);
//            valueSet.add(v2e2);
//
//
//
//            BarDataSet barDataSet1 = new BarDataSet(valueSet1, report.terminal);
//            barDataSet1.setColor(Color.rgb(27, 163, 156));
//            dataSets = new ArrayList<>();
//            dataSets.add(barDataSet1);
//
//
//            i++;
//        }

        BarEntry v1e1 = new BarEntry(3, 0); // Jan
        xAxis.add("8:00H");
        valueSet1 = new ArrayList<BarEntry>();
        valueSet1.add(v1e1);
        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Fastbytes");
        barDataSet1.setColor(Color.rgb(27, 163, 156));
        dataSets.add(barDataSet1);


        v1e1 = new BarEntry(2, 0); // Jan
        valueSet1 = new ArrayList<BarEntry>();
        valueSet1.add(v1e1);
        barDataSet1 = new BarDataSet(valueSet1, "5132");

        barDataSet1.setColor(Color.rgb(242, 171, 235));
        dataSets.add(barDataSet1);

        v1e1 = new BarEntry(4, 0); // Jan
        valueSet1 = new ArrayList<BarEntry>();
        valueSet1.add(v1e1);
        barDataSet1 = new BarDataSet(valueSet1, "Plaza A");
        barDataSet1.setColor(Color.rgb(66, 134, 244));
        dataSets.add(barDataSet1);

        v1e1 = new BarEntry(5, 0); // Jan
        valueSet1 = new ArrayList<BarEntry>();
        valueSet1.add(v1e1);
        barDataSet1 = new BarDataSet(valueSet1, "Bellevue Hotel");
        barDataSet1.setColor(Color.rgb(229, 110, 110));
        dataSets.add(barDataSet1);

        v1e1 = new BarEntry(1, 0); // Jan
        valueSet1 = new ArrayList<BarEntry>();
        valueSet1.add(v1e1);
        barDataSet1 = new BarDataSet(valueSet1, "Shakeys");
        barDataSet1.setColor(Color.rgb(229, 145, 110));
        dataSets.add(barDataSet1);

        v1e1 = new BarEntry(7, 0); // Jan
        valueSet1 = new ArrayList<BarEntry>();
        valueSet1.add(v1e1);
        barDataSet1 = new BarDataSet(valueSet1, "Vivere");
        barDataSet1.setColor(Color.rgb(229, 189, 110));
        dataSets.add(barDataSet1);
//
//        BarEntry v2e2 = new BarEntry(3, 0); // Jan
//        xAxis.add("9:00H");
//        ArrayList<BarEntry> valueSet2 = new ArrayList<BarEntry>();
//        valueSet2.add(v2e2);
//        BarDataSet barDataSet2 = new BarDataSet(valueSet2, "Fastbytes");
//        barDataSet2.setColor(Color.rgb(27, 163, 156));
//        dataSets.add(barDataSet2);
//
//
//        v2e2 = new BarEntry(2, 1); // Jan
//        valueSet2 = new ArrayList<BarEntry>();
//        valueSet2.add(v2e2);
//        barDataSet2 = new BarDataSet(valueSet2, "5132");
//
//        barDataSet2.setColor(Color.rgb(242, 171, 235));
//        dataSets.add(barDataSet2);
//
//        v2e2 = new BarEntry(4, 1); // Jan
//        valueSet2 = new ArrayList<BarEntry>();
//        valueSet2.add(v2e2);
//        barDataSet2 = new BarDataSet(valueSet2, "Plaza A");
//        barDataSet2.setColor(Color.rgb(66, 134, 244));
//        dataSets.add(barDataSet2);
//
//        v2e2 = new BarEntry(5, 1); // Jan
//        valueSet2 = new ArrayList<BarEntry>();
//        valueSet2.add(v2e2);
//        barDataSet2 = new BarDataSet(valueSet2, "Bellevue Hotel");
//        barDataSet2.setColor(Color.rgb(229, 110, 110));
//        dataSets.add(barDataSet2);
//
//        v2e2 = new BarEntry(1, 1); // Jan
//        valueSet2 = new ArrayList<BarEntry>();
//        valueSet2.add(v2e2);
//        barDataSet2 = new BarDataSet(valueSet2, "Shakeys");
//        barDataSet2.setColor(Color.rgb(229, 145, 110));
//        dataSets.add(barDataSet2);
//
//        v2e2 = new BarEntry(7, 1); // Jan
//        valueSet2 = new ArrayList<BarEntry>();
//        valueSet2.add(v2e2);
//        barDataSet2 = new BarDataSet(valueSet2, "Vivere");
//        barDataSet2.setColor(Color.rgb(229, 189, 110));
//        dataSets.add(barDataSet2);


        this.dataSet = dataSets;
        this.xAxis = xAxis;
    }



}
