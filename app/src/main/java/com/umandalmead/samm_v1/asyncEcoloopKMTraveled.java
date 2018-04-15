package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
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
import java.util.Calendar;
import java.util.Date;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class asyncEcoloopKMTraveled extends AsyncTask<String, Void, ArrayList<SummaryReport>>{
    Context _context;
    Activity _activity;
    ProgressDialog _progressDialog;
    String progressMessage;
    ArrayList<SummaryReport> _listReport;
    ArrayList<BarDataSet> dataSet;
    ArrayList<String> xAxis;
    Constants _constants;


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

        _progressDialog = new ProgressDialog(this._activity);
        _progressDialog.setMessage(progressMessage);
    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _progressDialog.setIndeterminate(false);
            _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _progressDialog.setCancelable(false);
            _progressDialog.setMessage("Report is being generated");
            _progressDialog.setTitle("Fetching Data");
            _progressDialog.show();
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

                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);
                cal.add(cal.DATE, -1);
                fromDate = cal.getTime();

                String fromDateStr = date_format.format(fromDate) + "T16:00:00Z";
                String toDateStr = date_format.format(toDate) + "T16:00:00Z";
                String link = _constants.TRACCAR_SERVER+"api/reports/summary?groupId=1&from="+fromDateStr+"&to="+toDateStr;
                URL url = new URL(link);
                URLConnection conn = url.openConnection();
                conn.setRequestProperty("Authorization", "Basic " + BasicAuth.encode(_constants.TRACCAR_USERNAME, _constants.TRACCAR_PASSWORD));
                conn.setRequestProperty("Content-Type", "application/json");



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
                        if (eloop.DeviceId == deviceId)
                        {
                            plateNumber = eloop.PlateNumber;
                            break;
                        }

                    }
                    listReport.add(new SummaryReport(deviceId, deviceName, plateNumber, distance, averageSpeed, maxSpeed, spentFuel, engineHours));
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
    protected void onPostExecute(ArrayList<SummaryReport> listReport)
    {
        try
        {
            ReportsActivity._reportChart.setVisibility(View.GONE);
            ReportsActivity._reportTable.setVisibility(View.VISIBLE);
            for(SummaryReport summary:listReport)
            {
                TableRow row = new TableRow(_context);
                row.setGravity(Gravity.CENTER_HORIZONTAL);
                TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
                int topMargin = 5;
                tableRowParams.setMargins(0,topMargin,0,0);
                row.setLayoutParams(tableRowParams);


                TextView plateNumber = new TextView(_context);
                plateNumber.setText(summary.plateNumber);
                plateNumber.setGravity(Gravity.LEFT);
                plateNumber.setTextColor(Color.BLACK);

                TextView distance = new TextView(_context);
                distance.setText(Double.toString(summary.distance) + " km");
                distance.setGravity(Gravity.RIGHT);
                distance.setTextColor(Color.BLACK);

                TextView maxSpeed = new TextView(_context);
                maxSpeed.setText(Double.toString(summary.maxSpeed) + " kn");
                maxSpeed.setGravity(Gravity.RIGHT);
                maxSpeed.setTextColor(Color.BLACK);

                row.addView(plateNumber);
                row.addView(distance);
                row.addView(maxSpeed);

                ReportsActivity._reportTable.addView(row);

            }
            _progressDialog.dismiss();
        }
        catch(Exception e)
        {
            Toast.makeText(this._context, e.getMessage().toString(), Toast.LENGTH_LONG).show();
        }

    }





}
