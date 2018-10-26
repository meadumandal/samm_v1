package com.umandalmead.samm_v1;

import android.app.DatePickerDialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.INotificationSideChannel;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.umandalmead.samm_v1.EntityObjects.Terminal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import io.supercharge.shimmerlayout.ShimmerLayout;

public class ReportsActivity extends Fragment {
    public Calendar _calendar = Calendar.getInstance();
    public EditText _fromDateTextBox, _toDateTextBox;
    public TextView _reportName, _initialTextView;
    public static TableLayout _vehicleReportTable, _passengerReportTable_summary, _passengerReportTable_history;
    public static ScrollView _scrollVehicleReport;
    public static BarChart _reportChart;
    public static SessionManager _sessionManager;
    public Constants _constants;
    public static LinearLayout _initialReportLayout,_layoutTerminalSelection;
    private int _selectedIndex;
    public Spinner _spinner;
    public static ImageView _SAMMLogoFAB;
    private AppBarLayout _ReportsAppBar, _AppBar_ReportActions;
    private Button _btn_CreateReport;
    private LinearLayout _LL_create_button_holder;
    private ShimmerLayout _SL_btn_create_report;

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
            _scrollVehicleReport = (ScrollView) _view.findViewById(R.id.scrollVehicleReport);
            _SAMMLogoFAB = (ImageView) _view.findViewById(R.id.SAMMLogoFAB);
            _ReportsAppBar = (AppBarLayout) _view.findViewById(R.id.reportsBarLayout);
            _AppBar_ReportActions = (AppBarLayout) _view.findViewById(R.id.AppBar_ReportActions);
            _btn_CreateReport = (Button) _view.findViewById(R.id.btnCreateReport);
            _LL_create_button_holder = (LinearLayout) _view.findViewById(R.id.LL_create_button_holder);
            _spinner = (Spinner) _view.findViewById(R.id.spinner_terminal);
            _SL_btn_create_report = (ShimmerLayout) _view.findViewById(R.id.SL_btn_Create_Report);
            _SL_btn_create_report.startShimmerAnimation();

            _SAMMLogoFAB.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
            _btn_CreateReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    _AppBar_ReportActions.setVisibility(View.VISIBLE);
                    _LL_create_button_holder.setVisibility(View.GONE);
                    _SL_btn_create_report.stopShimmerAnimation();
                }
            });
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



            _vehicleReportTable = (TableLayout) _view.findViewById(R.id.reportTable);
            _reportChart = (BarChart) _view.findViewById(R.id.reportChart);

            if(_sessionManager.GetReportType().equals(_constants.VEHICLE_REPORT_TYPE))
            {
                _reportName.setText(MenuActivity._GlobalResource.getString(R.string.title_total_distance));
                _layoutTerminalSelection.setVisibility(View.GONE);
                _initialTextView.setText(MenuActivity._GlobalResource.getString(R.string.info_please_select_date_range));
            }
            else if(_sessionManager.GetReportType().equals(_constants.PASSENGER_REPORT_TYPE))
            {
                _reportName.setText(MenuActivity._GlobalResource.getString(R.string.title_passenger_queueing_history));
                _layoutTerminalSelection.setVisibility(View.VISIBLE);
                _initialTextView.setText(MenuActivity._GlobalResource.getString(R.string.info_please_select_date_range_with_terminal));
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
                        if(_fromDateTextBox.getText().length() >0 && _toDateTextBox.getText().length()>0 && _spinner.getSelectedItemPosition() >0)
                        {
                            _passengerReportTable_history.setVisibility(View.VISIBLE);
                            _passengerReportTable_summary.setVisibility(View.VISIBLE);
                            _initialReportLayout.setVisibility(View.GONE);
                            _vehicleReportTable.setVisibility(View.GONE);
                            _scrollVehicleReport.setVisibility(View.GONE);
                            String terminal = "";
                            for(Terminal t: MenuActivity._terminalList)
                            {
                                if (t.Description.equals(_spinner.getSelectedItem().toString()))
                                {
                                    terminal = t.Value;
                                    break;
                                }
                            }

                            new mySQLPassengerQueueingHistoryReport(getContext(), getActivity()).execute(_fromDateTextBox.getText().toString(),_toDateTextBox.getText().toString(), terminal);
                        }
                        else
                        {
                            Toast.makeText(getContext(), MenuActivity._GlobalResource.getString(R.string.error_please_supply_all_fields), Toast.LENGTH_LONG).show();
                        }


                    }

                    else if(_sessionManager.GetReportType().equals(_constants.VEHICLE_REPORT_TYPE))
                    {
                        if(_fromDateTextBox.getText().length() >0 && _toDateTextBox.getText().length()>0)
                        {
                            _passengerReportTable_history.setVisibility(View.GONE);
                            _passengerReportTable_summary.setVisibility(View.GONE);
                            _scrollVehicleReport.setVisibility(View.VISIBLE);
                            _vehicleReportTable.setVisibility(View.VISIBLE);
                            _initialReportLayout.setVisibility(View.GONE);

                            new asyncEcoloopKMTraveled(getContext(), getActivity()).execute(_fromDateTextBox.getText().toString(), _toDateTextBox.getText().toString());
                        }
                        else
                        {
                            Toast.makeText(getContext(), MenuActivity._GlobalResource.getString(R.string.error_please_supply_all_fields), Toast.LENGTH_LONG).show();
                        }

                    }

                }
            });

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
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
