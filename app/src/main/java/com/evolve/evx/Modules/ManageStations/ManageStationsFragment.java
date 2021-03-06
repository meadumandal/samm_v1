package com.evolve.evx.Modules.ManageStations;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ListFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.evolve.evx.Constants;
import com.evolve.evx.EntityObjects.Terminal;
import com.evolve.evx.Enums;
import com.evolve.evx.ErrorDialog;
import com.evolve.evx.Helper;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;
import com.evolve.evx.TouchInterceptor;
import com.evolve.evx.YesNoDialog;

import java.util.Arrays;

/**
 * Created by eleazerarcilla on 01/07/2018.
 */

public class ManageStationsFragment extends ListFragment {
    private View myView;
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    public LoaderDialog _LoaderDialog;
    public Terminal[] originalPointsArray;
    public Boolean isItTheSame;
    public String[] pointsArrayInString;
    Helper _helper;
    View _myView;
    ManageStationsFragment _manageStationsFragment;
    Activity _activity;
    SessionManager _sessionManager;

    public ManageStationsFragment() {

    }

    private ImageView BtnAddPoint;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _manageStationsFragment = this;
        _helper = new Helper(getActivity(), getContext());
        _sessionManager = new SessionManager(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        _myView = inflater.inflate(R.layout.activity_addpoints, container, false);

        try {
            Integer routeID = 0;
            Bundle bundle = getArguments();
            if (bundle != null) {
                routeID = bundle.getInt("routeID");
                MenuActivity._FragmentTitle = _helper.GetRouteObjectByID(routeID).getRouteName();
            }

            _helper = new Helper();
            isItTheSame = true;
            int index = 0;

            _helper.FilterStationsForManageStationsModule(routeID);

            originalPointsArray = Arrays.copyOf(MenuActivity._PointsArray, MenuActivity._PointsArray.length);
            pointsArrayInString = new String[MenuActivity._PointsArray.length];
            int ctr = 0;
            for (Terminal t : MenuActivity._PointsArray) {
                pointsArrayInString[ctr] = t.getValue();
                ctr++;
            }


            ArrayAdapter adp = new ArrayAdapter(getContext(), R.layout.listview_viewpoints, MenuActivity._PointsArray);
            setListAdapter(adp);
            InitializeToolbar(MenuActivity._FragmentTitle);

            _LoaderDialog = new LoaderDialog(getActivity(), MenuActivity._GlobalResource.getString(R.string.GPS_adding_vehicle_gps), MenuActivity._GlobalResource.getString(R.string.dialog_initialize_with_ellipsis));
            _LoaderDialog.setCancelable(false);
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
        return _myView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        try {
            mList = (TouchInterceptor) getListView();
            if (_sessionManager.getIsAdmin()) {

                mList.setDropListener(mDropListener);


            }
            registerForContextMenu(mList);

        } catch (Exception ex) {
            _helper.logger(ex);
        }

    }


    public void HandleChangesInOrderOfPoints() {
        if (checkIfOrderIsModified(originalPointsArray, MenuActivity._PointsArray) == true) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(MenuActivity._GlobalResource.getString(R.string.info_points_modified))
                    .setMessage(MenuActivity._GlobalResource.getString(R.string.info_confirm_save_changes))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                LoaderDialog PointsUpdateLoader = new LoaderDialog(getActivity(), MenuActivity._GlobalResource.getString(R.string.dialog_updating_order_of_points), MenuActivity._GlobalResource.getString(R.string.dialog_please_wait_with_ellipsis));
                                PointsUpdateLoader.setCancelable(false);
                                String pointsArray = Arrays.toString(pointsArrayInString);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                new mySQLUpdateStationOrder(getContext(), getActivity(), PointsUpdateLoader, alertDialogBuilder).execute(pointsArray.substring(1, pointsArray.length() - 1), String.valueOf(MenuActivity._currentRouteIDSelected));

                                originalPointsArray = Arrays.copyOf(MenuActivity._PointsArray, MenuActivity._PointsArray.length);


                                //                                    Intent routeIntent = new Intent(ManageStationsFragment.this, ManageRoutesFragment.class);
                                //                                    startActivity(routeIntent);
                                //                                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                                //                                    finish();
                            } catch (Exception ex) {
                                Helper.logger(ex, true);
                            }


                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            _manageStationsFragment.getFragmentManager().beginTransaction().replace(R.id.content_frame, MenuActivity._manageRoutesFragment)
                                    .addToBackStack(Constants.FRAGMENTNAME_MANAGEROUTES)
                                    .commit();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        } else {
            _manageStationsFragment.getFragmentManager().beginTransaction().replace(R.id.content_frame, MenuActivity._manageRoutesFragment)
                    .addToBackStack(Constants.FRAGMENTNAME_MANAGEROUTES)
                    .commit();
        }


    }

    public void InitializeToolbar(String fragmentName) {
        SessionManager sessionManager = new SessionManager(MenuActivity._context);
        FAB_SammIcon = (ImageButton) _myView.findViewById(R.id.SAMMLogoFAB);
        FAB_SammIcon.setImageResource(R.drawable.ic_back_left_arrow);
        FAB_SammIcon.getLayoutParams().height = 60;
        FAB_SammIcon.getLayoutParams().width = 60;
        BtnAddPoint = (ImageView) _myView.findViewById(R.id.topnav_addButton);
        if (sessionManager.getIsAdmin())
            BtnAddPoint.setVisibility(View.VISIBLE);
        else
            BtnAddPoint.setVisibility(View.GONE);
        BtnAddPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    AddNewStationPoint("Add");
                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HandleChangesInOrderOfPoints();
                } catch (Exception ex) {
                    Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
        ViewTitle = (TextView) _myView.findViewById(R.id.samm_toolbar_title);
        ViewTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
        ViewTitle.setText(fragmentName);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Terminal selection = MenuActivity._PointsArray[position];
        final PopupMenu popup = new PopupMenu(getContext(), v, Gravity.RIGHT);
        popup.getMenuInflater().inflate(R.menu.popup_station_actions, popup.getMenu());

        if (_sessionManager.getIsAdmin()) {
            popup.getMenu().findItem(R.id.itemView).setVisible(true);
            popup.getMenu().findItem(R.id.itemDelete).setVisible(true);
            popup.getMenu().findItem(R.id.itemEdit).setVisible(true);
        } else {
            popup.getMenu().findItem(R.id.itemView).setVisible(true);
            popup.getMenu().findItem(R.id.itemDelete).setVisible(false);
            popup.getMenu().findItem(R.id.itemEdit).setVisible(false);
        }
        popup.show();
        AttachPopupEvents(popup, selection);

    }

    private void AttachPopupEvents(PopupMenu popup, final Terminal selectedTerminal) {
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                String clickedItemAction = item.getTitle().toString();
                Enums.ActionType action = Enums.ActionType.EDIT;
                if (clickedItemAction.equalsIgnoreCase("edit"))
                    action = Enums.ActionType.EDIT;
                if (clickedItemAction.equalsIgnoreCase("delete"))
                    action = Enums.ActionType.DELETE;
                if (clickedItemAction.equalsIgnoreCase("view"))
                    action = Enums.ActionType.VIEW;
                ProcessSelectedPointEvent(action, selectedTerminal);
                return true;
            }
        });
    }

    private boolean checkIfOrderIsModified(Terminal[] original, Terminal[] current) {
        int index = 0;
        for (Terminal point : original) {
            if (!(point.getValue().toString().equals(current[index].getValue().toString()))) {
                return true;

            }
            index++;
        }
        return false;
    }

    public class AddPointDialog extends Dialog implements
            android.view.View.OnClickListener {
        View myView;
        String _action;
        Button btnAddPoints, btnGetCurrentLocation;
        EditText editName;
        EditText editLat;
        EditText editLng;
        LocationManager locationManager;
        CheckBox check_isMainTerminal;


        Integer _destinationIDthatWillBeEdited;
        Terminal _stationThatWillBeEdited;
        public String TAG = "mead";


        public AddPointDialog(Activity activity, String action) {
            super(activity);
            this._action = action.toLowerCase();
        }

        public AddPointDialog(Activity activity, String action, Terminal stationThatWillBeEdited) {
            super(activity);
            this._action = action.toLowerCase();
            //this._destinationValueForEdit = destinationIDThatWillBeEdited;
            _destinationIDthatWillBeEdited = stationThatWillBeEdited.getID();
            _stationThatWillBeEdited = stationThatWillBeEdited;
        }


        @Override
        public void onClick(View view) {

        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            try {
                requestWindowFeature(Window.FEATURE_NO_TITLE);

                setContentView(R.layout.dialog_add_point);
                btnAddPoints = (Button) findViewById(R.id.btnAddPoint);
                btnGetCurrentLocation = (Button) findViewById(R.id.btnGetCurrentLocation);
                editName = (EditText) findViewById(R.id.terminalName);
                editLat = (EditText) findViewById(R.id.lat);
                editLng = (EditText) findViewById(R.id.lng);
                check_isMainTerminal = findViewById(R.id.chk_isMainTerminal);

                btnAddPoints.setTypeface(Helper.FONT_RUBIK_REGULAR);
                editName.setTypeface(Helper.FONT_RUBIK_REGULAR);
                editLat.setTypeface(Helper.FONT_RUBIK_REGULAR);
                editLng.setTypeface(Helper.FONT_RUBIK_REGULAR);
                check_isMainTerminal.setTypeface(Helper.FONT_RUBIK_REGULAR);

                TextView txtAction = (TextView) findViewById(R.id.txtActionLabel);
                final TextView txtDestinationIDForEdit = (TextView) findViewById(R.id.txtDestinationIDForEdit);
                Integer orderOfArrival = 0;
                MenuActivity.buttonEffect(btnAddPoints);
                btnAddPoints.setText(MenuActivity._GlobalResource.getString(R.string.save_button));
                if (_action.equalsIgnoreCase("add")) {
                    txtAction.setText("ADD NEW PICKUP/DROPOFF POINT");

                } else if (_action.equalsIgnoreCase("update")) {


                    for (Terminal d : MenuActivity._terminalList) {
                        if (d.ID.equals(_destinationIDthatWillBeEdited)) {
                            txtDestinationIDForEdit.setText(String.valueOf(d.ID));
                            editName.setText(d.Description);
                            editLat.setText(d.Lat.toString());
                            editLng.setText(d.Lng.toString());
                            check_isMainTerminal.setChecked(d.getIsMainTerminal().equals("1") ? true : false);
                            editName.setEnabled(true);
                            editLat.setEnabled(true);
                            editLng.setEnabled(true);
                            check_isMainTerminal.setEnabled(true);
                            btnAddPoints.setEnabled(true);
                            btnGetCurrentLocation.setEnabled(true);
                            break;
                        }
                    }
                    txtAction.setText("EDIT PICKUP/DROPOFF POINT");


                } else if (_action.equalsIgnoreCase("view")) {
                    for (Terminal d : MenuActivity._terminalList) {
                        if (d.ID.equals(_destinationIDthatWillBeEdited)) {
                            txtDestinationIDForEdit.setText(String.valueOf(d.ID));
                            editName.setText(d.Description);
                            editLat.setText(d.Lat.toString());
                            editLng.setText(d.Lng.toString());
                            check_isMainTerminal.setChecked(d.getIsMainTerminal().equals("1") ? true : false);

                            editName.setEnabled(false);
                            editLat.setEnabled(false);
                            editLng.setEnabled(false);
                            check_isMainTerminal.setEnabled(false);
                            btnAddPoints.setVisibility(View.GONE);
                            btnGetCurrentLocation.setVisibility(View.GONE);
                            break;
                        }
                    }
                    txtAction.setText("VIEW PICKUP/DROPOFF POINT");
                }

                btnGetCurrentLocation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Location current = MenuActivity.GetCurrentLocation();
                        final String lat = String.valueOf(current.getLatitude());
                        final String lng = String.valueOf(current.getLongitude());
                        editLat.setText(lat);
                        editLng.setText(lng);

                    }
                });


                btnAddPoints.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (_action.equals("add")) {
                            final String name = editName.getText().toString();
                            final String lat = editLat.getText().toString();
                            final String lng = editLng.getText().toString();
                            final String isMainTerminal = check_isMainTerminal.isChecked() ? "1" : "0";

                            ErrorDialog errorDialog = new ErrorDialog(MenuActivity._activity, "");
                            if (name.trim().length() == 0
                                    || lat.trim().length() == 0
                                    || lng.trim().length() == 0) {
                                errorDialog.setErrorMessage("Please supply all fields");
                                errorDialog.show();
                                return;
                            }

                            final Double doubleLat = Double.parseDouble(lat);
                            final Double doubleLng = Double.parseDouble(lng);
                            if (doubleLat < -90 || doubleLat > 90) {
                                errorDialog.setErrorMessage("Invalid latitude");
                                errorDialog.show();
                                return;
                            }
                            if (doubleLng < -180 || doubleLng > 180) {
                                errorDialog.setErrorMessage("Invalid longitude");
                                errorDialog.show();
                                return;
                            }


                            for (Terminal terminal : MenuActivity._terminalList) {
                                Double distanceFromExistingTerminal = _helper.getDistanceFromLatLonInKm(
                                        new LatLng(terminal.getLat(), terminal.getLng()),
                                        new LatLng(doubleLat, doubleLng));

                                if (distanceFromExistingTerminal * 1000 <= Constants.GEOFENCE_RADIUS) {
                                    final Terminal existingTerminal = terminal;
                                    final YesNoDialog dialog = new YesNoDialog(MenuActivity._activity, "Cannot add station",
                                            "The station you were trying to add collides with an existing station and it would cause conflict in the application.\n\n" +
                                                    "Are you trying to add this station?\n" +
                                                    existingTerminal.getDescription() + ": " + existingTerminal.getLat() + "," + existingTerminal.getLng());


                                    dialog._btnYes.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {

                                            dialog.dismiss();
                                            savePoint(existingTerminal.Description, existingTerminal.getLat(), existingTerminal.getLng(), MenuActivity._currentRouteIDSelected, isMainTerminal);
                                            ManageStationsFragment.AddPointDialog.this.dismiss();

                                        }
                                    });
                                    dialog._btnNo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(MenuActivity._activity, "Station not added", Toast.LENGTH_LONG).show();
                                            dialog.dismiss();

                                        }
                                    });
                                    dialog.show();
                                    return;
                                }
                            }
                            savePoint(name, doubleLat, doubleLng, MenuActivity._currentRouteIDSelected, isMainTerminal);
                            ManageStationsFragment.AddPointDialog.this.dismiss();


                        } else if (_action.equals("update")) {
                            String name = editName.getText().toString().trim();
                            String lat = editLat.getText().toString().trim();
                            String lng = editLng.getText().toString().trim();
                            final String isMainTerminal = check_isMainTerminal.isChecked() ? "1" : "0";


                            if (name.trim().length() == 0 || lat.trim().length() == 0 || lng.trim().length() == 0) {
                                Toast.makeText(getContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
                            } else {
                                for (Terminal terminal : MenuActivity._terminalList) {
                                    if (!terminal.getValue().equalsIgnoreCase(_stationThatWillBeEdited.getValue())) {
                                        Double distanceFromExistingTerminal = _helper.getDistanceFromLatLonInKm(
                                                new LatLng(terminal.getLat(), terminal.getLng()),
                                                new LatLng(Double.parseDouble(lat), Double.parseDouble(lng)));

                                        if (distanceFromExistingTerminal * 1000 <= Constants.GEOFENCE_RADIUS) {
                                            final Terminal existingTerminal = terminal;
                                            final YesNoDialog dialog = new YesNoDialog(MenuActivity._activity, "Cannot add station",
                                                    "The new location you provided collides with an existing station and it would cause conflict in the application.\n\n" +
                                                            "Are you trying to replace the entry with this?\n" +
                                                            existingTerminal.getDescription() + ": " + existingTerminal.getLat() + "," + existingTerminal.getLng());


                                            dialog._btnYes.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {

                                                    dialog.dismiss();
                                                    updatePoint(_destinationIDthatWillBeEdited, existingTerminal.Description, existingTerminal.getLat(), existingTerminal.getLng(), MenuActivity._currentRouteIDSelected, isMainTerminal);
                                                    ManageStationsFragment.AddPointDialog.this.dismiss();

                                                }
                                            });
                                            dialog._btnNo.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    Toast.makeText(MenuActivity._activity, "Station not modified", Toast.LENGTH_LONG).show();
                                                    dialog.dismiss();

                                                }
                                            });
                                            dialog.show();
                                            return;
                                        }
                                    }

                                }
                                updatePoint(_destinationIDthatWillBeEdited, name, Double.parseDouble(lat), Double.parseDouble(lng), MenuActivity._currentRouteIDSelected, isMainTerminal);
                                ManageStationsFragment.AddPointDialog.this.dismiss();
                            }
                        }

                    }
                });
            } catch (Exception e) {
                _helper.logger(e, true);

            }

            if (_LoaderDialog != null) _LoaderDialog.dismiss();
        }

        private void savePoint(String name, Double lat, Double lng, Integer tblRouteID, String isMainTerminal) {
            _LoaderDialog = new LoaderDialog(getActivity(), MenuActivity._GlobalResource.getString(R.string.dialog_adding_points_title), MenuActivity._GlobalResource.getString(R.string.dialog_points_map_setup_please_wait));
            _LoaderDialog.show();

            new mySQLAddStation(getContext(), _LoaderDialog, getActivity(), MenuActivity._googleMap, MenuActivity._googleAPI, 0, _manageStationsFragment).execute(name, lat.toString(), lng.toString(), tblRouteID.toString(), isMainTerminal);
        }

        private void updatePoint(Integer ID, String name, Double lat, Double lng, Integer tblRouteID, String isMainTerminal) {
            _LoaderDialog = new LoaderDialog(getActivity(), MenuActivity._GlobalResource.getString(R.string.dialog_updating_points_title), MenuActivity._GlobalResource.getString(R.string.dialog_points_map_update_please_wait));
            _LoaderDialog.show();
            new mySQLUpdateStation(getContext(), _LoaderDialog, getActivity(), MenuActivity._googleMap, MenuActivity._googleAPI, ID, _manageStationsFragment).execute(name, lat.toString(), lng.toString(), tblRouteID.toString(), isMainTerminal);
        }

        private void deletePoint(Integer ID) {
            _LoaderDialog = new LoaderDialog(getActivity(), MenuActivity._GlobalResource.getString(R.string.dialog_deleting_points_title), MenuActivity._GlobalResource.getString(R.string.dialog_points_map_update_please_wait));
            _LoaderDialog.show();
//            new mySQLDeleteStation(getContext(), _LoaderDialog, getActivity(), MenuActivity._googleMap, MenuActivity._googleAPI, ID, _manageStationsFragment).execute();
        }
    }

    public class AddRouteDialog extends Dialog implements android.view.View.OnClickListener {
        Button submitButton;
        EditText txtRouteName;
        TextView tvActionTitle;
        public String TAG = "eleaz";
        public final Activity _activity;
        private String _routeName;
        private Enums.ActionType _action;

        public AddRouteDialog(Activity activity, Enums.ActionType Action, @Nullable String RouteName) {
            super(activity);
            // TODO Auto-generated constructor stub
            this._activity = activity;
            this._action = Action;
            this._routeName = RouteName;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_addroute);
            Boolean isNew = this._action.toString().equalsIgnoreCase("add");
            submitButton = (Button) findViewById(R.id.btnNewRoute);
            txtRouteName = (EditText) findViewById(R.id.textRouteName);
            tvActionTitle = (TextView) findViewById(R.id.textviewActionTitle);
            tvActionTitle.setText(isNew ? "NEW ROUTE NAME" : "EDIT ROUTE NAME");
            submitButton.setText(isNew ? "Save" : "Update");
            txtRouteName.setText(this._routeName);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(), "Event here", Toast.LENGTH_LONG).show();
                }
            });
            MenuActivity.buttonEffect(submitButton);
        }

        @Override
        public void onClick(View view) {

        }
    }

    public void AddNewStationPoint(String DialogAction) {
        AddPointDialog dialog = new AddPointDialog(getActivity(), DialogAction);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }

    public void ModifyStationPoint(String DialogTitle, Terminal stationToBeEdited, Enums.ActionType actionType) {
        try {
            AddPointDialog dialog = new AddPointDialog(MenuActivity._activity, DialogTitle, stationToBeEdited);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        } catch (Exception ex) {
            Toast.makeText(getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }

    public void ProcessSelectedPointEvent(Enums.ActionType Action, final Terminal terminal) {

        if (Action == Enums.ActionType.DELETE) {
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(getActivity());

            builder.setTitle("Delete Point")
                    .setMessage(MenuActivity._GlobalResource.getString(R.string.info_delete_point_confirm))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                LoaderDialog DeleteLoader = new LoaderDialog(getActivity(),
                                        MenuActivity._GlobalResource.getString(R.string.dialog_deleting_points_title),
                                        MenuActivity._GlobalResource.getString(R.string.dialog_please_wait_with_ellipsis));
                                DeleteLoader.setCancelable(false);
                                DeleteLoader.setCancelable(false);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                                new mySQLDeleteStation(getContext(), getActivity(), DeleteLoader, alertDialogBuilder, _manageStationsFragment).execute(String.valueOf(terminal.getID()));
                            } catch (Exception ex) {
                                Helper.logger(ex, true);
                            }


                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            //Toast.makeText(getApplicationContext(), "Delete action here", Toast.LENGTH_LONG).show();
        } else if (Action == Enums.ActionType.EDIT) {
            ModifyStationPoint("UPDATE", terminal, Enums.ActionType.EDIT);
        } else if (Action == Enums.ActionType.VIEW) {
            ModifyStationPoint("VIEW", terminal, Enums.ActionType.VIEW);
        }

    }

    public void ProcessSelectedRoute(Enums.ActionType Action, @Nullable String RouteName) {
        if (Action == Enums.ActionType.DELETE) {
            Toast.makeText(getActivity(), "Delete action here", Toast.LENGTH_LONG).show();
        } else {
            ManageStationsFragment.AddRouteDialog dialog = new ManageStationsFragment.AddRouteDialog(getActivity(), Action, RouteName);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }

    private TouchInterceptor mList;
    private TouchInterceptor.DropListener mDropListener =
            new TouchInterceptor.DropListener() {
                public void drop(int from, int to) {
                    try {
                        if (pointsArrayInString.length != MenuActivity._PointsArray.length) {
                            pointsArrayInString = new String[MenuActivity._PointsArray.length];
                            int ctr = 0;
                            for (Terminal t : MenuActivity._PointsArray) {
                                pointsArrayInString[ctr] = t.getValue();
                                ctr++;
                            }

                        }
                        //Toast.makeText(getApplicationContext(),"Drop listener from: "+from+" to:"+to, Toast.LENGTH_LONG).show();
                        int direction = -1;
                        int loop_start = from;
                        int loop_end = to;
                        if (from < to) {
                            direction = 1;
                        }
                        Terminal target = MenuActivity._PointsArray[from];
                        for (int i = loop_start; i != loop_end; i = i + direction) {

                            pointsArrayInString[i] = MenuActivity._PointsArray[i + direction].getValue();
                            MenuActivity._PointsArray[i] = MenuActivity._PointsArray[i + direction];

                        }
                        MenuActivity._PointsArray[to] = target;
                        pointsArrayInString[to] = target.getValue();
                        //Toast.makeText(getApplicationContext(),"New array arrangement: "+ Arrays.toString(pointsArrayInString), Toast.LENGTH_LONG).show();
                        ((BaseAdapter) mList.getAdapter()).notifyDataSetChanged();
                    } catch (Exception ex) {
                        _helper.logger(ex);
                    }
                }


            };

//    @Override
//    public void onBackPressed() {
//        HandleChangesInOrderOfPoints();
//    }
}
