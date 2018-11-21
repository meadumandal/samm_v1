package com.umandalmead.samm_v1;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.Menu;

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
    private Helper _helper = new Helper();
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

        _loader.dismiss();
    }
    public String SelectedTabInstructions(List<String> STR_StepsList, String STR_TotalTime, Terminal TM_Terminal) {

        String Step = "<html><head><style type=\"text/css\">@font-face {font-family: MyFont;src: url(\"file:///android_asset/font/Rubik/Rubik-Regular.ttf\")}body {font-family: MyFont;font-size: medium;text-align: justify;}</style></head><h3 style='padding-left:5%;color:#4834d4;'>Suggested Actions</h3><body style='margin: 0; padding: 0'>";
        Step+="<div style=\"text-align: left; margin-left: 2%; margin-right: 4%; padding-bottom: 5%;\">" + "\t<div style=\"font-weight: bold;text-align: center;\">Walk your way to "+TM_Terminal.getDescription()+" terminal</div><div style='text-align: center;'>("+STR_TotalTime+")</div>\n";
        if (STR_StepsList != null) {
            for (int x = 0; x < STR_StepsList.size(); x++) {
                Step += "<div style='margin-top:4%; marging-bottom:4%;'><img style='margin-left:4%; margin-right:2%;' width=\"20px\" height=\"20px\" src='"+GetDirectionIcon(STR_StepsList.get(x))+"'>" + (x + 1) + ". " + CleanDirectionStep(STR_StepsList.get(x)) + ".<div style=\"\n" +
                        "    border-bottom: 1px solid #00000026;\n" +
                        "    margin-top: 0.5%;\n" +
                        "    margin-left: 8%;\n" +
                        "    align-items: center;\n" +
                        "    width: inherit;\n" +
                        "\"></div></div>";
                if ((x + 1) == STR_StepsList.size()) {
                    Step += "<div style='margin-top:4%; marging-bottom:4%;'><img style='margin-left:4%; margin-right:2%;' width=\"20px\" height=\"20px\" src='" + GetDirectionIcon("final") + "'>" + (x + 2) + ". " + GenerateFinalStep(_chosenTerminal, TM_Terminal);
                }
            }
        }
        return Step + "</div</body></html>";
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

    private String GetDirectionIcon(String STR_Step){
        String STR_Result = "file:///android_res/drawable/";
        if(STR_Step!=null){
           String STR_temp=STR_Step.toLowerCase();
            STR_temp= STR_temp.replace("<b>","");
            STR_temp= STR_temp.replace("</b>","");
            if(STR_temp.contains("slight left")){
                STR_Result+="slight_left.png";
            }
            else if(STR_temp.contains("slight right")){
                STR_Result+="slight_right.png";
            }
            else if(STR_temp.contains("head") || STR_temp.contains("continue")){
                STR_Result+="head.png";
            }
            else if(STR_temp.contains("turn right") || STR_Result.contains("sharp right")){
                STR_Result+="turn_right.png";
            }
            else if(STR_temp.contains("turn left") || STR_Result.contains("sharp left")){
                STR_Result+="turn_left.png";
            }
            else if(STR_temp.contains("final")){
                STR_Result+="checked.png";
            }
        }
        return  STR_Result;
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
        String strSignBoard = "";
        String signBoardSeparator = MenuActivity._GlobalResource.getString(R.string.sign_board_separator);
        for(Terminal dropOff: DropOffList)
        {
            strSignBoard += dropOff.getRouteName() + signBoardSeparator;
        }
        strSignBoard = strSignBoard.substring(0,strSignBoard.length() - signBoardSeparator.length());

        for (Terminal entry: DropOffList) {
            if(entry.getTblRouteID()==TM_PickUp.getTblRouteID()){
                int dist = 0;
                if (entry.OrderOfArrival < TM_PickUp.OrderOfArrival)
                {
                    dist =( _helper.getNoOfStationsByRouteID(entry.tblRouteID) - TM_PickUp.OrderOfArrival)
                            + (entry.OrderOfArrival);
                }
                else if (entry.OrderOfArrival > TM_PickUp.OrderOfArrival)
                {
                    dist = entry.OrderOfArrival - TM_PickUp.OrderOfArrival;
                }
                return "Ride the e-loop and alight on "
                        + "<b>"
//                        +dist
                        +TM_DropOff.Description
//                        +GeneratePrefix(dist)
//                        +" stop"
                        +"</b> station"
                        +".<div style=\"\n" +
                        "    border-bottom: 1px solid #00000026;\n" +
                        "    margin-top: 0.5%;\n" +
                        "    margin-left: 8%;\n" +
                        "    align-items: center;\n" +
                        "    width: inherit;\n" +
                        "\"></div></div>";
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
