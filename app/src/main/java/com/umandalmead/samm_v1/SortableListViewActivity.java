package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.umandalmead.samm_v1.EntityObjects.Terminal;

import java.util.Arrays;

/**
 * Created by eleazerarcilla on 01/07/2018.
 */

public class SortableListViewActivity extends ListActivity {
    private View myView;
    private ImageButton FAB_SammIcon;
    private  TextView ViewTitle;
    public ProgressDialog _ProgressDialog;
    public Terminal[] originalPointsArray;
    public Boolean isItTheSame;
    public String[] pointsArrayInString;
    Helper _helper;


    private ImageView BtnAddPoint;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addpoints);
        _helper = new Helper();
        isItTheSame = true;
        int index =0;

        originalPointsArray = Arrays.copyOf(MenuActivity._PointsArray, MenuActivity._PointsArray.length);
        pointsArrayInString = new String[MenuActivity._PointsArray.length];
        int ctr = 0;
        for(Terminal t: MenuActivity._PointsArray)
        {
            pointsArrayInString[ctr] = t.getValue();
            ctr++;
        }
        try {
            ArrayAdapter adp = new ArrayAdapter(this, R.layout.listview_viewpoints, MenuActivity._PointsArray);
            setListAdapter(adp);
            InitializeToolbar(MenuActivity._FragmentTitle);
            mList = (TouchInterceptor) getListView();
            mList.setDropListener(mDropListener);

            registerForContextMenu(mList);
            _ProgressDialog = new ProgressDialog(this);
            _ProgressDialog.setTitle("Adding Vehicle GPS");
            _ProgressDialog.setMessage("Initializing...");
            _ProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            _ProgressDialog.setCancelable(false);
        }
        catch (Exception ex){
            Toast.makeText(SortableListViewActivity.this, ex.getMessage().toString(), Toast.LENGTH_LONG).show();
        }
    }
    private void HandleChangesInOrderOfPoints()
    {
        if(checkIfOrderIsModified(originalPointsArray, MenuActivity._PointsArray) == true)
        {
            try
            {
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(SortableListViewActivity.this);

                builder.setTitle("Points have been modified.")
                        .setMessage("Save changes?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    ProgressDialog progDialog = new ProgressDialog(SortableListViewActivity.this);
                                    progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                    progDialog.setTitle("Please wait...");
                                    progDialog.setMessage("Updating order of points");
                                    progDialog.setCancelable(false);
                                    String pointsArray = Arrays.toString(pointsArrayInString);
                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SortableListViewActivity.this);
                                    new mySQLUpdateDestinationsOrder(getApplicationContext(), SortableListViewActivity.this,progDialog,"", alertDialogBuilder).execute(pointsArray.substring(1, pointsArray.length()-1),String.valueOf(MenuActivity._currentRouteIDSelected));

                                    originalPointsArray = Arrays.copyOf(MenuActivity._PointsArray, MenuActivity._PointsArray.length);


//                                    Intent routeIntent = new Intent(SortableListViewActivity.this, ManageRoutesActivity.class);
//                                    startActivity(routeIntent);
//                                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
//                                    finish();
                                }
                                catch(Exception ex)
                                {
                                    Helper.logger(ex);
                                }


                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                Intent routeIntent = new Intent(SortableListViewActivity.this, ManageRoutesActivity.class);
                                startActivity(routeIntent);
                                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
            catch(Exception ex)
            {
                _helper.logger(ex);
            }
        }
        else
        {
            try {
                Intent routeIntent = new Intent(SortableListViewActivity.this, ManageRoutesActivity.class);
                startActivity(routeIntent);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                finish();
            }catch (Exception ex){
                Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
            }
        }


    }

    public void InitializeToolbar(String fragmentName){
        FAB_SammIcon = (ImageButton) findViewById(R.id.SAMMLogoFAB);
        FAB_SammIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        BtnAddPoint = (ImageView) findViewById(R.id.topnav_addButton);
        BtnAddPoint.setVisibility(View.VISIBLE);
        BtnAddPoint.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                try {
                    AddNewStationPoint("Add");
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    HandleChangesInOrderOfPoints();
                }catch (Exception ex){
                    Toast.makeText(getApplicationContext(),ex.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
        ViewTitle = (TextView) findViewById(R.id.samm_toolbar_title);
        ViewTitle.setText(fragmentName);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Terminal selection = MenuActivity._PointsArray[position];
        Toast.makeText(this, selection.Value, Toast.LENGTH_SHORT).show();
        final PopupMenu popup = new PopupMenu(getApplicationContext(), v, Gravity.RIGHT);
        popup.getMenuInflater().inflate(R.menu.popup_route_actions, popup.getMenu());
        popup.show();
        AttachPopupEvents(popup, selection);

    }
    private void AttachPopupEvents(PopupMenu popup, final Terminal selectedTerminal){
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Enums.ActionType action = item.getTitle().toString().equalsIgnoreCase("edit") ? Enums.ActionType.EDIT : Enums.ActionType.DELETE;
                ProcessSelectedPointEvent(action, selectedTerminal);
                return true;
            }
        });
    }
    private boolean checkIfOrderIsModified(Terminal[] original, Terminal[] current)
    {
        int index = 0;
        for(Terminal point: original)
        {
            if (!(point.getValue().toString().equals(current[index].getValue().toString())))
            {
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
        Button btnAddPoints;
        EditText editName;
        EditText editLat;
        EditText editLng;


        Terminal _destinationThatWillBeEdited;
        public String TAG = "mead";


        public AddPointDialog(Activity activity, String action) {
            super(activity);
            this._action = action.toLowerCase();
        }
        public AddPointDialog(Activity activity, String action, Terminal destinationThatWillBeEdited) {
            super(activity);
            this._action = action.toLowerCase();
            //this._destinationValueForEdit = destinationThatWillBeEdited;
            _destinationThatWillBeEdited = destinationThatWillBeEdited;
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
                editName = (EditText) findViewById(R.id.terminalName);
                editLat = (EditText) findViewById(R.id.lat);
                editLng = (EditText) findViewById(R.id.lng);
                TextView txtAction = (TextView) findViewById(R.id.txtActionLabel);
                final TextView txtDestinationIDForEdit = (TextView) findViewById(R.id.txtDestinationIDForEdit);
                Integer orderOfArrival = 0;
                MenuActivity.buttonEffect(btnAddPoints);

                if(_action.equals("add"))
                {
                    btnAddPoints.setText("ADD");
                    txtAction.setText("ADD NEW PICKUP/DROPOFF POINT");

                }
                else {

                    btnAddPoints.setText("UPDATE");

                    for(Terminal d: MenuActivity._terminalList)
                    {
                        if (d.Value.equals(_destinationThatWillBeEdited.getValue()))
                        {
                            txtDestinationIDForEdit.setText(String.valueOf(d.ID));
                            editName.setText(d.Description);
                            editLat.setText(d.Lat.toString());
                            editLng.setText(d.Lng.toString());
                            break;
                        }
                    }
                    txtAction.setText("EDIT PICKUP/DROPOFF POINT");


                }
                final DialogInterface.OnClickListener dialog_deletepoint = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                deletePoint(Integer.parseInt(txtDestinationIDForEdit.getText().toString()));
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                btnAddPoints.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(_action.equals("add")) {
                            String name = editName.getText().toString();
                            String lat = editLat.getText().toString();
                            String lng = editLng.getText().toString();


                            if (name.trim().length() == 0 || lat.trim().length() == 0 || lng.trim().length() == 0) {
                                Toast.makeText(getApplicationContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
                            } else {

                                savePoint(name, Double.parseDouble(lat), Double.parseDouble(lng), MenuActivity._currentRouteIDSelected);
                                SortableListViewActivity.AddPointDialog.this.dismiss();
                            }
                        }
                        else if(_action.equals("update"))
                        {
                            String name = editName.getText().toString();
                            String lat = editLat.getText().toString();
                            String lng = editLng.getText().toString();


                            if (name.trim().length() == 0 || lat.trim().length() == 0 || lng.trim().length() == 0 ) {
                                Toast.makeText(getApplicationContext(), "Please supply all fields", Toast.LENGTH_LONG).show();
                            } else {
                                updatePoint(_destinationThatWillBeEdited.getID(), name, Double.parseDouble(lat), Double.parseDouble(lng), MenuActivity._currentRouteIDSelected);
                                SortableListViewActivity.AddPointDialog.this.dismiss();
                            }
                        }

                    }
                });
            }catch(Exception e)
            {
                Log.e(TAG, e.getMessage());
            }

            _ProgressDialog.dismiss();
        }

        private void savePoint(String name, Double lat, Double lng, Integer tblRouteID)
        {
            _ProgressDialog = new ProgressDialog(SortableListViewActivity.this);
            _ProgressDialog.setTitle("Adding Pickup/Dropoff Point");
            _ProgressDialog.setMessage("Please wait as we set up the points on the map");
            _ProgressDialog.show();
            new asyncAddPoints(getApplicationContext(), _ProgressDialog, SortableListViewActivity.this, MenuActivity._googleMap, MenuActivity._googleAPI,"Add", 0).execute(name, lat.toString(), lng.toString(), tblRouteID.toString());
        }
        private void updatePoint(Integer ID, String name, Double lat, Double lng, Integer tblRouteID)
        {
            _ProgressDialog = new ProgressDialog(SortableListViewActivity.this);
            _ProgressDialog.setTitle("Updating Pickup/Dropoff Point");
            _ProgressDialog.setMessage("Please wait as we update the points on the map");
            _ProgressDialog.show();
            new asyncAddPoints(getApplicationContext(), _ProgressDialog, SortableListViewActivity.this, MenuActivity._googleMap, MenuActivity._googleAPI, "Update", ID).execute(name, lat.toString(), lng.toString(), tblRouteID.toString());
        }
        private void deletePoint(Integer ID)
        {
            _ProgressDialog = new ProgressDialog(SortableListViewActivity.this);
            _ProgressDialog.setTitle("Deleting Pickup/Dropoff Point");
            _ProgressDialog.setMessage("Please wait as we update the points on the map");
            _ProgressDialog.show();
            new asyncAddPoints(getApplicationContext(), _ProgressDialog, SortableListViewActivity.this, MenuActivity._googleMap, MenuActivity._googleAPI, "Delete", ID).execute();
        }
    }
    public  class AddRouteDialog extends Dialog implements android.view.View.OnClickListener{
        Button submitButton;
        EditText txtRouteName;
        TextView tvActionTitle;
        public String TAG ="eleaz";
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
            Boolean isNew=this._action.toString().equalsIgnoreCase("add");
            submitButton = (Button) findViewById(R.id.btnNewRoute);
            txtRouteName = (EditText) findViewById(R.id.textRouteName);
            tvActionTitle = (TextView) findViewById(R.id.textviewActionTitle);
            tvActionTitle.setText(isNew ? "New route name" : "Edit route name");
            submitButton.setText(isNew ? "Save" : "Update");
            txtRouteName.setText(this._routeName);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(getContext(),"Event here", Toast.LENGTH_LONG).show();
                }
            });
            MenuActivity.buttonEffect(submitButton);
        }

        @Override
        public void onClick(View view) {

        }
    }
    public void AddNewStationPoint(String DialogAction){
        AddPointDialog dialog = new AddPointDialog(SortableListViewActivity.this, DialogAction);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
    }
    public void ModifyStationPoint(String DialogTitle, Terminal DestinationToBeEdited){
        try {
            AddPointDialog dialog = new AddPointDialog(SortableListViewActivity.this, "Update", DestinationToBeEdited);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }catch (Exception ex){
            Toast.makeText(getApplicationContext(),ex.getMessage(), Toast.LENGTH_LONG).show();
        }

    }
    public void ProcessSelectedPointEvent(Enums.ActionType Action, final Terminal SelectedTerminal){
        Terminal t = SelectedTerminal;
        if(Action == Enums.ActionType.DELETE){
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(SortableListViewActivity.this);

            builder.setTitle("Delete Point")
                    .setMessage("Are you sure you want to delete this point?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                ProgressDialog progDialog = new ProgressDialog(SortableListViewActivity.this);
                                progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                progDialog.setTitle("Please wait...");
                                progDialog.setMessage("Deleting point...");
                                progDialog.setCancelable(false);

                                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SortableListViewActivity.this);
                                new mySQLDeleteDestinationOrRoute(getApplicationContext(), SortableListViewActivity.this, progDialog,"", alertDialogBuilder, "Destination").execute(String.valueOf(SelectedTerminal.getID()));
                            }
                            catch(Exception ex)
                            {
                                Helper.logger(ex);
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
        }else {
            ModifyStationPoint("UPDATE",SelectedTerminal);
        }

    }
    public void ProcessSelectedRoute(Enums.ActionType Action, @Nullable String RouteName){
        if(Action == Enums.ActionType.DELETE){
            Toast.makeText(SortableListViewActivity.this, "Delete action here", Toast.LENGTH_LONG).show();
        }else {
            SortableListViewActivity.AddRouteDialog dialog = new SortableListViewActivity.AddRouteDialog(SortableListViewActivity.this, Action, RouteName);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        }

    }
    private TouchInterceptor mList;
    private TouchInterceptor.DropListener mDropListener =
            new TouchInterceptor.DropListener() {
                public void drop(int from, int to) {

                    //Toast.makeText(getApplicationContext(),"Drop listener from: "+from+" to:"+to, Toast.LENGTH_LONG).show();
                    int direction = -1;
                    int loop_start = from;
                    int loop_end = to;
                    if(from < to) {
                        direction = 1;
                    }
                    Terminal target = MenuActivity._PointsArray[from];
                    for(int i=loop_start;i!=loop_end;i=i+direction){

                        pointsArrayInString[i] = MenuActivity._PointsArray[i+direction].getValue();
                        MenuActivity._PointsArray[i] = MenuActivity._PointsArray[i+direction];

                    }
                    MenuActivity._PointsArray[to] = target;
                    pointsArrayInString[to] = target.getValue();
                    //Toast.makeText(getApplicationContext(),"New array arrangement: "+ Arrays.toString(pointsArrayInString), Toast.LENGTH_LONG).show();
                    ((BaseAdapter) mList.getAdapter()).notifyDataSetChanged();
                }


            };
    @Override
    public void onBackPressed() {
        HandleChangesInOrderOfPoints();
    }
}
