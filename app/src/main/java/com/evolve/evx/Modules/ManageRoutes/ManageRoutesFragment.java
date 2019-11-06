package com.evolve.evx.Modules.ManageRoutes;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;


import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.evolve.evx.Constants;
import com.evolve.evx.Enums;
import com.evolve.evx.Helper;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.Modules.ManageStations.mySQLStationProvider;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;

import static com.evolve.evx.MenuActivity._GlobalResource;
import static com.evolve.evx.MenuActivity._googleAPI;
import static com.evolve.evx.MenuActivity._googleMap;

/**
 * Created by eleazerarcilla on 01/07/2018.
 */

public class ManageRoutesFragment extends Fragment {

    private View _myView;
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    public static SwipeRefreshLayout _swipeRefreshRoute;
    public static NonScrollListView _routesListView;
    public static RouteViewCustomAdapter customAdapter;
    private Context _context;
    private Activity _activity;
    public AddRouteDialog dialog;
    Integer _lineID = 0;
    Helper _helper = new Helper();
    ManageRoutesFragment _manageRoutesFragment;
    FloatingActionButton FAB_addRoute;
    SessionManager _sessionManager;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _manageRoutesFragment = this;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        try
        {
            _myView =  inflater.inflate(R.layout.activity_addroute, container, false);
            _context = getContext();
            _activity = getActivity();
            _sessionManager = new SessionManager(_context);
            FAB_addRoute = _myView.findViewById(R.id.floatingActionButton_addRoute);
            if (_sessionManager.getIsAdmin())
            {
                FAB_addRoute.setVisibility(View.VISIBLE);
            }
            else
            {
                FAB_addRoute.setVisibility(View.GONE);
            }
            FAB_addRoute.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProcessSelectedRoute(Enums.ActionType.ADD,null, null, _lineID);
                }
            });
            Bundle bundle = this.getArguments();
            if (bundle!=null) {
                _lineID = bundle.getInt("lineID");
                MenuActivity._FragmentTitle = _helper.GetLineObjectByID(_lineID).getName();
            }

            _routesListView = (NonScrollListView) _myView.findViewById(R.id.routelistview);
            try
            {

                InitializeToolbar(MenuActivity._FragmentTitle);
                SessionManager sessionManager = new SessionManager(_context);
                final NonScrollListView routeListview = (NonScrollListView) _myView.findViewById(R.id.routelistview);
                _swipeRefreshRoute = (SwipeRefreshLayout) _myView.findViewById(R.id.swipe_refresh_routes);
                _swipeRefreshRoute.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        InitializeView(routeListview);
                    }
                });
                _swipeRefreshRoute.post(new Runnable() {
                    @Override
                    public void run() {
                        InitializeView(routeListview);
                    }
                });


            }
            catch(Exception ex)
            {
                Helper.logger(ex,true);
            }
            return _myView;

        }
        catch(Exception ex)
        {
            _helper.logger(ex);
        }
        return null;

    }



    public void InitializeToolbar(String fragmentName){
        try
        {
            FAB_SammIcon = (ImageButton) _myView.findViewById(R.id.SAMMLogoFAB);
            FAB_SammIcon.setImageResource(R.drawable.ic_back_left_arrow);
            FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MenuActivity._FragmentTitle = _GlobalResource.getString(R.string.title_manage_lines_activity);
                    getActivity().overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, MenuActivity._manageLinesFragment)
                            .addToBackStack(Constants.FRAGMENTNAME_MANAGELINES)
                            .commit();
                }
            });
            ViewTitle = (TextView) _myView.findViewById(R.id.samm_toolbar_title);
            ViewTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
            ViewTitle.setText(fragmentName);
        }
        catch(Exception ex)
        {
            _helper.logger(ex);
        }
    }
    public void InitializeView(NonScrollListView NSRouteListView){
        try
        {



            FragmentManager fm = getFragmentManager();

            new mySQLRoutesDataProvider(_activity, _context, ManageRoutesFragment._routesListView, _manageRoutesFragment, fm).execute(_lineID.toString());
            new mySQLStationProvider(_context, _activity, "", _googleMap, _googleAPI).execute();

//            LoaderDialog loaderDialog = new LoaderDialog(_activity, MenuActivity._GlobalResource.getString(R.string.dialog_routes_title), MenuActivity._GlobalResource.getString(R.string.dialog_routes_message_with_ellipsis));
//            loaderDialog.show();
//            ArrayList<Routes> routesByLineID = new ArrayList<Routes>();
//            if (_lineID!=0)
//                for(Routes route:MenuActivity._routeList)
//                {
//                    if (route.getTblLineID() == _lineID)
//                        routesByLineID.add(route);
//                }
//            else
//                routesByLineID = new ArrayList<Routes>(MenuActivity._routeList);
//            routesByLineID.add(new Routes(0,0, "Add Route"));
//
//            customAdapter =new RouteViewCustomAdapter(routesByLineID, _activity,NSRouteListView,fm, _swipeRefreshRoute, _manageRoutesFragment);
//            _routesListView.setAdapter(customAdapter);
//            _swipeRefreshRoute.setRefreshing(false);
//            loaderDialog.dismiss();
        }
        catch (Exception ex)
        {
            _helper.logger(ex,true);
        }

    }
    public class AddRouteDialog extends Dialog implements android.view.View.OnClickListener{
        Button submitButton;
        EditText txtRouteName;
        TextView tvActionTitle;
        public String TAG ="eleaz";
        public final Activity _activity;
        private Integer _routeID;
        private Integer _lineID;
        private String _routeName;
        private Enums.ActionType _action;
        LoaderDialog _LoaderDialog;


        public AddRouteDialog(Activity activity, Enums.ActionType Action, @Nullable Integer RouteID, @Nullable String RouteName, @Nullable Integer LineID) {
            super(activity);
            // TODO Auto-generated constructor stub
            this._activity = activity;
            this._action = Action;
            this._routeName = RouteName;
            this._routeID = RouteID;
            this._lineID = LineID;
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_addroute);
            final Boolean isNew=this._action.toString().equalsIgnoreCase("add");
            submitButton = (Button) findViewById(R.id.btnNewRoute);
            txtRouteName = (EditText) findViewById(R.id.textRouteName);
            tvActionTitle = (TextView) findViewById(R.id.textviewActionTitle);
            tvActionTitle.setText(isNew ? "NEW ROUTE NAME" : "EDIT ROUTE NAME");
            submitButton.setText(MenuActivity._GlobalResource.getString(R.string.save_button));
            txtRouteName.setText(this._routeName);

            _LoaderDialog = new LoaderDialog(_activity,MenuActivity._GlobalResource.getString(R.string.dialog_add_route_title), MenuActivity._GlobalResource.getString(R.string.dialog_please_wait_with_ellipsis));
            _LoaderDialog.setCancelable(false);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newRouteName = txtRouteName.getText().toString().trim();

                    if (newRouteName.isEmpty() || Helper.HasSpecialCharacters(newRouteName))
                    {
                        Toast.makeText(_context,MenuActivity._GlobalResource.getString(R.string.error_invalid_route_name), Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(isNew)
                        {
                            new mySQLAddRoute(_context,_activity, _LoaderDialog, dialog,"", _manageRoutesFragment, getFragmentManager()).execute(newRouteName,_lineID.toString());
                        }
                        else
                        {
                            new mySQLUpdateRoute(_context,_activity, _LoaderDialog, dialog,"", _manageRoutesFragment).execute(String.valueOf(_routeID), newRouteName);
                        }
                    }
                }
            });
            MenuActivity.buttonEffect(submitButton);
        }

        @Override
        public void onClick(View view) {

        }
    }
    public void ProcessSelectedRoute(Enums.ActionType Action,@Nullable Integer RouteID, @Nullable String RouteName, @Nullable Integer LineID){


        final int  id = RouteID!=null?RouteID:0;
        if(Action == Enums.ActionType.DELETE){
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(_context);
            builder.setTitle("Delete Point")
                    .setMessage(MenuActivity._GlobalResource.getString(R.string.dialog_confirm_delete_route))
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                LoaderDialog DeleteLoader = new LoaderDialog(_activity, MenuActivity._GlobalResource.getString(R.string.dialog_delete_route_title), MenuActivity._GlobalResource.getString(R.string.dialog_please_wait_with_ellipsis));
                                DeleteLoader.setCancelable(false);
                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_activity);
                                new mySQLDeleteRoute(_context, _activity, _routesListView, DeleteLoader,"", alertDialogBuilder, _manageRoutesFragment).execute(String.valueOf(id));
                            }
                            catch(Exception ex)
                            {
                                Helper.logger(ex,true);
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


            //Toast.makeText(ManageRoutesFragment.this, "Delete action here", Toast.LENGTH_LONG).show();
        }else {
            dialog = new AddRouteDialog(_activity, Action, RouteID, RouteName, LineID);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }
}
