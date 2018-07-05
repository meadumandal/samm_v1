package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.umandalmead.samm_v1.Adapters.RouteViewCustomAdapter;

/**
 * Created by eleazerarcilla on 01/07/2018.
 */

public class ManageRoutesActivity extends AppCompatActivity {
    private View myView;
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    private SwipeRefreshLayout swipeRefreshRoute;
    public NonScrollListView ScrollListView;
    private RouteViewCustomAdapter customAdapter;
    private Context _context;
    private Activity _activity;
    public AddRouteDialog dialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addroute);
        _context = getApplicationContext();
        _activity = ManageRoutesActivity.this;
        ScrollListView = (NonScrollListView) findViewById(R.id.routelistview);
        try
        {
            InitializeToolbar("Manage Routes");
            SessionManager sessionManager = new SessionManager(_context);
            final NonScrollListView routeListview = (NonScrollListView) findViewById(R.id.routelistview);
            swipeRefreshRoute = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_routes);
            swipeRefreshRoute.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    InitializeView(routeListview);
                }
            });
            swipeRefreshRoute.post(new Runnable() {
                @Override
                public void run() {
                    InitializeView(routeListview);
                }
            });

        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }


    }
    public void InitializeToolbar(String fragmentName){
        FAB_SammIcon = (ImageButton) findViewById(R.id.SAMMLogoFAB);
        FAB_SammIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent menuIntent = new Intent(ManageRoutesActivity.this, MenuActivity.class);
                startActivity(menuIntent);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                finish();
            }
        });
        ViewTitle = (TextView) findViewById(R.id.samm_toolbar_title);
        ViewTitle.setText(fragmentName);
    }
    public void InitializeView(NonScrollListView NSRouteListView){
        swipeRefreshRoute.setRefreshing(true);
        FragmentManager fm = _activity.getFragmentManager();
        new mySQLRoutesDataProvider(_activity).execute();
        customAdapter =new RouteViewCustomAdapter(MenuActivity._routeList, _activity,NSRouteListView,fm, swipeRefreshRoute);
        ScrollListView.setAdapter(customAdapter);
        swipeRefreshRoute.setRefreshing(false);
    }
    public  class AddRouteDialog extends Dialog implements android.view.View.OnClickListener{
        Button submitButton;
        EditText txtRouteName;
        TextView tvActionTitle;
        public String TAG ="eleaz";
        public final Activity _activity;
        private Integer _routeID;
        private String _routeName;
        private Enums.ActionType _action;
        ProgressDialog progressDialog = new ProgressDialog(ManageRoutesActivity.this    );


        public AddRouteDialog(Activity activity, Enums.ActionType Action, @Nullable Integer RouteID, @Nullable String RouteName) {
            super(activity);
            // TODO Auto-generated constructor stub
            this._activity = activity;
            this._action = Action;
            this._routeName = RouteName;
            this._routeID = RouteID;
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
            tvActionTitle.setText(isNew ? "New route name" : "Edit route name");
            submitButton.setText(isNew ? "Save" : "Update");
            txtRouteName.setText(this._routeName);
            progressDialog.setTitle("Adding Route...");
            progressDialog.setMessage("Please wait...");
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newRouteName = txtRouteName.getText().toString();
                    if (newRouteName.isEmpty())
                    {
                        Toast.makeText(_context,"Please enter a valid route name", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        if(isNew)
                        {

                            new mySQLAddRoute(_context,_activity, progressDialog, dialog,"").execute(txtRouteName.getText().toString());
                        }
                        else
                        {
                            new mySQLUpdateRoute(_context,_activity, progressDialog, dialog,"").execute(String.valueOf(_routeID), txtRouteName.getText().toString());
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
    public void ProcessSelectedRoute(Enums.ActionType Action,@Nullable Integer RouteID, @Nullable String RouteName){
        if(Action == Enums.ActionType.DELETE){
            Toast.makeText(ManageRoutesActivity.this, "Delete action here", Toast.LENGTH_LONG).show();
        }else {
            dialog = new AddRouteDialog(ManageRoutesActivity.this, Action, RouteID, RouteName);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }
}
