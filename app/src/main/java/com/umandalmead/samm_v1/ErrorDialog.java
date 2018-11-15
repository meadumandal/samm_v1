package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by MeadRoseAnn on 1/27/2018.
 */


public class ErrorDialog extends Dialog implements
        android.view.View.OnClickListener {
    public Activity _activity;
    public Dialog dialog;
    public Button btnOk;
    public TextView errorMessage;
    public String _errorMessage;
    public Helper _helper;
    public ErrorDialog(Activity activity, String errorMessage) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
        this._errorMessage = errorMessage;
        this._helper = new Helper(_activity, _activity.getApplicationContext());

    }
    public ErrorDialog(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
        this._errorMessage = "";

    }
    public void setErrorMessage(String errorMessage)
    {
        this._errorMessage = errorMessage;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_error);
        this.errorMessage = (TextView) findViewById(R.id.txtErrorMessage);
        this.errorMessage.setText(this._errorMessage);
        this.errorMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        this.errorMessage.setTypeface(Helper.FONT_RUBIK_REGULAR);
        btnOk    = (Button) findViewById(R.id.btnOK);
        btnOk.setTypeface(Helper.FONT_RUBIK_BOLD);
        btnOk.setOnClickListener(this);
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
