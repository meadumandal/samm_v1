package com.umandalmead.samm_v1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * Created by eleazerarcilla on 04/02/2018.
 */

public class AboutActivity extends Fragment {
    private TextView SammTV;
    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.activity_about, container, false);

        SammTV = (TextView) myView.findViewById(R.id.SAMM_text);
        SammTV.setText(Html.fromHtml("<b>SAMM</b> <i></i>"));
        return myView;
    }
}