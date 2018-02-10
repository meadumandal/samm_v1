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


public class mySQLEcoloopCount extends AsyncTask<String, Void, ArrayList<PassengerCountReport>>{
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
    public mySQLEcoloopCount(Context context, Activity activity)
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
//                String reportDate = params[0];
//                String link = "http://meadumandal.website/sammAPI/getPassengerCountReport.php?reportDate="
//                        + reportDate.toString()
//                        + "&reportTerminal=" + reportTerminal;
//                URL url = new URL(link);
//                URLConnection conn = url.openConnection();
//
//                conn.setDoOutput(true);
//
//                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//                String jsonResponse = reader.readLine();
//                JSONArray jsonArray = new JSONArray(jsonResponse);
                    ArrayList<PassengerCountReport> listReport = new ArrayList<>();
//                for(int index=0; index < jsonArray.length(); index++) {
//                    JSONObject jsonobject = jsonArray.getJSONObject(index);
//                    int count = jsonobject.getInt("COUNT");
//                    int hour = jsonobject.getInt("HOUR");
//
//                    listReport.add(new PassengerCountReport(count, hour));
//                }

                listReport.add(new PassengerCountReport(5, 5));
                listReport.add(new PassengerCountReport(8, 6));
                listReport.add(new PassengerCountReport(8, 7));
                listReport.add(new PassengerCountReport(8, 8));
                listReport.add(new PassengerCountReport(9, 9));
                listReport.add(new PassengerCountReport(9, 10));
                listReport.add(new PassengerCountReport(8, 11));
                listReport.add(new PassengerCountReport(8, 12));
                listReport.add(new PassengerCountReport(8, 13));
                listReport.add(new PassengerCountReport(8, 14));
                listReport.add(new PassengerCountReport(8, 15));
                listReport.add(new PassengerCountReport(10, 16));
                listReport.add(new PassengerCountReport(10, 17));
                listReport.add(new PassengerCountReport(10, 18));
                listReport.add(new PassengerCountReport(10, 19));
                listReport.add(new PassengerCountReport(10, 20));
                listReport.add(new PassengerCountReport(5, 21));
                listReport.add(new PassengerCountReport(5, 22));
                listReport.add(new PassengerCountReport(5, 23));
                listReport.add(new PassengerCountReport(5, 24));
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
            chart.setDescription("Ecoloop Flow");
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
        int i = 0;
        for(PassengerCountReport report:listReport)
        {
//            if(i>0)
//            {
//                while(report.hour - 1 != Integer.parseInt(xAxis.get(i-1).replace(":00H","")))
//                {
//                    BarEntry v1e1 = new BarEntry(0, i);
//                    xAxis.add(Integer.parseInt(xAxis.get(i-1).replace(":00H","")) + 1 + ":00H");
//                    valueSet1.add(v1e1);
//                    i++;
//                }
//            }
//            else
//            {
//                while(i != report.hour)
//                {
//                    BarEntry v1e1 = new BarEntry(0, i);
//                    xAxis.add(Integer.toString(i) + ":00H");
//                    valueSet1.add(v1e1);
//                    i++;
//                }
//            }
            BarEntry v1e1 = new BarEntry(report.count, i); // Jan
            xAxis.add(report.hour + ":00H");
            valueSet1.add(v1e1);
            i++;
        }
//        while(i < 25)
//        {
//            BarEntry v1e1 = new BarEntry(0, i);
//            xAxis.add(Integer.toString(i) + ":00H");
//            valueSet1.add(v1e1);
//            i++;
//        }

        BarDataSet barDataSet1 = new BarDataSet(valueSet1, "Number of Ecoloops travelling");
        barDataSet1.setColor(Color.rgb(27, 163, 156));
        dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        this.dataSet = dataSets;
        this.xAxis = xAxis;
    }



}
