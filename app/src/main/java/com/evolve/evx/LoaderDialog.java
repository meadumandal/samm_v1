package com.evolve.evx;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by eleazerarcilla on 07/10/2018.
 */

public class LoaderDialog extends Dialog {
    public Activity _activity;
    public Dialog dialog;
    public String _loaderMessage, _loaderTitle;
    public ImageView _loaderImage;
    public TextView TV_LoaderTitle, TV_LoaderMessage;
    public LoaderDialog(Activity activity, String loaderTitle, String loaderMessage) {
        super(activity);
        // TODO Auto-generated constructor stub
        this._activity = activity;
        this._loaderMessage = loaderMessage;
        this._loaderTitle = loaderTitle;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_loader);
        this._loaderImage = findViewById(R.id.IB_samm_loader_circle);
        this.dialog = new Dialog(_activity.getApplicationContext());
        Animation rotation;
        rotation = AnimationUtils.loadAnimation(_activity, R.anim.rotate);
        rotation.setFillAfter(true);
        this._loaderImage.startAnimation(rotation);

        //disable user tap
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        setCancelable(false);
        //make view transparent
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


    }



}
