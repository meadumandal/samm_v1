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
    LoaderDialog _LoaderDialog;
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
        _LoaderDialog = new LoaderDialog(this._activity,"", progressMessage);
    }
    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _LoaderDialog = new LoaderDialog(_activity, MenuActivity._GlobalResource.getString(R.string.dialog_fetching_data),MenuActivity._GlobalResource.getString(R.string.dialog_generating_report));
            _LoaderDialog.setCancelable(false);
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
    protected Void doInBackground(String... params)
    {
        busiestTimes = new ArrayList<>();
        Helper helper = new Helper();

        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String fromDate = params[0];
                String toDate = params[1];
                String terminal = params[2];
//                String _terminalAutoComplete =
                String link = _constants.WEB_API_URL + _constants.REPORTS_API_FOLDER +_constants.PASSENGERQUEUINGHISTORY_API_FILE_WITH_PENDING_QUERYSTRING +"terminal=" + terminal + "&fromDate="
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
                Helper.logger(ex,true);
                return null;
            }
            try{
                fromDate = params[0];
                toDate = params[1];
                terminal = params[2];
//                String _terminalAutoComplete =
                String link = _constants.WEB_API_URL + _constants.REPORTS_API_FOLDER + _constants.TERMINALBUSIESTTIME_API_FILE_WITH_PENDING_QUERYSTRING +"terminal=" + terminal + "&fromDate="
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
                Helper.logger(ex,true);
                return null;
            }
        }
        else
        {
            Toast.makeText(this._context, MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline), Toast.LENGTH_LONG).show();
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

            if(queueingHistory.isEmpty())
            {
                InfoDialog dialog=new InfoDialog(this._activity, MenuActivity._GlobalResource.getString(R.string.dialog_no_record_found));
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                _LoaderDialog.hide();
                tv_reportTerminal.setText("");
                tv_reportCoverage.setText("");
                tv_passengersWaitedDuringThisTime.setText("");
                return;
            }
            for(PassengerQueueingHistory history: queueingHistory)
            {
                TableRow tr = new TableRow(_context);
                LinearLayout.LayoutParams params =new LinearLayout.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.WRAP_CONTENT);
                params.setMargins(2,1,2,1);
                tr.setLayoutParams(params);
                tr.setGravity(Gravity.CENTER_HORIZONTAL);
                tr.setBackgroundColor(ContextCompat.getColor(_context, R.color.colorBlack));
                tr.setVisibility(View.VISIBLE);


                //TIME
                TextView tv_dateTime = new TextView(_context);
                TableRow.LayoutParams tv_dateTimeParam = new TableRow.LayoutParams(180, TableRow.LayoutParams.WRAP_CONTENT);
                tv_dateTimeParam.setMargins(3,0,1,0);
                tv_dateTime.setLayoutParams(tv_dateTimeParam);
                tv_dateTime.setTextColor(ContextCompat.getColor(_context, R.color.colorBlack));
                tv_dateTime.setBackground(new ColorDrawable(ContextCompat.getColor(_context, R.color.colorWhite)));
                tv_dateTime.setTypeface(tv_dateTime.getTypeface(),Typeface.BOLD);
                tv_dateTime.setTextSize(12);
                tv_dateTime.setPadding(8,5,5,5);
                tv_dateTime.setText(history.time);
                tr.addView(tv_dateTime);


                //NUMBER OF WAITING PASSENGERS
                TextView tv_noOfWaitingPssngr = new TextView(_context);
                TableRow.LayoutParams tv_noOfWaitingPassgrParam = new TableRow.LayoutParams(170, TableRow.LayoutParams.WRAP_CONTENT);
                tv_noOfWaitingPassgrParam.setMargins(2,0,1,0);
                tv_noOfWaitingPssngr.setLayoutParams(tv_noOfWaitingPassgrParam);

                tv_noOfWaitingPssngr.setTextColor(ContextCompat.getColor(_context, R.color.colorBlack));
                if (history.count>=10)
                    tv_noOfWaitingPssngr.setBackground(new ColorDrawable(ContextCompat.getColor(_context, R.color.colorRed)));
                else
                    tv_noOfWaitingPssngr.setBackground(new ColorDrawable(ContextCompat.getColor(_context, R.color.colorGreen)));

                tv_noOfWaitingPssngr.setPadding(8,5,5,5);
                if (history.count>1)tv_noOfWaitingPssngr.setText(history.count + MenuActivity._GlobalResource.getString(R.string.info_passengers_waited_plural));
                else tv_noOfWaitingPssngr.setText(history.count + MenuActivity._GlobalResource.getString(R.string.info_passenger_waited_singular));
                tr.addView(tv_noOfWaitingPssngr);
                cleanTable(passengerReportTable_history);
                passengerReportTable_history.addView(tr);
            }



            String strBusiestTimes = "";
            for(String busytime:busiestTimes)
            {
                strBusiestTimes += busytime + "\n";
            }
            strBusiestTimes = strBusiestTimes.substring(0, strBusiestTimes.length() - 1);

            tv_busiestTimes.setText(strBusiestTimes);
            tv_passengersWaitedDuringThisTime.setText("* " + passengersWaitedDuringThisTime + MenuActivity._GlobalResource.getString(R.string.info_suffix_passengers_waited));



        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
        _LoaderDialog.hide();

    }

    private void cleanTable(TableLayout table)
    {
        int childCount = table.getChildCount();
        if (childCount>3)
        {
            table.removeViews(3, childCount - 3);
        }

    }



}
