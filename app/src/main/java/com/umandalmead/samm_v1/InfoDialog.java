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


public class InfoDialog extends Dialog implements
        android.view.View.OnClickListener {
    public Activity _activity;
    public Dialog dialog;
    public Button btnOk;
    public TextView infoMessage;
    public String _infoMessage;
    public InfoDialog(Activity activity, String infoMessage) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
        this._infoMessage = infoMessage;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_info);
        this.infoMessage = (TextView) findViewById(R.id.txtInfoMessage);
        this.infoMessage.setText(this._infoMessage);
        this.infoMessage.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

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
