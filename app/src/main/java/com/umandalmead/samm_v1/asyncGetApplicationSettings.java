package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.view.Menu;
import android.view.View;

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
    private Boolean _BOOL_isAdhoc;

    public asyncGetApplicationSettings(Activity activity, Context context, Boolean IsAdhoc){
        this._activity = activity;
        this._context = context;
        this._helper = new Helper(_activity, _context);
        this._BOOL_isAdhoc = IsAdhoc;
    }

    @Override
    protected JSONObject doInBackground(Void... voids) {
        try {

            Helper helper = new Helper();
            if (helper.isConnectedToInternet(this._context) && MenuActivity._AL_applicationSettings==null) {
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
            if(MenuActivity._AL_applicationSettings==null) {
                _AL_Settings = new ArrayList<>();
                Integer i = 0;
                JSONArray jsonArray = jsonObject.getJSONArray("settings");
                for (i = 0; i < jsonArray.length(); i++) {
                    JSONObject json = (JSONObject) jsonArray.get(i);

                    Setting s = new Setting();
                    s.setID(json.getInt("ID"));
                    s.setName(json.getString("Name"));
                    s.setDescription(json.getString("Description"));
                    s.setValue(json.getString("Value"));
                    _AL_Settings.add(s);
                }
                MenuActivity._AL_applicationSettings = new ArrayList<>(_AL_Settings);
                ProcessSettings();
            }else {
                ProcessSettings();
            }
        } catch (Exception ex) {
            Helper.logger(ex);
        }
    }
    private void ProcessSettings(){
        for (Setting S_entry : MenuActivity._AL_applicationSettings) {
            if (S_entry.getID() == 3) { //3 - ApplicationSubName
                if (!_BOOL_isAdhoc)
                    AboutActivity.SammTV.setText(Html.fromHtml(S_entry.getName() + "<br/><br/>" + S_entry.getDescription()));
            }
            if (S_entry.getID() == 5) { //5-Version
                Boolean IsUpdated = BuildConfig.VERSION_NAME.equals(S_entry.getValue());
                if (!_BOOL_isAdhoc) {
                    AboutActivity.TV_SammLatestVersion.setText("(" + (IsUpdated ? "Up to date"
                            : "Latest version available: " + S_entry.getValue()) + ")");
                    AboutActivity._SL_TV_LatestVersion.startShimmerAnimation();
                } else {
                    if (!IsUpdated)
                        ShowOutdatedAppInfo();
                }
            }
        }
    }
    public void ShowOutdatedAppInfo(){
        if(MenuActivity._BOOL_IsGoogleMapShownAndAppIsOnHomeScreen) {
            MenuActivity._AppBarLayout.height = _helper.dpToPx(20, _context);
            MenuActivity._AppBar.setLayoutParams(MenuActivity._AppBarLayout);
            MenuActivity._AppBar.setVisibility(View.VISIBLE);
            MenuActivity._DestinationTextView.setVisibility(View.VISIBLE);
            MenuActivity._DestinationTextView.setBackgroundResource(R.color.colorNasturcianFlower);
            MenuActivity._DestinationTextView.setText("This version is outdated. Tap to update.");
            MenuActivity._DestinationTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String appPackageName = _activity.getPackageName(); // getPackageName() from Context or Activity object
                    try {
                        _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                    } catch (android.content.ActivityNotFoundException anfe) {
                        _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                    }
                }
            });
        }
    }
}
