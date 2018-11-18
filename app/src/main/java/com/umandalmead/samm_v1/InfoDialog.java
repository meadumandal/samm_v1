package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by MeadRoseAnn on 1/27/2018.
 */


public class InfoDialog extends Dialog implements
        android.view.View.OnClickListener {
    public Activity _activity;
    public Dialog dialog;
    public Button btnOk;
    public TextView infoMessage;
    public String _infoMessage;
    private Helper _helper;
    public InfoDialog(Activity activity, String infoMessage) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
        this._infoMessage = infoMessage;
        this._helper = new Helper(_activity,_activity.getApplicationContext());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_info);
        this.infoMessage = (TextView) findViewById(R.id.txtInfoMessage);
        this.infoMessage.setText(this._infoMessage);
        this.infoMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        this.infoMessage.setTypeface(Helper.FONT_RUBIK_REGULAR);
        btnOk    = (Button) findViewById(R.id.btnOK);
        btnOk.setOnClickListener(this);
        btnOk.setTypeface(Helper.FONT_RUBIK_BLACK);
        MenuActivity.buttonEffect(btnOk);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnOK:
            dismiss();
                break;
            default:
                break;
        }

    }
}
