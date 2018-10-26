package com.umandalmead.samm_v1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;


/**
 * Created by eleazerarcilla on 04/02/2018.
 */

public class AboutActivity extends Fragment {
    private TextView SammTV;
    private View myView;
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    private LikeView fbLike;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myView = inflater.inflate(R.layout.activity_about, container, false);
        SammTV = (TextView) myView.findViewById(R.id.SAMM_text);
        fbLike =  (LikeView) myView.findViewById(R.id.fb_like_view);
        SessionManager sessionManager = new SessionManager(myView.getContext());
        InitializeToolbar("About");
        InitializeFacebookLikeButton();

        if (sessionManager.getIsDeveloper()) {
            SammTV.setText(Html.fromHtml("<i>Developed by: E & M <i>"));
        } else {
            SammTV.setText(Html.fromHtml("This application is designed for Filinvest City 360 Ecoloop. This is a beta version and some features are not yet available. <br><br> <i>Developed by: E & M <i>"));
        }


        return myView;
    }
    private void InitializeFacebookLikeButton(){
        FacebookSdk.sdkInitialize(getContext());
        fbLike.setObjectIdAndType("https://www.facebook.com/SAMM.Ecoloop.Guide/", LikeView.ObjectType.PAGE);
    }
    public void InitializeToolbar(String fragmentName){
        FAB_SammIcon = (ImageButton) myView.findViewById(R.id.SAMMLogoFAB);
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        ViewTitle = (TextView) myView.findViewById(R.id.samm_toolbar_title);
        ViewTitle.setText(fragmentName);
    }
}
