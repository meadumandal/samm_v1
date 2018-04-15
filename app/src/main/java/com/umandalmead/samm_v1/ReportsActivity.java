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
import android.widget.TableLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.google.android.gms.vision.barcode.Barcode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ReportsActivity extends Fragment {
    public Calendar _calendar = Calendar.getInstance();
    public EditText _fromDateTextBox, _toDateTextBox;
    public TextView _reportName;
    public static TableLayout _reportTable;
    public static BarChart _reportChart;
    public AutoCompleteTextView _terminalAutoComplete;
    public static SessionManager _sessionManager;
    public Constants _constants;

    View _view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            _view = inflater.inflate(R.layout.activity_reports, container, false);
            _reportName = (TextView) _view.findViewById(R.id.textViewReportName);
            _fromDateTextBox = (EditText) _view.findViewById(R.id.fromDate);
            _toDateTextBox = (EditText) _view.findViewById(R.id.toDate);
            _constants = new Constants();
            this._sessionManager = new SessionManager(getContext());

            _reportTable = (TableLayout) _view.findViewById(R.id.reportTable);
            _reportChart = (BarChart) _view.findViewById(R.id.reportChart);

            if(_sessionManager.GetReportType().equals(_constants.VEHICLE_REPORT_TYPE))
            {
                _reportName.setText("REPORTS: Total distance traveled");
            }
            else if(_sessionManager.GetReportType().equals(_constants.PASSENGER_REPORT_TYPE))
            {
                _reportName.setText("REPORTS: Ave. no. of waiting passengers");
            }
            final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    _calendar.set(Calendar.YEAR, year);
                    _calendar.set(Calendar.MONTH, monthOfYear);
                    _calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel("from");
                }

            };
            final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker view, int year, int monthOfYear,
                                      int dayOfMonth) {
                    // TODO Auto-generated method stub
                    _calendar.set(Calendar.YEAR, year);
                    _calendar.set(Calendar.MONTH, monthOfYear);
                    _calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                    updateLabel("to");
                }

            };

//            _terminalAutoComplete = (AutoCompleteTextView) _view.findViewById(R.id._terminalAutoComplete);
//            if (_sessionManager.GetReportType() == "ecoloop")
//            {
////                AutoCompleteTextView txtViewTerminal = (AutoCompleteTextView)_view.findViewById(R.id._terminalAutoComplete);
////                txtViewTerminal.setVisibility(View.GONE);
//            }
//
//
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
//            _terminalAutoComplete.setThreshold(1);
//            _terminalAutoComplete.setAdapter(adapter);
            _fromDateTextBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(getActivity(), fromDate, _calendar
                            .get(Calendar.YEAR), _calendar.get(Calendar.MONTH),
                            _calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            _toDateTextBox.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    // TODO Auto-generated method stub
                    new DatePickerDialog(getActivity(), toDate, _calendar
                            .get(Calendar.YEAR), _calendar.get(Calendar.MONTH),
                            _calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
            Button btnViewReport = (Button) _view.findViewById(R.id.btnViewReport);
            btnViewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(_sessionManager.GetReportType().equals(_constants.PASSENGER_REPORT_TYPE))
                        new mySQLPassengerCountReport(getContext(), getActivity()).execute(_fromDateTextBox.getText().toString(),"");
                    else if(_sessionManager.GetReportType().equals(_constants.VEHICLE_REPORT_TYPE))
                        new asyncEcoloopKMTraveled(getContext(), getActivity()).execute(_fromDateTextBox.getText().toString(), _toDateTextBox.getText().toString());
                }
            });

        }
        catch(Exception ex)
        {
            Log.e(_constants.LOG_TAG, ex.getMessage());
        }

        return _view;
    }

    private void updateLabel(String dateType) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        if (dateType.equals("from"))
            _fromDateTextBox.setText(sdf.format(_calendar.getTime()));
        else
            _toDateTextBox.setText(sdf.format(_calendar.getTime()));
    }

}
