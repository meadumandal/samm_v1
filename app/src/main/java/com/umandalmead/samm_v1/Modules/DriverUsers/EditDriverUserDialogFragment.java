package com.umandalmead.samm_v1.Modules.DriverUsers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.Helper;
import com.umandalmead.samm_v1.NonScrollListView;
import com.umandalmead.samm_v1.R;
import com.umandalmead.samm_v1.SerializableRefreshLayoutComponents;

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
            final EditText edit_confirmPassword = (EditText) view.findViewById(R.id.confirmPassword);
            Button btnUpdate = (Button) view.findViewById(R.id.btnUpdateDriverUser);
            Button btnDelete = (Button) view.findViewById(R.id.btnDeleteDriverUser);

            Bundle argumentsBundle = getArguments();
            if(argumentsBundle !=null)
            {
                Gson gson = new Gson();
                _isAdd = argumentsBundle.getBoolean("isAdd");

                _datamodeljson = argumentsBundle.getString("datamodel");
                _datamodel =  gson.fromJson(_datamodeljson, Users.class);
                if (!_isAdd)
                {
                    edit_firstname.setText(_datamodel.firstName);
                    edit_lastname.setText(_datamodel.lastName);
                    edit_username.setText(_datamodel.username);
                }
                else
                {
                    textLabel.setText("Add New Driver User");

                    btnDelete.setVisibility(View.GONE);
                }
                _username = _datamodel.username;
                _userID = _datamodel.ID;
                SerializableRefreshLayoutComponents swipeRefreshLayoutSerializable =(SerializableRefreshLayoutComponents) argumentsBundle.getSerializable("swipeRefreshLayoutSerializable");
                _swipeRefresh = swipeRefreshLayoutSerializable._swipeRefreshLayoutSerializable;
                _fragmentManager = swipeRefreshLayoutSerializable._fragmentManager;
                _adminUserListView = swipeRefreshLayoutSerializable._adminUsersListView;

            }




            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setContentView(view);

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ProgressDialog progDialog = new ProgressDialog(getActivity());
                    progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progDialog.setTitle("Saving changes...");
                    progDialog.setMessage("Please wait...");
                    progDialog.setCancelable(false);

                    String new_username = edit_username.getText().toString();
                    String new_firstname = edit_firstname.getText().toString();
                    String new_lastname = edit_lastname.getText().toString();
                    String password = edit_password.getText().toString();
                    String confirmedPassword = edit_confirmPassword.getText().toString();

                    progDialog.show();
                    try
                    {
                        if (_isAdd) //If new entry
                        {
                            Users.validateRegistrationDetails(new Users(new_username, null, new_firstname, new_lastname, password, confirmedPassword));
                            new mySQLUpdateDriverUserDetails(getActivity(),getActivity(),progDialog,"", EditDriverUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Add").execute("0", new_username, new_firstname, new_lastname, password);

                        }
                        else //EDIT
                        {
                            if (password.trim().length()>0) //Check if password is changed
                            {

                                Users.validateRegistrationDetails(new Users(new_username, null, new_firstname, new_lastname, password, confirmedPassword));
                                new mySQLUpdateDriverUserDetails(getActivity(),getActivity(),progDialog,"", EditDriverUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Edit").execute(_userID.toString(), new_username,new_firstname, new_lastname, password);
                            }
                            else
                            {
                                Users.validateRegistrationDetails(new Users(new_username, null, new_firstname, new_lastname, null, null));
                                new mySQLUpdateDriverUserDetails(getActivity(),getActivity(),progDialog,"", EditDriverUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Edit").execute(_userID.toString(), new_username, new_firstname, new_lastname, null);
                            }

                        }

                        _datamodel.password = password;

                        _datamodel.username = new_username;
                    }
                    catch(Exception ex)
                    {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        progDialog.dismiss();
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
                                        ProgressDialog progDialog = new ProgressDialog(getActivity());
                                        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                                        progDialog.setTitle("Removing User");
                                        progDialog.setMessage("Please wait...");
                                        progDialog.setCancelable(false);
                                        progDialog.show();

                                        new mySQLUpdateDriverUserDetails(getActivity(),getActivity(),progDialog,"", EditDriverUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Delete").execute(_userID.toString(), "","","","");

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




                }
            });

        }
        catch(Exception ex)
        {
            Helper.logger(ex);
        }
        return builder;

    }
}
