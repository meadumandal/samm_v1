package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.umandalmead.samm_v1.EntityObjects.Terminal;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by eleazerarcilla on 21/10/2018.
 */

public class asyncGenerateDirectionSteps extends AsyncTask<Void, Void, String> {
    private Activity _activity;
    private Context _context;
    private String _finalStep;
    private Terminal _chosenTerminal;
    private List<String> _STR_StepsList;
    private String _STR_TotalTime;
    private Terminal _prospectTerminal;
    private Constants _constants = new Constants();
    private LoaderDialog _loader;
    public asyncGenerateDirectionSteps(Activity activity, Context context, Terminal terminal, List<String> stepslist, String totaltime, Terminal prospectTerminal,LoaderDialog loaderDialog){
        this._activity=activity;
        this._context=context;
        this._chosenTerminal = terminal;
        this._STR_StepsList = stepslist;
        this._STR_TotalTime = totaltime;
        this._prospectTerminal = prospectTerminal;
        this._loader =loaderDialog;

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(Void... voids) {
        String finalInstructions=null;
        try{
            finalInstructions = SelectedTabInstructions(_STR_StepsList,_STR_TotalTime,_prospectTerminal);
        }catch (Exception ex){
            Helper.logger(ex);
        }
        return finalInstructions;
    }

    @Override
    protected void onPostExecute(String finalInstructions) {
        super.onPostExecute(finalInstructions);
        MenuActivity._RouteStepsText.loadDataWithBaseURL(_constants.ROUTES_BASEURI, finalInstructions, _constants.ROUTES_MIMETYPE, _constants.ROUTES_ENCODING, null);
        MenuActivity._RouteStepsText.scrollTo(0,0);
        _loader.dismiss();
    }
    public String SelectedTabInstructions(List<String> STR_StepsList, String STR_TotalTime, Terminal TM_Terminal) {
        String Step =
                "<h3 style='padding-left:5%;color:#4834d4'>Suggested Actions</h3><body style='margin: 0; padding: 0'><table style='padding-left:5%; padding-right:2%;'><tr><td width='20%'><userImg style='height:60%; border-radius:50%;' src= 'drawable/ic_walking.png'></td>" +
//                        "<td style='padding-left:7%;'><medium style='background:#2196F3; color:white;border-radius:10%; padding: 7px;'>WALK</medium></td></tr>" +
                        "<td style='padding-left:7%;'><b>Walk your way to " + TM_Terminal.getDescription() + " Terminal </b></td></tr>" +
                        "<tr><td width='20%' style='text-align:center'><small>" + CleanTotalTime(STR_TotalTime) + "</small></td><td></td></tr>";

        if (STR_StepsList != null) {
            for (int x = 0; x < STR_StepsList.size(); x++) {
                Step += "<tr><td></td><td>" + (x + 1) + ". " + CleanDirectionStep(STR_StepsList.get(x)) + ".</td><tr>";
                if ((x + 1) == STR_StepsList.size()) {
                    Step += "<tr><td></td><td>" + (x + 2) + ". " + GenerateFinalStep(_chosenTerminal, TM_Terminal);
                }
            }
        }
        return Step + "</table></body>";
    }

    public String CleanDirectionStep(String STR_Step) {
        if (STR_Step != null) {
            if (STR_Step.contains("onto")) {
                STR_Step = STR_Step.replace("onto", "on to");
            }
            if (STR_Step.contains("<div style=\"font-size:0.9em\">")) {
                STR_Step = STR_Step.replace("<div style=\"font-size:0.9em\">", " ");
            }
            if (STR_Step.contains("</div>")) {
                STR_Step = STR_Step.replace("</div>", "");
            }
        }

        return STR_Step;
    }

    public String CleanTotalTime(String STR_TotalTime_Unlean) {
        if (STR_TotalTime_Unlean != null) {
            if (STR_TotalTime_Unlean.contains("hours")) {
                STR_TotalTime_Unlean = STR_TotalTime_Unlean.replace("hours", "h");
            }
            if (STR_TotalTime_Unlean.contains("mins")) {
                STR_TotalTime_Unlean = STR_TotalTime_Unlean.replace("mins", "min");
            }
        }

        return STR_TotalTime_Unlean;
    }

    public String GenerateFinalStep(Terminal TM_DropOff, Terminal TM_PickUp) {
        ArrayList<Terminal> DropOffList = Helper.GetAllDestinationRegardlessOfTheirTableRouteIds(TM_DropOff);
        for (Terminal entry: DropOffList) {
            if(entry.getTblRouteID()==TM_PickUp.getTblRouteID()){
                int dist = entry.OrderOfArrival - TM_PickUp.OrderOfArrival;
                return "Ride the e-loop and alight on <b>"
                        +dist
                        +GeneratePrefix(dist)
                        +" stop</b>.</td><tr>";
            }
        }
        return "";

    }
    private String GeneratePrefix(Integer i){
        String result = "";
        i = i <= 20 ? i : i%10;
            switch (i) {
                case 1:
                    result = "st";
                    break;
                case 2:
                    result = "nd";
                    break;
                case 3:
                    result = "rd";
                    break;
                default:
                    result = "th";
                    break;
            }
        return result;
    }

}
