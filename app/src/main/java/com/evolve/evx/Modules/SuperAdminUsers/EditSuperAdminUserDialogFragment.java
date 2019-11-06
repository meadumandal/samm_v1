package com.evolve.evx.Modules.SuperAdminUsers;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
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
import com.evolve.evx.EntityObjects.Users;
import com.evolve.evx.ErrorDialog;
import com.evolve.evx.Helper;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.R;
import com.evolve.evx.SerializableRefreshLayoutComponents;

/**
 * Created by MeadRoseAnn on 7/30/2018.
 */

public class EditSuperAdminUserDialogFragment extends DialogFragment {
    //private View pic;
    String TAG="mead", _datamodeljson, _username;
    Integer _userID;
    Users _datamodel;
    SwipeRefreshLayout _swipeRefresh;
    FragmentManager _fragmentManager;
    NonScrollListView _adminUserListView;
    Boolean _isAdd;



    public EditSuperAdminUserDialogFragment()
    {

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        Dialog builder = new Dialog(getActivity());
        try
        {

            View view = getActivity().getLayoutInflater().inflate(R.layout.fragmentdialog_edit_admin_user, new LinearLayout(getActivity()), false);

            final EditText edit_firstname = view.findViewById(R.id.firstname);
            final EditText edit_lastname =  view.findViewById(R.id.lastname);
            final EditText edit_username = view.findViewById(R.id.username);
            final EditText edit_emailAddress = view.findViewById(R.id.emailaddress);
            final EditText edit_password = view.findViewById(R.id.password);
            final TextView textLabel = view.findViewById(R.id.txtActionLabel);
            final EditText edit_confirmPassword = view.findViewById(R.id.confirmPassword);
            final TextView textPasswordAction = view.findViewById(R.id.txtPasswordAction);

            Button btnUpdate = view.findViewById(R.id.btnUpdateAdminUser);
            Button btnDelete = view.findViewById(R.id.btnDeleteAdminUser);


            edit_firstname.setTypeface(Helper.FONT_RUBIK_REGULAR);
            edit_lastname.setTypeface(Helper.FONT_RUBIK_REGULAR);
            edit_username.setTypeface(Helper.FONT_RUBIK_REGULAR);
            edit_emailAddress.setTypeface(Helper.FONT_RUBIK_REGULAR);
            edit_password.setTypeface(Helper.FONT_RUBIK_REGULAR);
            textLabel.setTypeface(Helper.FONT_RUBIK_REGULAR);
            edit_confirmPassword.setTypeface(Helper.FONT_RUBIK_REGULAR);
            btnUpdate.setTypeface(Helper.FONT_RUBIK_REGULAR);
            btnDelete.setTypeface(Helper.FONT_RUBIK_REGULAR);

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
                    edit_emailAddress.setText(_datamodel.emailAddress);
                    textPasswordAction.setText("Change Password");
                    edit_emailAddress.setVisibility(View.GONE);
                    textPasswordAction.setVisibility(View.GONE);
                    edit_password.setVisibility(View.GONE);
                    edit_confirmPassword.setVisibility(View.GONE);
                }
                else
                {
                    textLabel.setText("Add New SuperAdmin Users");
                    textPasswordAction.setText("Set Password");

                    btnDelete.setVisibility(View.GONE);
                }
                _username = _datamodel.username;
                _userID = _datamodel.ID;
                SerializableRefreshLayoutComponents swipeRefreshLayoutSerializable =(SerializableRefreshLayoutComponents) argumentsBundle.getSerializable("swipeRefreshLayoutSerializable");
                _swipeRefresh = swipeRefreshLayoutSerializable._swipeRefreshLayoutSerializable;
                _fragmentManager = swipeRefreshLayoutSerializable._fragmentManager;
                _adminUserListView = swipeRefreshLayoutSerializable._listView;

            }




            builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
            builder.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            builder.setContentView(view);

            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LoaderDialog SaveChangesLoader = new LoaderDialog(getActivity(), "Saving changes...","Please wait...");
                    SaveChangesLoader.setCancelable(false);

                    String new_username = edit_username.getText().toString();
                    String new_emailAddress = edit_emailAddress.getText().toString();
                    String new_firstname = edit_firstname.getText().toString();
                    String new_lastname = edit_lastname.getText().toString();
                    String password = edit_password.getText().toString();
                    String confirmedPassword = edit_confirmPassword.getText().toString();

                    SaveChangesLoader.show();
                    try
                    {
                        if (_isAdd) //If new entry
                        {
                            Users.validateRegistrationDetails(new Users(new_username, new_emailAddress, new_firstname, new_lastname, password, confirmedPassword));
                            new mySQLUpdateSuperAdminUserDetails(getActivity(),getActivity(),SaveChangesLoader,"", EditSuperAdminUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Add").execute("0", new_username, new_emailAddress, new_firstname, new_lastname, password);

                        }
                        else //EDIT
                        {
                            if (password.trim().length()>0) //Check if password is changed
                            {
                                if (!password.equals(confirmedPassword))
                                {
                                    ErrorDialog errorDialog = new ErrorDialog(MenuActivity._activity, "Passwords do not match!");
                                    errorDialog.show();
                                    SaveChangesLoader.hide();
                                    return;
                                }
                                if (password.trim().length()<6)
                                {
                                    ErrorDialog errorDialog = new ErrorDialog(MenuActivity._activity, MenuActivity._GlobalResource.getString(R.string.error_shortpassword));
                                    errorDialog.show();
                                    SaveChangesLoader.hide();
                                    return;
                                }
                                Users.validateRegistrationDetails(new Users(new_username, new_emailAddress, new_firstname, new_lastname, password, confirmedPassword));
                                new mySQLUpdateSuperAdminUserDetails(getActivity(),getActivity(),SaveChangesLoader,"", EditSuperAdminUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Edit").execute(_userID.toString(), new_username,new_emailAddress, new_firstname, new_lastname, password);
                            }
                            else
                            {
                                Users.validateRegistrationDetails(new Users(new_username, new_emailAddress, new_firstname, new_lastname, null, null));
                                new mySQLUpdateSuperAdminUserDetails(getActivity(),getActivity(),SaveChangesLoader,"", EditSuperAdminUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Edit").execute(_userID.toString(), new_username,new_emailAddress, new_firstname, new_lastname, null);
                            }

                        }

                        _datamodel.password = password;

                        _datamodel.username = new_username;
                    }
                    catch(Exception ex)
                    {
                        Toast.makeText(getActivity(), ex.getMessage(), Toast.LENGTH_LONG).show();
                        SaveChangesLoader.dismiss();
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
                                        LoaderDialog RemoveUserLoader= new LoaderDialog(getActivity(), "Removing User","Please wait...");
                                        RemoveUserLoader.setCancelable(false);
                                        RemoveUserLoader.show();

                                        new mySQLUpdateSuperAdminUserDetails(getActivity(),getActivity(),RemoveUserLoader,"", EditSuperAdminUserDialogFragment.this, _swipeRefresh, _adminUserListView, "Delete").execute(_userID.toString(), "","","","","");


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
