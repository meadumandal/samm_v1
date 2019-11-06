package com.evolve.evx;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.Html;
import android.view.View;

import com.evolve.evx.POJO.HTMLDirections.Setting;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

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
                String link = _constants.WEB_API_URL + _constants.SETTINGS_API_FOLDER + _constants.SETTINGS_API_FILE;
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
                    AboutActivity.SammTV.setText(Html.fromHtml(S_entry.getValue()));
            }
            if (S_entry.getID() == 5) { //5-Version
                //String ThisAppBuildVersion = "1.0.3", LatestAppVersionFromServer = "1.0.3";
                String ThisAppBuildVersion = BuildConfig.VERSION_NAME, LatestAppVersionFromServer = S_entry.getValue();
                Boolean IsUpdated = ThisAppBuildVersion.equals(LatestAppVersionFromServer);
                if (!_BOOL_isAdhoc) {
                    Boolean IsThisBuildGreaterThanServerBuild = CheckThisBuildAgainstServerBuild(ThisAppBuildVersion,LatestAppVersionFromServer);
                    AboutActivity.TV_SammLatestVersion.setText("(" + (IsUpdated ? MenuActivity._GlobalResource.getString(R.string.info_up_to_date)
                            : (IsThisBuildGreaterThanServerBuild ? MenuActivity._GlobalResource.getString(R.string.info_beta_version)
                            :MenuActivity._GlobalResource.getString(R.string.info_latest_version_available)) +" "+ S_entry.getValue()) + ")");
                    AboutActivity._SL_TV_LatestVersion.startShimmerAnimation();
                } else {
                    if (!IsUpdated)
                        ShowAppVersionInfo(ThisAppBuildVersion, LatestAppVersionFromServer);
                }
            }
            if(S_entry.getID() == 6){ //6 - Notice/Announcements
                if(S_entry.getValue() !=null && !S_entry.getValue().trim().equals("")){
                    //String format: IsPersitent|IsMaintenance|Title|Details|URL
                    NoticeDialog ND_Notice = new NoticeDialog(_activity, "Notice",S_entry.getValue());
                    ND_Notice.show();
                }
            }
            if(S_entry.getID()==7){ //7-IsPlateNumberVisible
                if(S_entry.getValue()!=null && !S_entry.getValue().trim().equals("")){
                    if(S_entry.getValue().equalsIgnoreCase("1"))
                        MenuActivity._BOOL_IsPlateNumberVisible = true;
                }

            }
        }
    }
    public void ShowAppVersionInfo(String ThisAppBuildVersion, String LatestAppVersionFromServer){
        try {
            Boolean IsThisBuildGreaterThanServerBuild = CheckThisBuildAgainstServerBuild(ThisAppBuildVersion,LatestAppVersionFromServer);
            if(!IsThisBuildGreaterThanServerBuild){
               final InfoDialog ID_UpdateRequired = new InfoDialog(_activity,MenuActivity._GlobalResource.getString(R.string.info_patch_is_available));
                ID_UpdateRequired.show();
                ID_UpdateRequired.setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        final String appPackageName = _activity.getPackageName();
                        try {
                            _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(_constants.PLAY_STORE_MARKET_URI + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(_constants.PLAY_STORE_URI_WITH_QUERYSTRING + appPackageName)));
                        }
                        ID_UpdateRequired.show();
                    }
                });
            }
            if (MenuActivity._BOOL_IsGoogleMapShownAndAppIsOnHomeScreen && MenuActivity._BOOL_IsGPSAcquired) {
                MenuActivity._AppBarLayout.height = _helper.dpToPx(20, _context);
                MenuActivity._AppBar.setLayoutParams(MenuActivity._AppBarLayout);
                MenuActivity._AppBar.setVisibility(View.VISIBLE);
                MenuActivity._DestinationTextView.setVisibility(View.VISIBLE);
                MenuActivity._DestinationTextView.setBackgroundResource(IsThisBuildGreaterThanServerBuild ? R.color.colorBrightYellow :R.color.colorNasturcianFlower);
                MenuActivity._DestinationTextView.setTextColor(IsThisBuildGreaterThanServerBuild ? _activity.getApplication().getResources().getColor(R.color.colorBlack)
                        : _activity.getApplication().getResources().getColor(R.color.colorWhite));
                MenuActivity._DestinationTextView.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
                MenuActivity._DestinationTextView.setText(IsThisBuildGreaterThanServerBuild ?
                        MenuActivity._GlobalResource.getString(R.string.info_using_beta_version) : MenuActivity._GlobalResource.getString(R.string.info_outdated_version));
                MenuActivity._DestinationTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final String appPackageName = _activity.getPackageName();
                        try {
                            _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(_constants.PLAY_STORE_MARKET_URI + appPackageName)));
                        } catch (android.content.ActivityNotFoundException anfe) {
                            _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(_constants.PLAY_STORE_URI_WITH_QUERYSTRING + appPackageName)));
                        }
                    }
                });
            }
        }catch (Exception ex){
            _helper.logger(ex);
        }
    }
    private Boolean CheckThisBuildAgainstServerBuild(String ThisAppBuildVersion, String LatestAppVersionFromServer){
        try{
            List<Integer> AL_ThisAppBuildVersion = new ArrayList<Integer>();
            List<Integer> AL_LatestAppVersionFromServer = new ArrayList<Integer>();
            for (String STR_entry : ThisAppBuildVersion.split("\\.")) {
                AL_ThisAppBuildVersion.add(Integer.parseInt(STR_entry));
            }
            for (String STR_entry : LatestAppVersionFromServer.split("\\.")) {
                AL_LatestAppVersionFromServer.add(Integer.parseInt(STR_entry));
            }
            if(!ThisAppBuildVersion.equals(LatestAppVersionFromServer)) {
                for (Integer i = 0; i != AL_ThisAppBuildVersion.size(); i++) {
                    if(AL_ThisAppBuildVersion.get(i)==AL_LatestAppVersionFromServer.get(i))
                        continue;
                    if(AL_ThisAppBuildVersion.get(i)>AL_LatestAppVersionFromServer.get(i))
                        return true;
                    else
                        return false;

                }
            }

        }catch (Exception ex)
        {
            _helper.logger(ex);
        }
        return false;
    }
}
