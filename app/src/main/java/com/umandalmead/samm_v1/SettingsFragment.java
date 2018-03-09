package com.umandalmead.samm_v1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


/**
 * Created by eleazerarcilla on 04/02/2018.
 */

public class SettingsFragment extends Fragment {

    View myView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.fragment_settings, container, false);
        EditText editSubName = (EditText) myView.findViewById(R.id.editAppSubName);
        Button btnUpdateSubName = (Button) myView.findViewById(R.id.btnUpdateSubName);


        return myView;
    }
}
