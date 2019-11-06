package com.evolve.evx;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.share.widget.LikeView;

import io.supercharge.shimmerlayout.ShimmerLayout;


/**
 * Created by eleazerarcilla on 04/02/2018.
 */

public class AboutActivity extends Fragment {
    public static TextView SammTV, TV_SammVersion, TV_SammLatestVersion;
    private View myView;
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    private LikeView fbLike;
    public static ShimmerLayout _SL_TV_LatestVersion;
    private SessionManager _sessionManager;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getContext());
        _sessionManager = new SessionManager(getContext());
        myView = inflater.inflate(R.layout.activity_about, container, false);
        SammTV = (TextView) myView.findViewById(R.id.SAMM_text);
        TV_SammVersion = (TextView) myView.findViewById(R.id.SAMM_version);
        TV_SammLatestVersion = (TextView) myView.findViewById(R.id.SAMM_LatestVersion);
        _SL_TV_LatestVersion = (ShimmerLayout) myView.findViewById(R.id.SL_LatestVersion);
        fbLike =  (LikeView) myView.findViewById(R.id.fb_like_view);
        SessionManager sessionManager = new SessionManager(myView.getContext());
        InitializeToolbar(MenuActivity._GlobalResource.getString(R.string.title_about_activity));
        if(_sessionManager.isFacebook())
            InitializeFacebookLikeButton();
        SammTV.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
        TV_SammVersion.setTypeface(MenuActivity.FONT_RUBIK_BOLD);
        TV_SammLatestVersion.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
        new asyncGetApplicationSettings(this.getActivity(), getContext(), false).execute();
        TV_SammVersion.setText("EVX "+BuildConfig.VERSION_NAME);
        return myView;
    }
    private void InitializeFacebookLikeButton(){
        fbLike.setVisibility(View.VISIBLE);
        fbLike.setObjectIdAndType(MenuActivity._GlobalResource.getString(R.string.Facebook_samm_page_url), LikeView.ObjectType.PAGE);
        fbLike.setLikeViewStyle(LikeView.Style.BOX_COUNT);
        fbLike.setFragment(this);
        fbLike.setOnErrorListener(new LikeView.OnErrorListener() {
            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getContext(),e.toString(),Toast.LENGTH_LONG).show();
            }
        });
    }
    public void InitializeToolbar(String fragmentName){
        try {
            FAB_SammIcon = (ImageButton) myView.findViewById(R.id.SAMMLogoFAB);
            FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
            ViewTitle = (TextView) myView.findViewById(R.id.samm_toolbar_title);
            ViewTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
            ViewTitle.setText(fragmentName);
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }
}
