package com.evolve.evx;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.evolve.evx.EntityObjects.Lines;
import com.evolve.evx.EntityObjects.Terminal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import io.supercharge.shimmerlayout.ShimmerLayout;

import static com.evolve.evx.MenuActivity._activity;

public class ReportsActivity extends Fragment {

    public Calendar _calendar = Calendar.getInstance();
    public EditText
            _fromDateTextBox,
            _toDateTextBox,
            _distanceSpeed_fromDataTextBox,
            _distanceSpeed_toDateTextBox;
    public TextView
            _reportName;
    public static TableLayout
            _vehicleReportTable,
            _passengerReportTable_summary,
            _passengerReportTable_history;
    public static ScrollView _scrollVehicleReport;
    public static SessionManager _sessionManager;
    public Constants _constants;
    public static LinearLayout _initialReportLayout,
            _vehicleTripsReportFilters;
    public Spinner
            _spinnerLines,
            _spinnerterminal;
    public static ImageView _SAMMLogoFAB;
    private AppBarLayout
            _ReportsAppBar,
            _appbar_vehiclerounds_reportfilters,
            _appbar_distancespeed_reportfilters;
    private Button _btn_CreateReport;
    private LinearLayout _LL_create_button_holder;
    private ShimmerLayout _SL_btn_create_report;
    private TextView _TV_ActivityTitle, _TV_ReportSubTitle;
    public static PieChart _PC_EcoLoopMain, _PC_EcoLoopMaxSpeed, _PC_NumberOfRounds;
    public static Button _BTN_BACK_EcoLoop_Others;
    public static TabLayout _TL_EcoloopTraveled;
    public static RelativeLayout _RL_DistanceTraveled;
    private View _view;
    private ImageButton _btnHideReportFilters, _btnHideDistanceReportFilters;
    public static ImageView _IV_ExportReport;
    private Helper _helper;
    public static  LinearLayout.LayoutParams _RoundsReportAppBarLayout;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            _view = inflater.inflate(R.layout.activity_reports, container, false);
            _reportName = _view.findViewById(R.id.textViewReportName);
            _fromDateTextBox = _view.findViewById(R.id.fromDate);
            _toDateTextBox = _view.findViewById(R.id.toDate);
            _distanceSpeed_fromDataTextBox = (EditText) _view.findViewById(R.id.distancespeed_fromDate);
            _distanceSpeed_toDateTextBox = (EditText) _view.findViewById(R.id.distanceSpeed_toDate);
            _IV_ExportReport = _view.findViewById(R.id.IV_ExportButton);
            _constants = new Constants();

            _SAMMLogoFAB = _view.findViewById(R.id.SAMMLogoFAB);
            _ReportsAppBar = _view.findViewById(R.id.reportsBarLayout);
            _appbar_vehiclerounds_reportfilters = (AppBarLayout) _view.findViewById(R.id.appbar_vehiclerounds_reportsfilter);
            _appbar_distancespeed_reportfilters = _view.findViewById(R.id.appbar_reportsfilter_distancespeed);
            _btn_CreateReport = (Button) _view.findViewById(R.id.btnCreateReport);
            _LL_create_button_holder = (LinearLayout) _view.findViewById(R.id.LL_create_button_holder);
            _SL_btn_create_report = (ShimmerLayout) _view.findViewById(R.id.SL_btn_Create_Report);
            _TV_ActivityTitle = (TextView) _view.findViewById(R.id.TV_ReportTitle);
            _TV_ReportSubTitle = (TextView) _view.findViewById(R.id.textViewReportName);
            _PC_EcoLoopMain = (PieChart) _view.findViewById(R.id.PC_EcoLoopMainPieChart);
            _PC_NumberOfRounds = _view.findViewById(R.id.vehicleTripsPieChart);
            _BTN_BACK_EcoLoop_Others = (Button) _view.findViewById(R.id.BTN_back);
            _TL_EcoloopTraveled = (TabLayout) _view.findViewById(R.id.TL_EcoloopTraveled);
            _RL_DistanceTraveled = (RelativeLayout) _view.findViewById(R.id.RL_DistanceTraveled);
            _SL_btn_create_report.startShimmerAnimation();
            _spinnerterminal = _view.findViewById(R.id.spinner_terminal);
            _spinnerLines = _view.findViewById(R.id.spinner_lines);

            _fromDateTextBox.setTypeface(Helper.FONT_RUBIK_REGULAR);
            _toDateTextBox.setTypeface(Helper.FONT_RUBIK_REGULAR);


            _RoundsReportAppBarLayout = (LinearLayout.LayoutParams) _appbar_vehiclerounds_reportfilters.getLayoutParams();

            _TV_ActivityTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
            _TV_ReportSubTitle.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
            this._sessionManager = new SessionManager(getContext());
            this._helper = new Helper(_activity, getContext());
            _btnHideReportFilters = _view.findViewById(R.id.btn_hidevehicleroundsreportfilters);
            _btnHideDistanceReportFilters = _view.findViewById(R.id.btn_hidedistancereportfilters);
            _btnHideDistanceReportFilters.setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View view) {
                    if (_appbar_distancespeed_reportfilters.getVisibility()==View.VISIBLE)
                    {
                        _appbar_distancespeed_reportfilters.animate()
                                .alpha(0.0f)
                                .setDuration(350)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        _appbar_distancespeed_reportfilters.setVisibility(View.GONE);
                                    }
                                });
                        _btnHideDistanceReportFilters.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                    }
                    else {
                        _appbar_distancespeed_reportfilters.setVisibility(View.VISIBLE);
                        _btnHideDistanceReportFilters.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                    }
                }
            });
            _btnHideReportFilters.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (_appbar_vehiclerounds_reportfilters.getVisibility()==View.VISIBLE)
                    {
                        _appbar_vehiclerounds_reportfilters.animate()
                                .alpha(0.0f)
                                .setDuration(350)
                                .setListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {
                                        _appbar_vehiclerounds_reportfilters.setVisibility(View.GONE);
                                    }
                                });
                        _btnHideReportFilters.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);
                    }
                    else {
                        _appbar_vehiclerounds_reportfilters.setVisibility(View.VISIBLE);
                       // _RoundsReportAppBarLayout.height= LinearLayout.LayoutParams.WRAP_CONTENT;
                        //_appbar_vehiclerounds_reportfilters.setLayoutParams(_RoundsReportAppBarLayout);
                        _btnHideReportFilters.setImageResource(R.drawable.ic_arrow_drop_up_white_24dp);
                    }
                }
            });

            //Tutorials
            if(_sessionManager.GetReportType().equals(_constants.DISTANCE_SPEED_REPORT)) {
                if (!_sessionManager.getEcoLoopReportTutorialStatus()) {
                    TutorialDialog ReportTutorial = new TutorialDialog(_activity,
                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_create_title)
                                ,MenuActivity._GlobalResource.getString(R.string.TUT_generate_title)},
                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_create_report_inst)
                                ,MenuActivity._GlobalResource.getString(R.string.TUT_report_distance_filter_inst)},
                            new Integer[]{R.drawable.tut_createreport
                                    ,R.drawable.tut_ecoloopfilter});
                    ReportTutorial.show();
                    _sessionManager.TutorialStatus(Enums.UIType.REPORT_ECOLOOP, true);
                }
            }else{
                if(!_sessionManager.getRoundsReportTutorialStatus()) {
                    TutorialDialog ReportTutorial = new TutorialDialog(_activity,
                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_create_title)
                                    ,MenuActivity._GlobalResource.getString(R.string.TUT_generate_title)},
                            new String[]{MenuActivity._GlobalResource.getString(R.string.TUT_create_report_inst)
                                    ,MenuActivity._GlobalResource.getString(R.string.TUT_report_rounds_filter_inst)},
                            new Integer[]{R.drawable.tut_createreport
                                    ,R.drawable.tut_roundsreportfilters});
                    ReportTutorial.show();
                    _sessionManager.TutorialStatus(Enums.UIType.REPORT_ROUNDS, true);
                }
            }


            _SAMMLogoFAB.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
            _btn_CreateReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try
                    {
                        if(_sessionManager.GetReportType().equals(_constants.DISTANCE_SPEED_REPORT))
                        {
                            _appbar_distancespeed_reportfilters.setVisibility(View.VISIBLE);
                            _LL_create_button_holder.setVisibility(View.GONE);
                            _SL_btn_create_report.stopShimmerAnimation();
                            _btnHideDistanceReportFilters.setVisibility(View.VISIBLE);
                        }
                        else if(_sessionManager.GetReportType().equals(_constants.VEHICLE_ROUNDS_REPORT)) {
                            _LL_create_button_holder.setVisibility(View.GONE);
                            _SL_btn_create_report.stopShimmerAnimation();
                            _appbar_vehiclerounds_reportfilters.setVisibility(View.VISIBLE);
                            _btnHideReportFilters.setVisibility(View.VISIBLE);
                        }

                    }
                    catch(Exception ex)
                    {
                        Helper.logger(ex);
                    }

                }
            });
            List<Lines> spinnerLinesValue =new ArrayList<>();
            spinnerLinesValue.add(new Lines(0, "Select a line", 0,""));
            spinnerLinesValue.addAll(MenuActivity._lineList);

            CustomAdapterForValueDisplayPair lineIDValuePairCustomAdapter = new CustomAdapterForValueDisplayPair(
                    getContext(),
                    R.layout.value_display_list_item,
                    spinnerLinesValue
            );
            _spinnerLines.setAdapter(lineIDValuePairCustomAdapter);

            // Set an item selection listener for spinner widget
            _spinnerLines.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Set the value for selected index variable
                    if (i!=0)
                    {
                        TextView textview_lineID = view.findViewById(R.id.textview_value);
                        Integer selectedLineID = Integer.parseInt(textview_lineID.getText().toString());
                        List<Terminal> filteredTerminals = new ArrayList<>();
                        filteredTerminals.add(new Terminal(0, 0, "selectstation", "Select a station", 0, "", 0.0, 0.0, "", null, 0, "", "", "", 0,0.0));
                        for(Terminal terminal: MenuActivity._terminalList)
                        {
                            if (terminal.getLineID() == selectedLineID) {
                                Boolean isExists = false;
                                for (Terminal filteredTerminal : filteredTerminals) {
                                    if (filteredTerminal.getValue().equalsIgnoreCase(terminal.getValue())) {
                                        isExists = true;
                                        break;
                                    }
                                }
                                if (!isExists)
                                    filteredTerminals.add(terminal);
                            }
                        }
                        CustomAdapterForValueDisplayPair stationIDValuePairCustomAdapter = new CustomAdapterForValueDisplayPair(
                                getContext(),
                                R.layout.value_display_list_item,
                                filteredTerminals);
                        _spinnerterminal.setEnabled(true);
                        _spinnerterminal.setAdapter(stationIDValuePairCustomAdapter);
                        _spinnerterminal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                                // Set the value for selected index variable
                                if (i!=0)
                                {

                                }

                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {

                            }
                        });
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });



            if(_sessionManager.GetReportType().equals(_constants.DISTANCE_SPEED_REPORT))
            {
                _reportName.setText(MenuActivity._GlobalResource.getString(R.string.title_total_distance));
            } else if (_sessionManager.GetReportType().equals(_constants.PASSENGER_ACTIVITY_REPORT)) {
                _reportName.setText(MenuActivity._GlobalResource.getString(R.string.title_vehicle_rounds_report));
            } else if (_sessionManager.GetReportType().equals(_constants.VEHICLE_ROUNDS_REPORT))
            {
                _reportName.setText(MenuActivity._GlobalResource.getString(R.string.title_vehicle_rounds_report));

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
                    if (_sessionManager.GetReportType().equals(_constants.VEHICLE_ROUNDS_REPORT))
                        updateLabel("to");
                    else
                        updateLabel("todistance");
                }

            };

            _distanceSpeed_toDateTextBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(getActivity(), toDate, _calendar
                            .get(Calendar.YEAR), _calendar.get(Calendar.MONTH),
                            _calendar.get(Calendar.DAY_OF_MONTH)).show();
                }

            });
            _distanceSpeed_fromDataTextBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new DatePickerDialog(getActivity(), fromDate, _calendar
                            .get(Calendar.YEAR), _calendar.get(Calendar.MONTH),
                            _calendar.get(Calendar.DAY_OF_MONTH)).show();
                }
            });
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
            Button btnViewDistancespeedReport = _view.findViewById(R.id.btnViewDistanceSpeedReport);
            btnViewDistancespeedReport.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    try {
                        if (_distanceSpeed_fromDataTextBox.getText().length() > 0 && _distanceSpeed_toDateTextBox.getText().length() > 0) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                            if (dateFormat.parse(_distanceSpeed_fromDataTextBox.getText().toString()).after(dateFormat.parse(_distanceSpeed_toDateTextBox.getText().toString()))) {
                                ErrorDialog errorDialog = new ErrorDialog(_activity, "Invalid date range");
                                errorDialog.show();
                            }else {
                                _TL_EcoloopTraveled.setVisibility(View.VISIBLE);
                                _RL_DistanceTraveled.setVisibility(View.VISIBLE);
                                new asyncEcoloopKMTraveled(getContext(), getActivity()).execute(_distanceSpeed_fromDataTextBox.getText().toString(), _distanceSpeed_toDateTextBox.getText().toString());
                            }
                        } else {
                            ErrorDialog errorDialog = new ErrorDialog(_activity, "Please supply all filters");
                            errorDialog.show();
                        }
                    }catch (Exception ex){
                        Helper.logger(ex);
                    }
                }
            });

            btnViewReport.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try {
                        _TL_EcoloopTraveled.setVisibility(View.GONE);
                        _RL_DistanceTraveled.setVisibility(View.GONE);
                        Lines selectedLineInSpinner = ((Lines) _spinnerLines.getSelectedItem());
                        Terminal selectedTerminalInSpinner = (Terminal) _spinnerterminal.getSelectedItem();
                        Integer lineID = selectedLineInSpinner.getID();
                        String stationValue = selectedTerminalInSpinner==null ? "" : selectedTerminalInSpinner.getValue();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                        Date from, to;
                        String strFrom, strTo;
                        strFrom = _fromDateTextBox.getText().toString().trim();
                        strTo = _toDateTextBox.getText().toString().trim();


                        if (lineID == 0
                                && stationValue.isEmpty()
                                && _fromDateTextBox.getText().toString().isEmpty()
                                && _toDateTextBox.getText().toString().isEmpty()) {
                            ErrorDialog errorDialog = new ErrorDialog(_activity, "Please supply all filters");
                            errorDialog.show();
                        }
                        else if (strFrom.isEmpty() || strTo.isEmpty())
                        {
                            ErrorDialog errorDialog = new ErrorDialog(_activity, "Invalid date range");
                            errorDialog.show();
                        }
                        else if  (dateFormat.parse(_fromDateTextBox.getText().toString()).after(dateFormat.parse(_toDateTextBox.getText().toString()))) {
                            ErrorDialog errorDialog = new ErrorDialog(_activity, "Invalid date range");
                            errorDialog.show();
                        }
                        else
                        {
                            _appbar_vehiclerounds_reportfilters.setVisibility(View.GONE);
                            _btnHideReportFilters.setImageResource(R.drawable.ic_arrow_drop_down_white_24dp);

                            _PC_NumberOfRounds.setEntryLabelColor(Color.BLACK);
                            _PC_NumberOfRounds.setEntryLabelTypeface(Helper.FONT_RUBIK_BOLD);
                            _PC_NumberOfRounds.setEntryLabelTextSize(12f);
                            _PC_NumberOfRounds.setNoDataText("No data available");
                            _PC_NumberOfRounds.setNoDataTextTypeface(Helper.FONT_RUBIK_REGULAR);
                            _PC_NumberOfRounds.animate();
                            _PC_NumberOfRounds.setCenterTextTypeface(Helper.FONT_RUBIK_BOLD);
                            _PC_NumberOfRounds.setCenterText("No. of trips from " + selectedTerminalInSpinner.getDescription()
                                    + " and back\n"
                                    + _fromDateTextBox.getText().toString()
                                    + " - "
                                    + _toDateTextBox.getText().toString());

                            _PC_NumberOfRounds.setHoleRadius(50f);
                            _PC_NumberOfRounds.setTransparentCircleRadius(50f);
                            _PC_NumberOfRounds.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
                                @Override
                                public void onValueSelected(Entry e, Highlight h) {

                                }

                                @Override
                                public void onNothingSelected() {

                                }
                            });
                            new mySQLVehicleGeofenceHistoryReport(getContext(), getActivity()).
                                    execute(lineID.toString(),
                                            stationValue.toLowerCase(),
                                            _fromDateTextBox.getText().toString(),
                                            _toDateTextBox.getText().toString());


                        }
                    } catch (Exception ex) {
                        Helper.logger(ex);
                    }


                }
            });

        } catch (Exception ex) {
            Helper.logger(ex, true);
        }

        return _view;
    }


    private void updateLabel(String dateType) {
        String myFormat = "MM/dd/yy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);
        if (dateType.equals("from")) {
            if(_sessionManager.GetReportType().equals(_constants.VEHICLE_ROUNDS_REPORT))
                _fromDateTextBox.setText(sdf.format(_calendar.getTime()));
            else
                _distanceSpeed_fromDataTextBox.setText(sdf.format(_calendar.getTime()));
        }
        else {
            if(_sessionManager.GetReportType().equals(_constants.VEHICLE_ROUNDS_REPORT))
                _toDateTextBox.setText(sdf.format(_calendar.getTime()));
            else
                _distanceSpeed_toDateTextBox.setText(sdf.format(_calendar.getTime()));

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {

                    // permission denied,Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

}
