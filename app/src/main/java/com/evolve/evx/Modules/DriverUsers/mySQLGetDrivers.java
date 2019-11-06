package com.evolve.evx.Modules.DriverUsers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.evolve.evx.Constants;
import com.evolve.evx.EntityObjects.Users;
import com.evolve.evx.Helper;
import com.evolve.evx.LoaderDialog;
import com.evolve.evx.MenuActivity;
import com.evolve.evx.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by eleazerarcilla on 27/10/2018.
 */

public class mySQLGetDrivers extends AsyncTask<Void, Void, JSONArray> {
    private Context _context;
    private Activity _activity;
    private LoaderDialog _loader;
    private ArrayList<Users> _AL_Users;
    private Constants _constants = new Constants();

    public mySQLGetDrivers(Activity activity, Context context) {
        this._activity = activity;
        this._context = context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _loader = new LoaderDialog(_activity, "Fetching driver info", MenuActivity._GlobalResource.getString(R.string.dialog_please_wait_with_ellipsis));
        _loader.show();
    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        try {

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context)) {
                String link = _constants.WEB_API_URL + _constants.USERS_API_FOLDER + "getUsersByType.php?usertype=driver";
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray;

                try {

                    return new JSONArray(jsonResponse);
                } catch (Exception ex) {
                    if (_loader != null)
                        _loader.dismiss();
                    Helper.logger(ex, true);
                    return null;
                }
            } else {
                if (_loader != null)
                    _loader.dismiss();
                return null;
            }
        } catch (Exception ex) {
            Helper.logger(ex, true);
            if (_loader != null)
                _loader.dismiss();
            return null;

        }

    }

    @Override
    protected void onPostExecute(JSONArray jsonArray) {
        super.onPostExecute(jsonArray);
        try {
            _AL_Users = new ArrayList<>();
            Integer i = 0;
            for (i = 0; i < jsonArray.length(); i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);

                int ID = json.getInt("ID");
                String username = json.getString("username");
                String emailAddress = json.getString("emailAddress");
                String firstName = json.getString("firstName");
                String lastName = json.getString("lastName");
                String userType = json.getString("userType");
                String password = json.getString("password");
                int IsActive = json.getInt("IsActive");

                _AL_Users.add(new Users(ID, username, emailAddress, firstName, lastName, userType, password, IsActive));
            }
            MenuActivity._driverList = new ArrayList<>(_AL_Users);
            _loader.dismiss();
        } catch (Exception ex) {
            _loader.dismiss();
            Helper.logger(ex);
        }
    }
}
