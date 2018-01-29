package com.example.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

/**
 * Created by MeadRoseAnn on 1/27/2018.
 */


public class NoInternetDialog extends Dialog implements
        android.view.View.OnClickListener {
    public Activity _activity;
    public Dialog dialog;
    public Button btnTryAgain;
    public NoInternetDialog(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_no_internet);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);
        btnTryAgain.setOnClickListener(this);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTryAgain:
                if(MenuActivity.isOnline())
                    this._activity.startActivity(new Intent(this._activity, LoginActivity.class));
                break;
            default:
                break;
        }

    }
}
