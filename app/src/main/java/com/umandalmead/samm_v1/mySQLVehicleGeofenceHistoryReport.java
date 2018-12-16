package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.umandalmead.samm_v1.EntityObjects.VehicleGeofenceHistory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Created by MeadRoseAnn on 11/11/2018.
 */


public class mySQLVehicleGeofenceHistoryReport extends AsyncTask<String, Void, ArrayList<VehicleGeofenceHistory>>{
    Context _context;
    Activity _activity;

    Helper _helper;
    Constants _constants;
    public static String STR_dateRange="";
    /**
     *This is the generic format in accessing data from mySQL
     * @param context
     * @param activity
     */
    public mySQLVehicleGeofenceHistoryReport(Context context, Activity activity)
    {
        this._context = context;
        this._activity = activity;
        this._helper = new Helper(_activity,_context);
        this._constants = new Constants();

    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();

        }
        catch(Exception ex)
        {
            _helper.logger(ex,true);
        }


    }

    /**
     *
     * @return A hashmap with column names and values
     */
    @Override
    protected  ArrayList<VehicleGeofenceHistory> doInBackground(String... params)
    {
        ArrayList<VehicleGeofenceHistory> vehicleGeofenceHistories = new ArrayList<>();
        if (_helper.isConnectedToInternet(this._context))
        {
            try{

                String lineID = params[0];
                String destinationValue = params[1];
                String from = params[2];
                String to = params[3];
                String link = _constants.WEB_API_URL
                        + _constants.REPORTS_API_FOLDER
                        + _constants.VEHICLE_GEOFENCE_HISTORY_REPORT_WITH_PENDING_QUERYSTRING
                        +"lineID=" + lineID
                        + "&destinationValue=" + destinationValue
                        + "&fromDate=" + from
                        + "&toDate=" + to;
                STR_dateRange= from + " to " + to;
                URL url = new URL(link);

                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);


                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    String stationName = jsonobject.getString("StationName");
                    String plateNumber = jsonobject.getString("PlateNumber");
                    Integer numberOfTrips = jsonobject.getInt("NumberOfTrips");
                    vehicleGeofenceHistories.add(new VehicleGeofenceHistory(stationName, plateNumber, numberOfTrips));
                }

            }
            catch(Exception ex)
            {
                _helper.logger(ex,true);

            }

        }
        else
        {
            Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();

        }
        return vehicleGeofenceHistories;
    }

    @Override
    protected void onPostExecute(ArrayList<VehicleGeofenceHistory> vehicleGeofenceHistories)
    {

        try
        {

            ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();
            Legend l = ReportsActivity._PC_NumberOfRounds.getLegend();
            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
            l.setEnabled(true);
            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
            l.setOrientation(Legend.LegendOrientation.VERTICAL);
            l.setDrawInside(false);
            l.setXEntrySpace(7f);
            l.setYEntrySpace(0f);
            l.setYOffset(0f);

            for(VehicleGeofenceHistory vehicleGeofenceHistory:vehicleGeofenceHistories)
            {
                yValues.add(new PieEntry(vehicleGeofenceHistory.NumberOfTrips,vehicleGeofenceHistory.PlateNumber));
            }



            PieDataSet pieDataSet = new PieDataSet(yValues, null);
            ArrayList<Integer> colors = new ArrayList<>();
            for(int c: ColorTemplate.VORDIPLOM_COLORS)
                colors.add(c);
            for(int c:ColorTemplate.JOYFUL_COLORS)
                colors.add(c);
            for(int c:ColorTemplate.COLORFUL_COLORS)
                colors.add(c);
            for(int c:ColorTemplate.LIBERTY_COLORS)
                colors.add(c);
            for(int c:ColorTemplate.PASTEL_COLORS)
                colors.add(c);

            pieDataSet.setColors(colors);
            pieDataSet.setSliceSpace(3f);
            pieDataSet.setSelectionShift(5f);
            pieDataSet.setValueFormatter(new IntegerValueFormatter());

            PieData data = new PieData(pieDataSet);
            data.setValueTextSize(11f);
            data.setValueTextColor(Color.BLACK);
            data.setValueTypeface(Helper.FONT_RUBIK_REGULAR);
            ReportsActivity._PC_NumberOfRounds.setData(data);
            ReportsActivity._PC_NumberOfRounds.highlightValues(null);
            ReportsActivity._PC_NumberOfRounds.invalidate();
            ReportsActivity._IV_ExportReport.setVisibility(View.GONE);
            ReportsActivity._IV_ExportReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ActivityCompat.requestPermissions(_activity,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            1);


                    ReportsActivity._PC_NumberOfRounds.saveToGallery("VehicleRoundsReport",100);//,"/DCIM/Camera");
                    Handler HND_PostExportMessage = new Handler();
                    HND_PostExportMessage.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            InfoDialog dialog = new InfoDialog(_activity, "Image has been exported.\n FileName: "+ "VehicleRoundsReport " + STR_dateRange);
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        }
                    }, 2000);

                }
            });

        }
        catch(Exception ex)
        {
            _helper.logger(ex,true);
        }



    }




}
