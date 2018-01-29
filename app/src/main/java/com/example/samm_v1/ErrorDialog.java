package com.example.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
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
    public ErrorDialog(Activity activity, String errorMessage) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
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

        btnOk    = (Button) findViewById(R.id.btnOK);
        btnOk.setOnClickListener(this);



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
