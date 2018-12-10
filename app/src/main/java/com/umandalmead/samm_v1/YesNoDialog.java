package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by MeadRoseAnn on 12/4/2018.
 */

public class YesNoDialog extends Dialog
{
    public Activity _activity;
    public String _title, _message;
    public Button _btnYes, _btnNo;
    public TextView _txtTitle, _txtConfirmationMessage;
    public YesNoDialog(Activity activity, String title, String message)
    {
        super(activity);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_yesno);
        this._activity = activity;
        this._title = title;
        this._message = message;
        this._txtTitle = findViewById(R.id.txtTitleMessage);
        this._txtConfirmationMessage = findViewById(R.id.txtConfirmationMessage);
        this._btnYes = findViewById(R.id.btnYes);
        this._btnNo = findViewById(R.id.btnNo);
    }
//    @Override
//    public void onClick(View view) {
//
//    }
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        _txtTitle.setText(_title);
        _txtConfirmationMessage.setText(_message);

        this._txtTitle.setTypeface(Helper.FONT_RUBIK_BOLD);
        this._txtConfirmationMessage.setTypeface(Helper.FONT_RUBIK_REGULAR);
        this._btnYes.setTypeface(Helper.FONT_RUBIK_REGULAR);
        this._btnNo.setTypeface(Helper.FONT_RUBIK_REGULAR);

        MenuActivity.buttonEffect(_btnYes);
        MenuActivity.buttonEffect(_btnNo);
        getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
