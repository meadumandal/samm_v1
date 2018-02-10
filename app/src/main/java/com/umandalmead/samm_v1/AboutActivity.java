package com.umandalmead.samm_v1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by eleazerarcilla on 04/02/2018.
 */

public class AboutActivity extends AppCompatActivity {
    private TextView SammTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        SammTV = (TextView) findViewById(R.id.SAMM_text);
        SammTV.setText(Html.fromHtml("<b>SAMM</b> <i></i>"));
    }
}
