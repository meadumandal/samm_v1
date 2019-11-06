package com.evolve.evx.Modules.ManageStations;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.evolve.evx.MenuActivity;
import com.evolve.evx.R;
import com.evolve.evx.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddStationFragment extends Fragment {
    private View myView;
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.activity_addroute, container, false);
        FAB_SammIcon = (ImageButton) myView.findViewById(R.id.SAMMLogoFAB);
        ViewTitle = (TextView) myView.findViewById(R.id.samm_toolbar_title);
        ViewTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
        ViewTitle.setText("Add Point");
        SessionManager sessionManager = new SessionManager(myView.getContext());
        return myView;
    }

}
