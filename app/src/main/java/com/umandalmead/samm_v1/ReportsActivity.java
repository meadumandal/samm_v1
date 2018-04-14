package com.umandalmead.samm_v1;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportsActivity extends Fragment {
    public Calendar myCalendar = Calendar.getInstance();
    public static String TAG="mead";
    public EditText reportDate;
    public AutoCompleteTextView reportTerminal;
    public static SessionManager _sessionManager;

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            myView = inflater.inflate(R.layout.activity_reports, container, false);

            reportDate = (EditText) myView.findViewById(R.id.reportDate);
//            reportTerminal = (AutoCompleteTextView) myView.findViewById(R.id.reportTerminal);
            this._sessionManager = new SessionManager(getContext());
//            if (_sessionManager.GetReportType() == "ecoloop")
//            {
////                AutoCompleteTextView txtViewTerminal = (AutoCompleteTextView)myView.findViewById(R.id.reportTerminal);
////                txtViewTerminal.setVisibility(View.GONE);
//            }


//            List<String> array = new ArrayList<>();
//
//            ArrayAdapter<String> adapter;
//
//            for(Terminal _drawable:MenuActivity._terminalList)
//            {
//                array.add(_drawable.Value);
//            }
//            adapter = new ArrayAdapter<>(getActivity(),android.R.layout.simple_list_item_1, array);
//
//            reportTerminal.setThreshold(1);
//            reportTerminal.setAdapter(adapter);



            EditText edittext= (EditText) myView.findViewById(R.id.reportDate);
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
                    new DatePickerDialog(getActivity(), date, myCalendar
                            .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                            myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            Button btnViewReport = (Button) myView.findViewById(R.id.btnViewReport);
            btnViewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(_sessionManager.GetReportType().equals("passenger"))
                        new mySQLPassengerCountReport(getContext(), getActivity()).execute(reportDate.getText().toString(),"");
                    else
                        new mySQLEcoloopCount(getContext(), getActivity()).execute(reportDate.getText().toString());
                }
            });

        }
        catch(Exception ex)
        {
            Log.e(TAG, ex.getMessage());
        }

        return myView;
    }

    private void updateLabel() {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        reportDate.setText(sdf.format(myCalendar.getTime()));
    }

}
