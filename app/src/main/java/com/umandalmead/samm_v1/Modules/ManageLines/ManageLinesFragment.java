package com.umandalmead.samm_v1.Modules.ManageLines;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.Enums;
import com.umandalmead.samm_v1.ErrorDialog;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.LoaderDialog;
import com.umandalmead.samm_v1.MenuActivity;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SessionManager;

import java.util.ArrayList;

/**
 * Created by eleazerarcilla on 01/07/2018.
 */

public class ManageLinesFragment extends Fragment {
    private View _myView;
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    public static SwipeRefreshLayout _swipeRefreshLines;
    public static LineViewCustomAdapter _customAdapter;
    public static NonScrollListView _lineListView;
    private Context _context;
    private Activity _activity;
    public AddLineDialog dialog;
    public Helper _helper;
    private ManageLinesFragment _manageLinesFragment;
    public static Integer _adminUserID;
    public static FloatingActionButton FAB_addLine;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _manageLinesFragment = this;

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        try
        {
            _myView =  inflater.inflate(R.layout.activity_addline, container, false);
            _context = getContext();
            _activity = getActivity();
            FAB_addLine = _myView.findViewById(R.id.floatingActionButton_addLine);

            Bundle arguments = getArguments();
            if (arguments!=null)
            {
                _adminUserID = arguments.getInt("adminUserID");
            }
            else
            {
                _adminUserID =0;
            }



            _helper = new Helper();
            try
            {
                InitializeToolbar(MenuActivity._GlobalResource.getString(R.string.title_manage_lines_activity));
                SessionManager sessionManager = new SessionManager(_context);
                _lineListView = (NonScrollListView) _myView.findViewById(R.id.linelistview);
                _swipeRefreshLines = (SwipeRefreshLayout) _myView.findViewById(R.id.swipe_refresh_lines);
                _swipeRefreshLines.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        _swipeRefreshLines.setRefreshing(true);
                        new mySQLLinesDataProvider(_activity, _lineListView, _manageLinesFragment, getFragmentManager()).execute(_adminUserID.toString());

                    }
                });
                _swipeRefreshLines.post(new Runnable() {
                    @Override
                    public void run() {
                        _swipeRefreshLines.setRefreshing(true);
                        new mySQLLinesDataProvider(_activity, _lineListView, _manageLinesFragment, getFragmentManager()).execute(_adminUserID.toString());
                    }
                });
            }
            catch(Exception ex)
            {
                Helper.logger(ex,true);
            }
            FAB_addLine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProcessSelectedLine(Enums.ActionType.ADD,null, null, null);
                }
            });
            return _myView;
        }
        catch(Exception ex)
        {
            _helper.logger(ex);

        }
        return null;


    }
    public void InitializeToolbar(String fragmentName){
        try {
            FAB_SammIcon = (ImageButton) _myView.findViewById(R.id.SAMMLogoFAB);
            FAB_SammIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
    //                Intent menuIntent = new Intent(ManageLinesFragment.this, MenuActivity.class);
    //                startActivity(menuIntent);
    //                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    //                finish();
                }
            });
            ViewTitle = (TextView) _myView.findViewById(R.id.samm_toolbar_title);
            ViewTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
            ViewTitle.setText(fragmentName);
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
    public void InitializeView(NonScrollListView NSLinesListView){

    }
    public  class AddLineDialog extends Dialog implements android.view.View.OnClickListener{
        Button submitButton;
        EditText txtLineName;
        Spinner spinnerAdminUser;
        TextView tvActionTitle;
        public String TAG ="eleaz";
        public final Activity _activity;
        private Integer _lineID;
        private String _lineName;
        private Enums.ActionType _action;
        LoaderDialog _LoaderDialog;
        public int _selectedIndex = 0;
        public Integer _indexOfCurrentAdminUserAssignedToTheLine = 0;
        public Integer _userIDOfTheCurrentAdminAssignedToTheLine = 0;


        public AddLineDialog(Activity activity, Enums.ActionType Action, @Nullable Integer LineID, @Nullable String LineName,  @Nullable Integer AdminUserID) {
            super(activity);
            // TODO Auto-generated constructor stub
            this._activity = activity;
            this._action = Action;
            this._lineName = LineName;
            this._lineID = LineID;
            this._userIDOfTheCurrentAdminAssignedToTheLine = AdminUserID;
        }
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dialog_addline);
            final Boolean isNew=this._action.toString().equalsIgnoreCase("add");
            submitButton = (Button) findViewById(R.id.btnNewLine);
            txtLineName = (EditText) findViewById(R.id.textLineName);
            spinnerAdminUser = (Spinner) findViewById(R.id.spinnerAdminUsers);
            tvActionTitle = (TextView) findViewById(R.id.textviewActionTitle);
            tvActionTitle.setText(isNew ? "NEW LINE NAME" : "EDIT LINE NAME");
            submitButton.setText(isNew ? "Save" : "Update");
            txtLineName.setText(this._lineName);



            ArrayList<Users> copyOfAdminUsers = new ArrayList<>();
            copyOfAdminUsers.add(new Users(0, "Select administrator of this line", "", "","","","",1));
            copyOfAdminUsers.addAll(MenuActivity._adminUsers);
            Integer index = 0;
            for (Users u : copyOfAdminUsers)
            {
                if(_userIDOfTheCurrentAdminAssignedToTheLine != null)
                    if(_userIDOfTheCurrentAdminAssignedToTheLine!=0)
                        if (u.ID ==_userIDOfTheCurrentAdminAssignedToTheLine)
                        {
                            _indexOfCurrentAdminUserAssignedToTheLine= index;
                            break;
                        }

                index++;
            }
            ArrayAdapter<Users> adapter = new ArrayAdapter<Users>(getContext(), R.layout.list_item, copyOfAdminUsers)
            {
                public View getView(int position, View convertView, ViewGroup parent) {
                    // Cast the spinner collapsed item (non-popup item) as a text view
                    TextView tv = (TextView) super.getView(position, convertView, parent);

                    // Set the text color of spinner item
                    if(position == 0)
                        tv.setTextColor(Color.GRAY);
                    else
                        tv.setTextColor(Color.BLACK);

                    // Return the view
                    return tv;
                }
               @Override
               public boolean isEnabled(int position)
               {
                   if(position == 0)
                       return false;
                   else
                       return true;
               }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent){
                    // Cast the drop down items (popup items) as text view
                    TextView tv = (TextView) super.getDropDownView(position,convertView,parent);
                    if (position == 0)
                        tv.setTextColor(Color.GRAY);
                    else
                        tv.setTextColor(Color.BLACK);

                    tv.setTypeface(Typeface.DEFAULT);

                    // If this item is selected item
                    if(position == _selectedIndex && position!=0){
                        // Set spinner selected popup item's text color
                        // tv.setTextColor(Color.BLUE);
                        tv.setTypeface(Typeface.DEFAULT_BOLD);
                    }

                    // Return the modified view
                    return tv;
                }
            };
            // Set an item selection listener for spinner widget
            spinnerAdminUser.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    // Set the value for selected index variable
                    _selectedIndex = i;
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });

            spinnerAdminUser.setAdapter(adapter);
            spinnerAdminUser.setPrompt("SELECT AN ADMIN USER");
            spinnerAdminUser.setSelection(_indexOfCurrentAdminUserAssignedToTheLine);

            _LoaderDialog = new LoaderDialog(_activity,"Adding Line...", "Please wait...");
            _LoaderDialog.setCancelable(false);
            submitButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String newLineName = txtLineName.getText().toString();
                    Users chosenLineAdmin = (Users) spinnerAdminUser.getSelectedItem();

                    if (newLineName.isEmpty())
                    {
                        ErrorDialog errorDialog = new ErrorDialog(_activity, "Please enter a valid line name");
                        errorDialog.show();
                        return;
                    }
                    if (chosenLineAdmin.ID == 0)
                    {
                        ErrorDialog errorDialog = new ErrorDialog(_activity, "Please select the administrator of this line");
                        errorDialog.show();
                        return;
                    }
                    else
                    {
                        if(isNew)
                        {
                            new mySQLAddLine(_context,_activity, _LoaderDialog, dialog,"", _manageLinesFragment, getFragmentManager()).execute(txtLineName.getText().toString(), chosenLineAdmin.ID.toString());
                        }
                        else
                        {
                            new mySQLUpdateLine(_context,_activity, _LoaderDialog, dialog,"", _manageLinesFragment, getFragmentManager()).execute(String.valueOf(_lineID), txtLineName.getText().toString(), chosenLineAdmin.ID.toString());
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
    public void ProcessSelectedLine(Enums.ActionType action, @Nullable Integer lineID, @Nullable String lineName, @Nullable Integer adminUserID){

        final Integer  id = lineID!=null?lineID:0;
        try
        {
            if(action == Enums.ActionType.DELETE){
                AlertDialog.Builder builder;
                builder = new AlertDialog.Builder(_activity);
                builder.setTitle("Delete Line")
                        .setMessage("Are you sure you want to delete this line?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    LoaderDialog DeleteLoader = new LoaderDialog(_activity, "Please wait...", "Deleting line...");
                                    DeleteLoader.setCancelable(false);

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(_activity);
                                    new mySQLDeleteLine(getContext(), _activity, DeleteLoader, alertDialogBuilder, _manageLinesFragment, getFragmentManager()).execute(String.valueOf(id));
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
                dialog = new AddLineDialog(_activity, action, lineID, lineName, adminUserID);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        }
        catch(Exception ex)
        {
            _helper.logger(ex,true);
        }


    }
}
