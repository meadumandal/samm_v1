package com.umandalmead.samm_v1;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatCallback;
import android.util.AttributeSet;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by eleazerarcilla on 30/06/2018.
 */

public class InfoLayout extends Dialog {
    private String _title, _description;
    private Context _context;
    private TextView _infoTitleTV, _infoDescriptionTV;
    private LinearLayout _infoLayout;
    private Activity _activity;
    public InfoLayout(Context context, String InfoTitle, String InfoDescription, @Nullable String Image) {
        super(context);
        this._context = context;
        this._title = InfoTitle;
        this._description = InfoDescription;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.layout_info_holder);
        try {
            _infoTitleTV = (TextView) findViewById(R.id.TextView_InfoTitle);
            _infoDescriptionTV = (TextView) findViewById(R.id.TextView_InfoDesc);
            _infoLayout = (LinearLayout) findViewById(R.id.Info_Layout);
        } catch (Exception ex) {
            String t = "";
        }

    }


//    public void Show() {
//       // Initialize();
//        _infoLayout.setVisibility(View.VISIBLE);
//    }

    public void Hide() {
        _infoLayout.setVisibility(View.GONE);
    }
}
