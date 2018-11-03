package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;

import com.umandalmead.samm_v1.EntityObjects.Users;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Setting;
import com.umandalmead.samm_v1.POJO.HTMLDirections.Settings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * Created by eleazerarcilla on 03/11/2018.
 */

public class asyncGetApplicationSettings extends AsyncTask<Void,Void, JSONObject> {
    private Activity _activity;
    private Context _context;
    private Helper _helper;
    private ArrayList<Setting> _AL_Settings;
    private Constants _constants = new Constants();

    public asyncGetApplicationSettings(Activity activity, Context context){
        this._activity = activity;
        this._context = context;
        this._helper = new Helper(_activity, _context);
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        try {

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context)) {
                String link = _constants.WEB_API_URL + _constants.SETTINGS_API_FOLDER + "getSettings.php";
                URL url = new URL(link);
                URLConnection conn = url.openConnection();

                conn.setDoOutput(true);

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String jsonResponse = reader.readLine();
                JSONArray jsonArray;

                try {

                    return new JSONObject(jsonResponse);
                } catch (Exception ex) {
                    Helper.logger(ex, true);
                    return null;
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            Helper.logger(ex, true);
            return null;

        }

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
        try {
            _AL_Settings = new ArrayList<>();
            Integer i = 0;
            JSONArray jsonArray = jsonObject.getJSONArray("settings");
            for (i = 0; i < jsonArray.length(); i++) {
                JSONObject json = (JSONObject) jsonArray.get(i);

                Setting s= new Setting();
                s.setID(json.getInt("ID"));
                s.setName(json.getString("Name"));
                s.setDescription(json.getString("Description"));
                s.setValue(json.getString("Value"));
                _AL_Settings.add(s);
            }
            MenuActivity._AL_applicationSettings = new ArrayList<>(_AL_Settings);
            for (Setting S_entry:_AL_Settings) {
                if(S_entry.getID()==3){ //3 - ApplicationSubName
                    AboutActivity.SammTV.setText(Html.fromHtml(S_entry.getName()+"<br/><br/>"+S_entry.getDescription()));
                }
                if(S_entry.getID()==5){ //5-Version
                    AboutActivity.TV_SammLatestVersion.setText("("+(BuildConfig.VERSION_NAME.equals(S_entry.getValue()) ? "Up to date"
                            : "Latest version available: "+S_entry.getValue())+")");
                    AboutActivity._SL_TV_LatestVersion.startShimmerAnimation();
                }
            }
        } catch (Exception ex) {
            Helper.logger(ex);
        }
    }
}
