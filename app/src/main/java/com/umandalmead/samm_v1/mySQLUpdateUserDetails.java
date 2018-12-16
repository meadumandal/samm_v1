package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.umandalmead.samm_v1.MenuActivity._UserNameMenuItem;


/**
 * Created by MeadRoseAnn on 06/20/2018
 */


public class mySQLUpdateUserDetails extends AsyncTask<String, Void, Void>{
    /**
     *
     * This updates the movement of passengers in mySQL Database
     * @param context
     * @param _activity
     * @param progressMessage Message that will appear in UI while processing
     * @param map Pass the main map of the app, so the asynctask will be able to pin the location of destinations
     */
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    String _returnMessageOfUpdatingTheUserDetails,
            _returnMessageOfUpdatingThePassword,
            _newFirstName,
            _newLastName,
            _currentPassword,
            _newPassword,
            _confirmPassword;
    SessionManager _sessionManager;
    Boolean _updatePassword, _isUpdatingOfUserDetailsSuccessful = false, _isUpdatingOfPasswordSuccesful = false;
    private Constants _constants = new Constants();

    public mySQLUpdateUserDetails(Context context, Activity activity, LoaderDialog loaderDialog, Boolean updatePassword)
    {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._updatePassword = updatePassword;
        _sessionManager = new SessionManager(_context);
    }

    @Override
    protected void onPreExecute()
    {
        try
        {
            super.onPreExecute();
            _LoaderDialog.show();
        }
        catch(Exception ex)
        {
            Helper.logger(ex,true);
        }


    }
    @Override
    protected Void doInBackground(String... params)
    {
        String username = params[0];
        _newFirstName = params[1];
        _newLastName = params[2];
        if (_updatePassword)
        {
            _currentPassword = params[3];
            _newPassword = params[4];
            _confirmPassword = params[5];
        }


        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context))
        {
            try{
                String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + _constants.USERS_UPDATE_API_FILE_WITH_PENDING_QUERYSTRING;
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(link);
                List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                postParameters.add(new BasicNameValuePair("username", username));
                postParameters.add(new BasicNameValuePair("firstName", _newFirstName));
                postParameters.add(new BasicNameValuePair("lastName", _newLastName));
                httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                HttpResponse response = httpClient.execute(httpPost);
                String strResponse = EntityUtils.toString(response.getEntity());
                JSONObject json = new JSONObject(strResponse);

                _returnMessageOfUpdatingTheUserDetails = json.getString("message") +  "\n";
                _isUpdatingOfUserDetailsSuccessful = true;
            }
            catch(Exception ex)
            {
                _returnMessageOfUpdatingTheUserDetails = ex.getMessage();
                _isUpdatingOfUserDetailsSuccessful = false;
            }
        }
        else
        {
            _returnMessageOfUpdatingTheUserDetails = MenuActivity._GlobalResource.getString(R.string.Error_looks_like_your_offline);
            _isUpdatingOfUserDetailsSuccessful = false;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void voids)
    {
        if (_isUpdatingOfUserDetailsSuccessful)
        {
            //User Details are successfully updated, proceed with updating of password
            _sessionManager.setFirstName(_newFirstName);
            _sessionManager.setLastName(_newLastName);
            _UserNameMenuItem.setTitle(_sessionManager.getFullName().toUpperCase());
            MenuActivity._HeaderUserFullName.setText(_sessionManager.getFullName().toUpperCase());
            UserProfileActivity.tv_NameDisplay.setText(_sessionManager.getFullName().toUpperCase());
            if (_updatePassword)
            {
                ChangePassword(_currentPassword, _newPassword, _confirmPassword);
            }
            else
            {
                InfoDialog infoDialog = new InfoDialog(_activity, "Account details are successfully updated");
                infoDialog.show();
                _LoaderDialog.hide();
            }

        }
        else
        {
            //User details are not successfully updated, do not proceed to change password
            ErrorDialog errorDialog = new ErrorDialog(_activity, _returnMessageOfUpdatingTheUserDetails);
            errorDialog.show();
            _LoaderDialog.hide();
        }




    }
    private void ChangePassword(String currentPassword, final String newPassword, String confirmPassword)
    {

        if(newPassword.equals(confirmPassword))
        {
            if(newPassword.length() < 6)
            {
                _returnMessageOfUpdatingThePassword = MenuActivity._GlobalResource.getString(R.string.error_shortpassword);
                _isUpdatingOfPasswordSuccesful = false;
                ErrorDialog errorDialog = new ErrorDialog(_activity, _returnMessageOfUpdatingThePassword);
                errorDialog.show();
                _LoaderDialog.hide();
            }
            else {

                final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(_sessionManager.getEmail(), currentPassword);

                // Prompt the user to re-provide their sign-in credentials
                user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            _returnMessageOfUpdatingThePassword = " Password updated\n";
                                            _isUpdatingOfPasswordSuccesful = true;
                                            LogoutUser();
                                        } else {
                                            _returnMessageOfUpdatingThePassword = "Error updating password\n";
                                            _isUpdatingOfPasswordSuccesful = false;
                                            ErrorDialog errorDialog = new ErrorDialog(_activity, _returnMessageOfUpdatingThePassword);
                                            errorDialog.show();

                                            _LoaderDialog.hide();
                                        }
                                    }
                                });
                            } else {
                                _returnMessageOfUpdatingThePassword = task.getException().getMessage().toString();
                                _isUpdatingOfPasswordSuccesful = false;
                                ErrorDialog errorDialog = new ErrorDialog(_activity, _returnMessageOfUpdatingThePassword);
                                errorDialog.show();

                                _LoaderDialog.hide();
                            }
                        }
                    });
            }

        }
    }
    public void LogoutUser(){
        InfoDialog ID_Logged_Out = new InfoDialog(_activity, MenuActivity._GlobalResource.getString(R.string.USER_logged_out_password_changed));
        ID_Logged_Out.show();
        ID_Logged_Out.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                _sessionManager.logoutUser();
                Activity activity = _activity;
                activity.overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                activity.startActivity(new Intent(_activity, LoginActivity.class));
                activity.finish();
            }
        });
    }
}