package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
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
import android.widget.Toast;

import com.umandalmead.samm_v1.Adapters.LineViewCustomAdapter;
import com.umandalmead.samm_v1.EntityObjects.Lines;
import com.umandalmead.samm_v1.EntityObjects.Users;

import java.util.ArrayList;

/**
 * Created by eleazerarcilla on 01/07/2018.
 */

public class ManageLinesActivity extends AppCompatActivity {
    private View myView;
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    public static SwipeRefreshLayout _swipeRefreshLines;
    public static LineViewCustomAdapter _customAdapter;
    public static NonScrollListView _lineListView;
    private Context _context;
    private Activity _activity;
    public AddLineDialog dialog;
    public Helper _helper;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addline);
        _context = getApplicationContext();
        _activity = ManageLinesActivity.this;

        _helper = new Helper();
        try
        {
            InitializeToolbar(MenuActivity._GlobalResource.getString(R.string.title_manage_lines_activity));
            SessionManager sessionManager = new SessionManager(_context);
            _lineListView = (NonScrollListView) findViewById(R.id.linelistview);
            _swipeRefreshLines = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_lines);
            _swipeRefreshLines.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    _swipeRefreshLines.setRefreshing(true);
                    new mySQLLinesDataProvider(_activity, _lineListView).execute();

                }
            });
            _swipeRefreshLines.post(new Runnable() {
                @Override
                public void run() {
                    _swipeRefreshLines.setRefreshing(true);
                    new mySQLLinesDataProvider(_activity, _lineListView).execute();
                }
            });
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }
    public void InitializeToolbar(String fragmentName){
        try {
            FAB_SammIcon = (ImageButton) findViewById(R.id.SAMMLogoFAB);
            FAB_SammIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
            FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent menuIntent = new Intent(ManageLinesActivity.this, MenuActivity.class);
                    startActivity(menuIntent);
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    finish();
                }
            });
            ViewTitle = (TextView) findViewById(R.id.samm_toolbar_title);
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
            tvActionTitle.setText(isNew ? "New line name" : "Edit line name");
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
                        Toast.makeText(_context,"Please enter a valid line name", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (chosenLineAdmin.ID == 0)
                    {
                        Toast.makeText(_context,"Please select an administrator", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    else
                    {
                        if(isNew)
                        {
                            new mySQLAddLine(_context,_activity, _LoaderDialog, dialog,"").execute(txtLineName.getText().toString(), chosenLineAdmin.ID.toString());
                        }
                        else
                        {
                            new mySQLUpdateLine(_context,_activity, _LoaderDialog, dialog,"").execute(String.valueOf(_lineID), txtLineName.getText().toString(), chosenLineAdmin.ID.toString());
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
                builder = new AlertDialog.Builder(ManageLinesActivity.this);
                builder.setTitle("Delete Line")
                        .setMessage("Are you sure you want to delete this line?")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    LoaderDialog DeleteLoader = new LoaderDialog(ManageLinesActivity.this, "Please wait...", "Deleting line...");
                                    DeleteLoader.setCancelable(false);

                                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ManageLinesActivity.this);
                                    new mySQLDeleteLine(getApplicationContext(), ManageLinesActivity.this, DeleteLoader, alertDialogBuilder).execute(String.valueOf(id));
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


                //Toast.makeText(ManageRoutesActivity.this, "Delete action here", Toast.LENGTH_LONG).show();
            }else {
                dialog = new AddLineDialog(ManageLinesActivity.this, action, lineID, lineName, adminUserID);
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
