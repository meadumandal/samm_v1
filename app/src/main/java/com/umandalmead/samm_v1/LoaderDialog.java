package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by eleazerarcilla on 07/10/2018.
 */

public class LoaderDialog extends Dialog {
    public Activity _activity;
    public Dialog dialog;
    public String _loaderMessage, _loaderTitle;
    public ImageButton _loaderImage;
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
        this.TV_LoaderMessage = (TextView) findViewById(R.id.loader_message);
        this.TV_LoaderTitle = (TextView) findViewById(R.id.loader_title) ;
        this.TV_LoaderMessage.setText(this._loaderMessage);
        this.TV_LoaderTitle.setText(this._loaderTitle);
        this._loaderImage = (ImageButton) findViewById(R.id.IB_samm_loader_circle);

        Animation rotation;
        rotation = AnimationUtils.loadAnimation(_activity, R.anim.rotate);
        rotation.setFillAfter(true);
        this._loaderImage.startAnimation(rotation);

        //disable user tap

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //make view transparent
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);


    }


}
