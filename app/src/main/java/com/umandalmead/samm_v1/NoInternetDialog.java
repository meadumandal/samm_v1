package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import io.supercharge.shimmerlayout.ShimmerLayout;

/**
 * Created by MeadRoseAnn on 1/27/2018.
 */


public class NoInternetDialog extends Dialog implements
        android.view.View.OnClickListener {
    public Activity _activity;
    public Dialog dialog;
    public Button btnTryAgain;
    private ShimmerLayout SL_TryAgain;
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
        SL_TryAgain = (ShimmerLayout) findViewById(R.id.SL_TryAgain);
        SL_TryAgain.startShimmerAnimation();
        btnTryAgain.setOnClickListener(this);
        MenuActivity.buttonEffect(btnTryAgain);


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTryAgain:
                if(MenuActivity.isOnline())
                    SL_TryAgain.startShimmerAnimation();
                    this._activity.startActivity(new Intent(this._activity, LoginActivity.class));
                break;
            default:
                break;
        }

    }
}
