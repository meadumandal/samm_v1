package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.Menu;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;
import com.umandalmead.samm_v1.EntityObjects.GPS;
import com.umandalmead.samm_v1.EntityObjects.Terminal;

import java.util.List;

/**
 * Created by eleazerarcilla on 21/10/2018.
 */

public class asyncProcessSelectedDestination extends AsyncTask<Void, Void, Terminal>{
    Context _context;
    Activity _activity;
    LoaderDialog _LoaderDialog;
    String progressMessage;
    public static String TAG="mead";
    private Constants _constants = new Constants();
    private Terminal _ChosenTerminal;
    private List<Terminal> _TerminalList;
    private  Place _place;
    private Boolean _IsPossibleTerminalsEmpty, _IsOnline = false;
    Helper _helper;


    public asyncProcessSelectedDestination(Activity activity, Context context, List<Terminal> terminalList, Place place){
        this._activity = activity;
        this._context = context;
        this._TerminalList = terminalList;
        this._LoaderDialog = new LoaderDialog(_activity,"Searching","Please wait...");
        this._place = place;
        this._helper = new Helper(_activity,_context);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        _LoaderDialog.show();
        new asyncCheckInternetConnectivity(_activity);
    }

    @Override
    protected Terminal doInBackground(Void... voids) {
        try {
            double prevDistance = 0.0;
            int ctr = 0;
            if(_TerminalList.size()>0) {
                for (Terminal dest : _TerminalList) {
                    double tempDistance;
                    LatLng destLatLng = new LatLng(dest.Lat, dest.Lng);
                    LatLng searchLatLng = _place.getLatLng();
                    tempDistance = _helper.getDistanceFromLatLonInKm(destLatLng, searchLatLng);
                    if (ctr == 0) {
                        prevDistance = tempDistance;
                        _ChosenTerminal = dest;
                    } else {
                        if (tempDistance <= prevDistance) {
                            prevDistance = tempDistance;
                            _ChosenTerminal = dest;
                        }
                    }
                    ctr++;
                }
                _IsPossibleTerminalsEmpty = _helper.FindNearestPickUpPoints(_ChosenTerminal,_activity,_context);
            }

        }catch (Exception ex){
            Helper.logger(ex);
        }
        return _ChosenTerminal;
    }

    @Override
    protected void onPostExecute(Terminal terminal) {
             _LoaderDialog.dismiss();
            if (!_IsPossibleTerminalsEmpty) {
                new AnalyzeForBestRoutes(_context, _activity,
                        MenuActivity._googleMap, MenuActivity._userCurrentLoc,
                        MenuActivity._possiblePickUpPointList, terminal)
                        .execute();
            } else {
                Toast.makeText(_context, "Unable to process, data returned was empty!", Toast.LENGTH_SHORT).show();
            }

    }
}
