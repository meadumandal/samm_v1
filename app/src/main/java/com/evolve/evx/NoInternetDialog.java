package com.evolve.evx;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

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
    private Helper _helper;
    private TextView _TV_1, _TV_2;
    public NoInternetDialog(Activity activity) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
        this._helper = new Helper(activity, activity.getApplicationContext());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_no_internet);
        btnTryAgain = (Button) findViewById(R.id.btnTryAgain);
        _TV_1 = (TextView) findViewById(R.id.textView);
        _TV_2 = (TextView) findViewById(R.id.textView2);
        SL_TryAgain = (ShimmerLayout) findViewById(R.id.SL_TryAgain);
        SL_TryAgain.startShimmerAnimation();
        btnTryAgain.setTypeface(_helper.FONT_RUBIK_BOLD);
        _TV_1.setTypeface(_helper.FONT_RUBIK_REGULAR);
        _TV_2.setTypeface(_helper.FONT_RUBIK_REGULAR);
        btnTryAgain.setOnClickListener(this);
        MenuActivity.buttonEffect(btnTryAgain);
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;


    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnTryAgain:
             //   if(Helper.isOnline(_activity,getContext()))
                    SL_TryAgain.stopShimmerAnimation();
             new asyncCheckInternetConnectivity(this._activity).execute();
               //     this._activity.startActivity(new Intent(this._activity, LoginActivity.class));
                break;
            default:
                break;
        }

    }
}
