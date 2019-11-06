package com.evolve.evx.Modules.SuperAdminUsers;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.evolve.evx.Constants;
import com.evolve.evx.ErrorDialog;
import com.evolve.evx.Helper;
import com.evolve.evx.InfoDialog;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.NonScrollListView;
import com.evolve.evx.SessionManager;

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

import static com.evolve.evx.MenuActivity._UserNameMenuItem;

/**
 * Created by MeadRoseAnn on 7/30/2018.
 */

public class mySQLUpdateSuperAdminUserDetails extends AsyncTask<String, Void, String> {
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    String _promptMessage;
    Boolean _status;
    String newusername, newfirstname, newEmailAddress, newlastname, newpassword, currentPassword, confirmPassword;
    Integer userID;
    SessionManager _sessionManager;
    EditSuperAdminUserDialogFragment _editDialog;
    SwipeRefreshLayout _swipeRefreshLayout;
    NonScrollListView _adminUserListView;
    String _action;
    Helper _helper = new Helper();
    private Constants _constants = new Constants();

    public mySQLUpdateSuperAdminUserDetails(Context context, Activity activity, LoaderDialog loaderDialog, String promptMessage, EditSuperAdminUserDialogFragment editDialog, SwipeRefreshLayout swipeRefreshLayout, NonScrollListView adminUserListView, String action) {
        this._context = context;
        this._activity = activity;
        this._LoaderDialog = loaderDialog;
        this._promptMessage = promptMessage;
        this._editDialog = editDialog;
        this._swipeRefreshLayout = swipeRefreshLayout;
        this._adminUserListView = adminUserListView;
        _sessionManager = new SessionManager(_context);
        this._action = action;
    }

    @Override
    protected void onPreExecute() {
        try {
            super.onPreExecute();
        } catch (Exception ex) {
            Helper.logger(ex, true);
        }


    }

    @Override
    protected String doInBackground(String... params) {
        userID = Integer.parseInt(params[0]);
        newusername = params[1];
        newEmailAddress = params[2];
        newfirstname = params[3];
        newlastname = params[4];
        newpassword = params[5];

        JSONObject json = new JSONObject();

        Helper helper = new Helper();
        if (helper.isConnectedToInternet(this._context)) {
            try {
                if (this._action.equals("Add")) {

                    String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + "add.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("username", newusername));
                    postParameters.add(new BasicNameValuePair("firstname", newfirstname));
                    postParameters.add(new BasicNameValuePair("lastname", newlastname));
                    postParameters.add(new BasicNameValuePair("emailaddress", newEmailAddress));
                    postParameters.add(new BasicNameValuePair("password", newpassword));
                    postParameters.add(new BasicNameValuePair("usertype", Constants.SUPERADMIN_USERTYPE));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    String strResponse = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(strResponse);
                    if (json.getString("status").equals("1")) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.createUserWithEmailAndPassword(newEmailAddress, newpassword)
                                .addOnCompleteListener(_activity, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {

                                        if (!task.isSuccessful()) {

                                        } else {

                                        }
                                        ;

                                    }
                                });
                    }

                } else if (this._action.equals("Delete")) {
                    String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + "delete.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("userID", userID.toString()));
                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    String strResponse = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(strResponse);
                } else if (this._action.equals("Edit")) {
                    String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + "edit.php?";
                    HttpClient httpClient = new DefaultHttpClient();
                    HttpPost httpPost = new HttpPost(link);
                    List<NameValuePair> postParameters = new ArrayList<NameValuePair>(4);
                    postParameters.add(new BasicNameValuePair("userID", userID.toString()));
                    postParameters.add(new BasicNameValuePair("newusername", newusername));
                    postParameters.add(new BasicNameValuePair("newfirstname", newfirstname));
                    postParameters.add(new BasicNameValuePair("newlastname", newlastname));
                    if (newpassword != null) {
                        postParameters.add(new BasicNameValuePair("newpassword", newpassword));
                    }

                    httpPost.setEntity(new UrlEncodedFormEntity(postParameters));
                    HttpResponse response = httpClient.execute(httpPost);
                    String strResponse = EntityUtils.toString(response.getEntity());
                    json = new JSONObject(strResponse);


                }
                if (json.getString("status").equals("1"))
                    _status = true;
                else
                    _status = false;
                _promptMessage += json.getString("message") + "\n";


            } catch (Exception ex) {
                _helper.logger(ex, true);
            }
        } else {
            Toast.makeText(this._context, "Looks like you're offline", Toast.LENGTH_LONG).show();
            _LoaderDialog.hide();

        }
        return _promptMessage;
    }

    @Override
    protected void onPostExecute(String param) {

        try {
            _LoaderDialog.hide();
            _UserNameMenuItem.setTitle(_sessionManager.getFullName().toUpperCase());

            if (_status) {

                if (newpassword != null && this._action.equalsIgnoreCase("edit")) {
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    AuthCredential credential = EmailAuthProvider
                            .getCredential(newEmailAddress, newpassword);

                    // Prompt the user to re-provide their sign-in credentials
                    user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    user.updatePassword(newpassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                finishSaving();
                                            } else {
                                                ErrorDialog errorDialog = new ErrorDialog(MenuActivity._activity, _promptMessage);
                                                errorDialog.show();
                                            }
                                        }
                                    });
                                } else {
                                    ErrorDialog errorDialog = new ErrorDialog(MenuActivity._activity, _promptMessage);
                                    errorDialog.show();

                                }
                            }
                        });
                } else {
                    finishSaving();
                }

            } else {
                ErrorDialog errorDialog = new ErrorDialog(MenuActivity._activity, _promptMessage);
                errorDialog.show();

            }
        } catch (Exception ex) {
            Helper.logger(ex, true);
        }


    }

    private void finishSaving() {
        InfoDialog dialog = new InfoDialog(this._activity, "Super Admin user details are updated with the following message/s:\n\n" + _promptMessage);

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        _editDialog.dismiss();
        _swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                _swipeRefreshLayout.setRefreshing(true);
                FragmentManager fm = _activity.getFragmentManager();
                new mySQLGetSuperAdminUsers(_activity, _LoaderDialog, _adminUserListView, fm, _swipeRefreshLayout).execute();


            }
        });
        _swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                _swipeRefreshLayout.setRefreshing(true);
                FragmentManager fm = _activity.getFragmentManager();
                new mySQLGetSuperAdminUsers(_activity, _LoaderDialog, _adminUserListView, fm, _swipeRefreshLayout).execute();

            }
        });
    }
}