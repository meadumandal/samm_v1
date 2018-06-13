package com.umandalmead.samm_v1;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.opengl.Visibility;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.data.BarDataSet;
import com.google.android.gms.awareness.state.BeaconState;
import com.umandalmead.samm_v1.EntityObjects.PassengerQueueingHistory;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;


/**
 * Created by MeadRoseAnn on 10/1/2017.
 */


public class mySQLPassengerQueueingHistoryReport extends AsyncTask<String, Void, Void>{
    Context _context;
    Activity _activity;
    ProgressDialog progDialog;
    String progressMessage;
    ArrayList<PassengerQueueingHistory> _listReport;
    ArrayList<BarDataSet> dataSet;
    ArrayList<String> xAxis;
    private Constants _constants = new Constants();
    public static ArrayList<String> busiestTimes = new ArrayList<String>();
    public static ArrayList<PassengerQueueingHistory> queueingHistory = new ArrayList<PassengerQueueingHistory>();
    public static int passengersWaitedDuringThisTime =0;
    public static String fromDate;
    public static String toDate;
    public static String terminal;

    /**
     *This is the generic format in accessing data from mySQL
     * @param context
     * @param activity
     */
    public mySQLPassengerQueueingHistoryReport(Context context, Activity activity)
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
        catch(Exception ex)
        {
            Helper.logger(ex);
        }


    }

    /**
     *
     * @return A hashmap with column names and values
     */
    @Override
    protected Void doInBackground(String... params)
    {
        Helper helper = new Helper();

        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String fromDate = params[0];
                String toDate = params[1];
                String terminal = params[2];
//                String _terminalAutoComplete =
                String link = _constants.WEB_API_URL + "getPassengerQueueingHistory.php?terminal=" + terminal + "&fromDate="
                        + fromDate.toString() + "&toDate=" + toDate.toString();
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);

                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    //String terminal = jsonobject.getString("TERMINAL");
                    String time = jsonobject.getString("DateTime");
                    int count = jsonobject.getInt("numberOfWaitingPassengers");

                    queueingHistory.add(new PassengerQueueingHistory(time, count));//, terminal));
                }

            }
            catch(Exception ex)
            {
                Helper.logger(ex);
                return null;
            }
            try{
                fromDate = params[0];
                toDate = params[1];
                terminal = params[2];
//                String _terminalAutoComplete =
                String link = _constants.WEB_API_URL + "getTerminalBusiestTimes.php?terminal=" + terminal + "&fromDate="
                        + fromDate.toString() + "&toDate=" + toDate.toString();
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray = new JSONArray(jsonResponse);

                for(int index=0; index < jsonArray.length(); index++) {
                    JSONObject jsonobject = jsonArray.getJSONObject(index);
                    //String terminal = jsonobject.getString("TERMINAL");

                    String dateTime = jsonobject.getString("DateTime");
                    busiestTimes.add(dateTime);//, terminal));
                    if (index<=0)
                        passengersWaitedDuringThisTime = jsonobject.getInt("numberofWaitingPassengers");
                }

            }
            catch(Exception ex)
            {
                Helper.logger(ex);
                return null;
            }
        }
        else
        {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            return null;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void v)
    {
        try
        {

            TableLayout passengerReportTable_history = (TableLayout)this._activity.findViewById(R.id.passengerReportTable_history);
            TextView tv_busiestTimes = (TextView) this._activity.findViewById(R.id.tv_busiesttime);
            TextView tv_passengersWaitedDuringThisTime = (TextView)this._activity.findViewById(R.id.tv_PassengersWaitedDuringThisTime);
            TextView tv_reportCoverage = (TextView) this._activity.findViewById(R.id.tv_reportCoverage);
            TextView tv_reportTerminal = (TextView) this._activity.findViewById(R.id.tv_reportTerminal);
            tv_reportTerminal.setText(terminal);
            tv_reportCoverage.setText(fromDate + " to " + toDate);
            String strBusiestTimes = "";
            for(String busytime:busiestTimes)
            {
                strBusiestTimes += busytime + "\n";
            }
            strBusiestTimes = strBusiestTimes.substring(0, strBusiestTimes.length() - 1);
            tv_busiestTimes.setText(strBusiestTimes);
            tv_passengersWaitedDuringThisTime.setText("* " + passengersWaitedDuringThisTime + " passengers waited during this time.");
            progDialog.hide();

            for(PassengerQueueingHistory history: queueingHistory)
            {
                TableRow tr = new TableRow(_context);
                TableRow.LayoutParams params =new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(2,1,2,1);
                tr.setLayoutParams(params);
                tr.setGravity(Gravity.CENTER_HORIZONTAL);
                tr.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorBlack));
                tr.setPadding(5,5,5,5);
                tr.setVisibility(View.VISIBLE);

                //TIME
                TextView tv_dateTime = new TextView(_context);

                tv_dateTime.setTextColor(ContextCompat.getColor(_context, R.color.colorBlack));
                tv_dateTime.setBackground(new ColorDrawable(ContextCompat.getColor(_context, R.color.colorWhite)));
                tv_dateTime.setTypeface(tv_dateTime.getTypeface(),Typeface.BOLD);
                tv_dateTime.setTextSize(12);
                tv_dateTime.setPadding(8,5,5,5);
                TableRow.LayoutParams tvParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                tvParams.setMargins(10,0,1,0);
                //tv_dateTime.setLayoutParams(tvParams);

                tr.addView(tv_dateTime);
                tv_dateTime.setText(history.time);

                //NUMBER OF WAITING PASSENGERS
                TextView tv_noOfWaitingPssngr = new TextView(_context);

                TableRow.LayoutParams tvParams1 = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                tvParams1.setMargins(2,0,3,0);
                //tv_noOfWaitingPssngr.setLayoutParams(tvParams1);

                tv_noOfWaitingPssngr.setTextColor(ContextCompat.getColor(_context, R.color.colorBlack));
                if (history.count>=10)
                    tv_noOfWaitingPssngr.setBackground(new ColorDrawable(ContextCompat.getColor(_context, R.color.colorRed)));
                else
                    tv_noOfWaitingPssngr.setBackground(new ColorDrawable(ContextCompat.getColor(_context, R.color.colorGreen)));

                tv_noOfWaitingPssngr.setPadding(8,5,5,5);
                if (history.count>1)
                    tv_noOfWaitingPssngr.setText(history.count + " passengers waited");
                else
                    tv_noOfWaitingPssngr.setText(history.count + " passenger waited");
                tr.addView(tv_noOfWaitingPssngr);


                passengerReportTable_history.addView(tr);
            }



            //BarChart chart = (BarChart) this._activity.findViewById(R.id.reportChart);


        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }

    }



}
