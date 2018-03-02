package com.umandalmead.samm_v1;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.umandalmead.samm_v1.EntityObjects.Destination;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends AppCompatActivity {
    public Calendar myCalendar = Calendar.getInstance();
    public static String TAG="mead";
    public EditText reportDate;
    public AutoCompleteTextView reportTerminal;
    public static SessionManager _sessionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reports);
            reportDate = (EditText) findViewById(R.id.reportDate);
            reportTerminal = (AutoCompleteTextView) findViewById(R.id.reportTerminal);
            this._sessionManager = new SessionManager(getApplicationContext());
            if (_sessionManager.GetReportType() == "ecoloop")
            {
                AutoCompleteTextView txtViewTerminal = (AutoCompleteTextView)findViewById(R.id.reportTerminal);
                txtViewTerminal.setVisibility(View.GONE);
            }


            List<String> array = new ArrayList<>();

            ArrayAdapter<String> adapter;

            for(Destination d:MenuActivity._listDestinations)
            {
                array.add(d.Value);
            }
            adapter = new ArrayAdapter<>(ReportsActivity.this,android.R.layout.simple_list_item_1, array);

            reportTerminal.setThreshold(1);
            reportTerminal.setAdapter(adapter);



            EditText edittext= (EditText) findViewById(R.id.reportDate);
            final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    myCalendar.set(Calendar.YEAR, year);
                    myCalendar.set(Calendar.MONTH, monthOfYear);
                    myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel();
                }

            };

            edittext.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(ReportsActivity.this, date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            Button btnViewReport = (Button) findViewById(R.id.btnViewReport);
            btnViewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(_sessionManager.GetReportType().equals("passenger"))
                        new mySQLPassengerCountReport(getApplicationContext(), ReportsActivity.this).execute(reportDate.getText().toString(), reportTerminal.getText().toString());
                    else
                        new mySQLEcoloopCount(getApplicationContext(), ReportsActivity.this).execute(reportDate.getText().toString());
                }
            });

        }
        catch(Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }

    }
    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        reportDate.setText(sdf.format(myCalendar.getTime()));
    }

}
