package com.evolve.evx.Modules.DriverUsers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.evolve.evx.Constants;
import com.evolve.evx.EntityObjects.Lines;
import com.evolve.evx.EntityObjects.Users;
import com.evolve.evx.ErrorDialog;
import com.evolve.evx.Helper;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.R;
import com.evolve.evx.SerializableRefreshLayoutComponents;

import java.util.ArrayList;

/**
 * Created by MeadRoseAnn on 08/20/2018.
 */

public class EditDriverUserDialogFragment extends DialogFragment {
    //private View pic;
    String TAG="mead", _datamodeljson, _username;
    Integer _userID;
    Users _datamodel;
    SwipeRefreshLayout _swipeRefresh;
    FragmentManager _fragmentManager;
    NonScrollListView _adminUserListView;
    Boolean _isAdd;



    public EditDriverUserDialogFragment()
    {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog builder = new Dialog(getActivity());
        try
        {

            View view = getActivity().getLayoutInflater().inflate(R.layout.fragmentdialog_edit_driver_user, new LinearLayout(getActivity()), false);

            final EditText edit_firstname = (EditText) view.findViewById(R.id.firstname);
            final EditText edit_lastname = (EditText) view.findViewById(R.id.lastname);
            final EditText edit_username = (EditText) view.findViewById(R.id.username);
            final EditText edit_password = (EditText) view.findViewById(R.id.password);
            final TextView textLabel = (TextView) view.findViewById(R.id.txtActionLabel);
            final TextView textPasswordAction = view.findViewById(R.id.txtPasswordAction);
            final EditText edit_confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
            final Spinner spinnerLines  = view.findViewById(R.id.spinnerLines);
            final CheckBox checkShowPasswords = view.findViewById(R.id.showPassword);
            Button btnUpdate = (Button) view.findViewById(R.id.btnUpdateDriverUser);
            Button btnDelete = (Button) view.findViewById(R.id.btnDeleteDriverUser);

            ArrayList<Lines> tempLineList = new ArrayList<>();
            tempLineList.add(new Lines(0, "Select a line *",0 ,""));
            tempLineList.addAll(MenuActivity._lineList);

            ArrayAdapter<Lines> linesListAdapter = new ArrayAdapter<Lines>(getContext(), R.layout.spinner_item, tempLineList)
            {

                @Override
                public boolean isEnabled(int position)
                {
                    if (position == 0)
                        return false;
                    else
                        return true;
                }
                @Override
                public View getDropDownView(int position, View convertView, ViewGroup parent)
                {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;

                    if(position==0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(ContextCompat.getColor(getContext(),R.color.colorBlack));
                    }
                    return view;
                }
                @Override
                public View getView(int position, View convertView, ViewGroup parent)
                {
                    TextView tv = (TextView) super.getView(position, convertView, parent);

                    if(position == 0)
                        tv.setTextColor(Color.RED);
                    else
                        tv.setTextColor(Color.BLACK);

                    // Return the view
                    return tv;
                }
            };

            spinnerLines.setAdapter(linesListAdapter);

            Bundle argumentsBundle = getArguments();
            if(argumentsBundle !=null)
            {
                Gson gson = new Gson();
                _isAdd = argumentsBundle.getBoolean("isAdd");

                _datamodeljson = argumentsBundle.getString("datamodel");
                if (_datamodeljson!=null)
                {
                    _datamodel =  gson.fromJson(_datamodeljson, Users.class);
                }

                if (!_isAdd)
                {
                    edit_firstname.setText(_datamodel.firstName);
                    edit_lastname.setText(_datamodel.lastName);
                    edit_username.setText(_datamodel.username);
                    Integer index = 0;
                    for(Lines l:tempLineList)
                    {
                        if (l.getID() == _datamodel.tblLineID)
                        {
                            spinnerLines.setSelection(index);
                            break;
                        }
                        index++;

                    }
                    _username = _datamodel.username;
                    _userID = _datamodel.ID;

                }
                else
                {
                    textPasswordAction.setText("Set password");
                    textLabel.setText("Add New Driver Account");
                    btnDelete.setVisibility(View.GONE);
                }

                SerializableRefreshLayoutComponents swipeRefreshLayoutSerializable =(SerializableRefreshLayoutComponents) argumentsBundle.getSerializable("swipeRefreshLayoutSerializable");
                _swipeRefresh = swipeRefreshLayoutSerializable._swipeRefreshLayoutSerializable;
                _fragmentManager = swipeRefreshLayoutSerializable._fragmentManager;
                _adminUserListView = swipeRefreshLayoutSerializable._listView;

            }




            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setContentView(view);
            checkShowPasswords.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    if (b)
                    {
                        edit_password.setTransformationMethod(null);
                        edit_confirmPassword.setTransformationMethod(null);
                    }
                    else
                    {
                        edit_password.setTransformationMethod(new PasswordTransformationMethod());
                        edit_confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                    }
                }
            });

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoaderDialog UpdateDriverLoader = new LoaderDialog(getActivity(),"Saving changes...","Please wait..." );
                    UpdateDriverLoader.setCancelable(false);

                    String new_username = edit_username.getText().toString();
                    String new_firstname = edit_firstname.getText().toString();
                    String new_lastname = edit_lastname.getText().toString();
                    String password = edit_password.getText().toString();
                    String confirmedPassword = edit_confirmPassword.getText().toString();
                    Integer tblLineID = ((Lines)spinnerLines.getSelectedItem()).getID();

                    UpdateDriverLoader.show();
                    try
                    {
                        if (_isAdd) //If new entry
                        {
                            Users.validateRegistrationDetails(new Users(new_username, Constants.DRIVER_EMAILADDRESS, new_firstname, new_lastname, password, confirmedPassword));
                            if (tblLineID==0)
                            {
                                ErrorDialog errorDialog = new ErrorDialog(getActivity(), "Please select the line of this driver");
                                errorDialog.show();
                                UpdateDriverLoader.dismiss();
                            }
                            else
                                new mySQLUpdateDriverUserDetails(getActivity(),getActivity(),UpdateDriverLoader,"", EditDriverUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Add").execute("0", new_username, new_firstname, new_lastname, password, tblLineID.toString());
                        }
                        else //EDIT
                        {
                            if (password.trim().length()>0) //Check if password is changed
                            {

                                Users.validateRegistrationDetails(new Users(new_username, Constants.DRIVER_EMAILADDRESS, new_firstname, new_lastname, password, confirmedPassword));
                                new mySQLUpdateDriverUserDetails(getActivity(),getActivity(),UpdateDriverLoader,"", EditDriverUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Edit").execute(_userID.toString(), new_username,new_firstname, new_lastname, password, tblLineID.toString());
                            }
                            else
                            {
                                Users.validateRegistrationDetails(new Users(new_username, Constants.DRIVER_EMAILADDRESS, new_firstname, new_lastname, null, null));
                                new mySQLUpdateDriverUserDetails(getActivity(),getActivity(),UpdateDriverLoader,"", EditDriverUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Edit").execute(_userID.toString(), new_username, new_firstname, new_lastname, null, tblLineID.toString());
                            }
                            _datamodel.password = password;

                            _datamodel.username = new_username;

                        }


                    }
                    catch(Exception ex)
                    {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        UpdateDriverLoader.dismiss();
                    }

                }
            });

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder;
                    builder = new AlertDialog.Builder(getActivity());


                    builder.setTitle("Remove User")
                            .setMessage("Are you sure you want to remove this user?")
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        LoaderDialog RemoveUserLoader = new LoaderDialog(getActivity(),"Removing User","Please wait...");
                                        RemoveUserLoader.setCancelable(false);
                                        RemoveUserLoader.show();

                                        new mySQLUpdateDriverUserDetails(getActivity(),getActivity(),RemoveUserLoader,"", EditDriverUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Delete").execute(_userID.toString(), "","","","","");

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




                }
            });

        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }
        return builder;

    }
}
