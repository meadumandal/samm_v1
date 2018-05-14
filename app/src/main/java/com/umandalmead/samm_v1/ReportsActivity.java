package com.umandalmead.samm_v1;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.umandalmead.samm_v1.EntityObjects.Terminal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ReportsActivity extends Fragment {
    public Calendar _calendar = Calendar.getInstance();
    public EditText _fromDateTextBox, _toDateTextBox;
    public TextView _reportName, _initialTextView;
    public static TableLayout _reportTable, _passengerReportTable_summary, _passengerReportTable_history;
    public static BarChart _reportChart;
    public static SessionManager _sessionManager;
    public Constants _constants;
    public static LinearLayout _initialReportLayout,_layoutTerminalSelection;
    private int _selectedIndex;
    public Spinner _spinner;

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
            _initialReportLayout = (LinearLayout) _view.findViewById(R.id.initialReportLayout);
            _layoutTerminalSelection = (LinearLayout) _view.findViewById(R.id.layout_terminalSelection);
            _initialTextView = (TextView) _view.findViewById(R.id.textView_initialText);
            _passengerReportTable_history = (TableLayout) _view.findViewById(R.id.passengerReportTable_history);
            _passengerReportTable_summary = (TableLayout) _view.findViewById(R.id.passengerReportTable_summary);


            _spinner = (Spinner) _view.findViewById(R.id.spinner_terminal);
            this._sessionManager = new SessionManager(getContext());
            List<String> terminals = new ArrayList<>();
            terminals.add("--ALL--");
            for (Terminal t : MenuActivity._terminalList)
            {
                terminals.add(t.Description);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, terminals)
            {
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Cast the spinner collapsed item (non-popup item) as a text view
                    TextView tv = (TextView) super.getView(position, convertView, parent);

                    // Set the text color of spinner item
                    tv.setTextColor(Color.WHITE);

                    // Return the view
                    return tv;
                }

                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent){
                    // Cast the drop down items (popup items) as text view
                    TextView tv = (TextView) super.getDropDownView(position,convertView,parent);

                    // Set the text color of drop down items
                    tv.setTextColor(Color.BLACK);
                    tv.setTypeface(Typeface.DEFAULT);

                    // If this item is selected item
                    if(position == _selectedIndex){
                        // Set spinner selected popup item's text color
                        // tv.setTextColor(Color.BLUE);
                        tv.setTypeface(Typeface.DEFAULT_BOLD);
                    }

                    // Return the modified view
                    return tv;
                }
            };
            // Set an item selection listener for spinner widget
            _spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Set the value for selected index variable
                    _selectedIndex = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            _spinner.setAdapter(adapter);
            _spinner.setPrompt("SELECT A TERMINAL");



            _reportTable = (TableLayout) _view.findViewById(R.id.reportTable);
            _reportChart = (BarChart) _view.findViewById(R.id.reportChart);

            if(_sessionManager.GetReportType().equals(_constants.VEHICLE_REPORT_TYPE))
            {
                _reportName.setText("REPORTS: Total distance traveled");
                _layoutTerminalSelection.setVisibility(View.GONE);
                _initialTextView.setText("Please select date range and click 'View' button");
            }
            else if(_sessionManager.GetReportType().equals(_constants.PASSENGER_REPORT_TYPE))
            {
                _reportName.setText("REPORTS: Passenger Queueing History");
                _layoutTerminalSelection.setVisibility(View.VISIBLE);
                _initialTextView.setText("Please select date range, terminal and click 'View' button");
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
                    {
                        _passengerReportTable_history.setVisibility(View.VISIBLE);
                        _passengerReportTable_summary.setVisibility(View.VISIBLE);
                        _initialReportLayout.setVisibility(View.GONE);
                        _reportTable.setVisibility(View.GONE);

                        new mySQLPassengerCountReport(getContext(), getActivity()).execute(_fromDateTextBox.getText().toString(),_toDateTextBox.getText().toString(), _spinner.getSelectedItem().toString());
                    }

                    else if(_sessionManager.GetReportType().equals(_constants.VEHICLE_REPORT_TYPE))
                    {
                        _passengerReportTable_history.setVisibility(View.GONE);
                        _passengerReportTable_summary.setVisibility(View.GONE);
                        _reportTable.setVisibility(View.VISIBLE);
                        _initialReportLayout.setVisibility(View.GONE);

                        new asyncEcoloopKMTraveled(getContext(), getActivity()).execute(_fromDateTextBox.getText().toString(), _toDateTextBox.getText().toString());
                    }

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
