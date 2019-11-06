package com.evolve.evx;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by eleazerarcilla on 16/11/2018.
 */

public class TutorialDialog extends Dialog implements
        android.view.View.OnClickListener {
    private Activity _activity;
    private Helper _helper;
    private Button _BTN_OK, _BTN_NEXT;
    private TextView _TV_Instructions, _TV_Tutorial_Title, _TV_skip;
    private ImageView _IV_tutorial_image;
    private ArrayList<String> _AL_STR_Tutorials = new ArrayList<String>();
    private ArrayList<Integer> _AL_INT_ResourceIds = new ArrayList<Integer>();
    private Integer tutorialCtr =0;
    private Dialog dialog;
    private ArrayList<String> _AL_STR_TutorialTitles = new ArrayList<String>();
    private Constants _constants = new Constants();
    public TutorialDialog(Activity activity,String[] STR_array_TutorialTitles, String[] STR_array_Tutorials, Integer[] INT_array_ImageResources){
        super(activity);
        this._activity = activity;
        this._AL_STR_Tutorials =  new ArrayList<String>(Arrays.asList(STR_array_Tutorials));
        this._AL_INT_ResourceIds =  new ArrayList<Integer>(Arrays.asList(INT_array_ImageResources));
        this._helper = new Helper(_activity, _activity.getApplicationContext());
        this.dialog = new Dialog(_activity.getApplicationContext());
        this._AL_STR_TutorialTitles = new ArrayList<String>(Arrays.asList(STR_array_TutorialTitles));;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_tutorial);
        try{
            InitializeUIObjects();
        }catch (Exception ex){
            Helper.logger(ex);
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.BTN_tutorial_next:
                tutorialCtr++;
                ViewNextStep();
                break;
            case R.id.BTN_tutorial_done:
                dismiss();
                break;
            case R.id.TV_tutorial_skip:
                dismiss();
                break;
            default:
                break;
        }
    }
    private void InitializeUIObjects(){
        try {
            _BTN_NEXT = (Button) findViewById(R.id.BTN_tutorial_next);
            _BTN_OK = (Button) findViewById(R.id.BTN_tutorial_done);
            _TV_Instructions = (TextView) findViewById(R.id.TV_Tutorial_Instruction);
            _IV_tutorial_image = (ImageView) findViewById(R.id.IV_tutorial_image);
            _TV_Tutorial_Title = (TextView) findViewById(R.id.TV_Tutorial_Title);
            _TV_skip = (TextView) findViewById(R.id.TV_tutorial_skip);
            getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
            dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            setCancelable(false);
            SetTypeFace();
            if (_AL_STR_TutorialTitles.size() != 0 &&_AL_INT_ResourceIds.size() != 0 && _AL_STR_Tutorials.size() != 0) {
                if(_AL_STR_TutorialTitles.get(0).toLowerCase().contains("rate me")){
                    _BTN_OK.setText("Sure!");
                    _BTN_OK.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final String appPackageName = _activity.getPackageName();
                            try {
                                _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(_constants.PLAY_STORE_MARKET_URI + appPackageName)));
                            } catch (android.content.ActivityNotFoundException anfe) {
                                _activity.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(_constants.PLAY_STORE_URI_WITH_QUERYSTRING + appPackageName)));
                            }
                            dismiss();
                        }
                    }); Toggle(_BTN_OK);
                }else {
                    if (_AL_STR_TutorialTitles.size() > 1 && _AL_INT_ResourceIds.size() > 1 && _AL_STR_Tutorials.size() > 1)
                        Toggle(_BTN_NEXT);
                    else {
                        Toggle(_BTN_OK);
                        _TV_skip.setVisibility(View.GONE);
                    }
                    _BTN_OK.setOnClickListener(this);
                    _BTN_NEXT.setOnClickListener(this);
                }
                _TV_skip.setOnClickListener(this);
                _IV_tutorial_image.setImageResource(_AL_INT_ResourceIds.get(tutorialCtr));
                _TV_Instructions.setText(_AL_STR_Tutorials.get(tutorialCtr));
                _TV_Tutorial_Title.setText(_AL_STR_TutorialTitles.get(tutorialCtr));
            }
            MenuActivity.buttonEffect(_BTN_OK);
            MenuActivity.buttonEffect(_BTN_NEXT);
        }catch (Exception ex){
            Helper.logger(ex);
        }

    }
    private void Toggle(View view){
        try {
            view.setVisibility(view.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
    private void ViewNextStep(){
        try {
            if(_AL_STR_TutorialTitles.size()-1 >= tutorialCtr && _AL_INT_ResourceIds.size()-1 >= tutorialCtr && _AL_STR_Tutorials.size()-1 >= tutorialCtr) {
                _IV_tutorial_image.setImageResource(_AL_INT_ResourceIds.get(tutorialCtr));
                _TV_Instructions.setText(_AL_STR_Tutorials.get(tutorialCtr));
                _TV_Tutorial_Title.setText(_AL_STR_TutorialTitles.get(tutorialCtr));
                if(_AL_STR_TutorialTitles.size()-1 == tutorialCtr && _AL_INT_ResourceIds.size()-1 == tutorialCtr && _AL_STR_Tutorials.size()-1 == tutorialCtr){
                    Toggle(_BTN_OK);
                    Toggle(_BTN_NEXT);
                    _TV_skip.setVisibility(View.GONE);
                }
            }
        }
        catch (Exception ex){
            Helper.logger(ex);
        }
    }
    private void SetTypeFace(){
        _BTN_NEXT.setTypeface(Helper.FONT_RUBIK_BOLD);
        _BTN_OK.setTypeface(Helper.FONT_RUBIK_BOLD);
        _TV_Instructions.setTypeface(Helper.FONT_RUBIK_REGULAR);
        _TV_Tutorial_Title.setTypeface(Helper.FONT_RUBIK_BOLD);
    }
}
