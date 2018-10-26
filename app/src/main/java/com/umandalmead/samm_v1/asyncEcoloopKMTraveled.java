package com.umandalmead.samm_v1;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarDataSet;
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
import java.util.Date;


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
        clearTable();
        try
        {
            ReportsActivity._reportChart.setVisibility(View.GONE);
            ReportsActivity._vehicleReportTable.setVisibility(View.VISIBLE);
            for(SummaryReport summary:listReport)
            {
                TableRow row = new TableRow(_context);
                row.setGravity(Gravity.CENTER_HORIZONTAL);
                TableLayout.LayoutParams tableRowParams = new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT,TableLayout.LayoutParams.WRAP_CONTENT);
                row.setLayoutParams(tableRowParams);

                TextView plateNumber = new TextView(_context);
                plateNumber.setText(summary.plateNumber);
                plateNumber.setTextColor(Color.BLACK);
                plateNumber.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorWhite));
                plateNumber.setPadding(helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context));
                plateNumber.setTypeface(null, Typeface.BOLD);
                TableRow.LayoutParams textViewParams = new TableRow.LayoutParams(helper.dpToPx(120,_context), ActionBar.LayoutParams.WRAP_CONTENT);
                textViewParams.setMargins(helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context));

                plateNumber.setLayoutParams(textViewParams);


                TextView distance = new TextView(_context);
                distance.setText(Double.toString(summary.distance) + " km");
                distance.setGravity(Gravity.RIGHT);
                distance.setTextColor(Color.BLACK);
                distance.setBackgroundResource(R.color.colorWhite);
                distance.setPadding(helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context));
                textViewParams = new TableRow.LayoutParams(helper.dpToPx(110,_context), ActionBar.LayoutParams.WRAP_CONTENT);
                textViewParams.setMargins(helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context));
                distance.setLayoutParams(textViewParams);


                TextView maxSpeed = new TextView(_context);
                maxSpeed.setText(Double.toString(summary.maxSpeed) + " kn");
                maxSpeed.setGravity(Gravity.RIGHT);
                maxSpeed.setTextColor(Color.BLACK);
                maxSpeed.setBackgroundResource(R.color.colorWhite);
                maxSpeed.setPadding(helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context),helper.dpToPx(5,_context));
                textViewParams = new TableRow.LayoutParams(helper.dpToPx(100,_context), ActionBar.LayoutParams.WRAP_CONTENT);
                textViewParams.setMargins(helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context),helper.dpToPx(1,_context));
                maxSpeed.setLayoutParams(textViewParams);


                row.addView(plateNumber);
                row.addView(distance);
                row.addView(maxSpeed);

                ReportsActivity._vehicleReportTable.addView(row);

            }
            _LoaderDialog.dismiss();
        }
        catch(Exception ex)
        {
            _LoaderDialog.dismiss();
            Helper.logger(ex,true);

        }

    }

    private void clearTable()
    {
        int childCount = ReportsActivity._vehicleReportTable.getChildCount();
        if (childCount>0)
        {
            ReportsActivity._vehicleReportTable.removeViews(2, childCount - 2);

        }
    }





}
