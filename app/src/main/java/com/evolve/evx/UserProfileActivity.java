package com.evolve.evx;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.method.PasswordTransformationMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import de.hdodenhof.circleimageview.CircleImageView;
import io.supercharge.shimmerlayout.ShimmerLayout;

import static com.evolve.evx.MenuActivity._activity;

public class UserProfileActivity extends Fragment {

    View _view;
    EditText tv_firstName, tv_lastName, tv_password, tv_confirmPassword, tv_currentPassword;

    public static CircleImageView userImage;
    Button btn_save;
    SessionManager _sessionManager;
    String _promptMessage="";
    LoaderDialog _LoaderDialog;
    public static String _facebookImg;
    private TextView SammTV;
    private View myView;
    private ImageButton FAB_SammIcon;
    private ImageView IB_profile_loader_circle;
    public static TextView ViewTitle,tv_isFacebook,tv_NameDisplay, tv_userTypeDisplay, tv_userType;
    private ShimmerLayout SL_FB_InfoMessageShimmer;
    private LinearLayout LL_UserCredentialsHolder;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _view = inflater.inflate(R.layout.activity_user_profile, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        InitializeToolbar(MenuActivity._GlobalResource.getString(R.string.title_user_activity));
        tv_firstName = (EditText)_view.findViewById(R.id.edit_firstName);
        tv_lastName = (EditText)_view.findViewById(R.id.edit_lastName);
        tv_password = (EditText)_view.findViewById(R.id.edit_password);
        tv_userType = _view.findViewById(R.id.txtUsertype);
        tv_confirmPassword = (EditText)_view.findViewById(R.id.edit_confirmpassword);
        tv_currentPassword = (EditText) _view.findViewById(R.id.edit_currentPassword);
        tv_isFacebook = (TextView) _view.findViewById(R.id.tv_isFacebook);
        userImage = (CircleImageView) _view.findViewById(R.id.profileImg);
        IB_profile_loader_circle = (ImageView)  _view.findViewById(R.id.IB_profile_loader_circle);
        btn_save = (Button) _view.findViewById(R.id.btn_save);
        tv_NameDisplay = (TextView) _view.findViewById(R.id.txtUserFullname);
        LL_UserCredentialsHolder = (LinearLayout) _view.findViewById(R.id.LL_UserCredentialsHolder);
        _sessionManager = new SessionManager(getContext());
        SL_FB_InfoMessageShimmer = (ShimmerLayout) _view.findViewById(R.id.SL_FBUser_Profile_InfoMessage);

        tv_firstName.setTypeface(Helper.FONT_RUBIK_REGULAR);
        tv_lastName.setTypeface(Helper.FONT_RUBIK_REGULAR);
        tv_password.setTypeface(Helper.FONT_RUBIK_REGULAR);
        tv_confirmPassword.setTypeface(Helper.FONT_RUBIK_REGULAR);
        tv_currentPassword.setTypeface(Helper.FONT_RUBIK_REGULAR);


        _LoaderDialog = new LoaderDialog(this.getActivity(), "Updating", "Updating your profile, please wait...");
        _LoaderDialog.setCancelable(false);

        tv_firstName.setText(_sessionManager.getFirstName());
        tv_lastName.setText(_sessionManager.getLastName());
        Animation rotation;
        rotation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.rotate);
        rotation.setFillAfter(true);
        this.IB_profile_loader_circle.startAnimation(rotation);

        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        if (_sessionManager.isFacebook())
        {
            LL_UserCredentialsHolder.setVisibility(View.GONE);
            tv_isFacebook.setVisibility(View.VISIBLE);
            tv_isFacebook.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
            tv_isFacebook.setPadding(0,200,0,0);
            SL_FB_InfoMessageShimmer.startShimmerAnimation();
            _facebookImg = "http://graph.facebook.com/" + _sessionManager.getUsername().trim() + "/picture?type=large";
            try {
                FetchFBDPTask dptask = new FetchFBDPTask();
                dptask.execute();
            } catch (Exception ex) {
                Toast.makeText(getContext(), "Non-Facebook username!", Toast.LENGTH_LONG).show();
            }
        }
        else{
            LL_UserCredentialsHolder.setVisibility(View.VISIBLE);
            IB_profile_loader_circle.setVisibility(View.GONE);
            SL_FB_InfoMessageShimmer.setVisibility(View.GONE);
             userImage.setVisibility(View.GONE);
            CheckBox checkbox_showpassword = (CheckBox) _view.findViewById(R.id.checkBox_showPassword);

            checkbox_showpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                    if (isChecked) {
                        tv_confirmPassword.setTransformationMethod(null);
                        tv_currentPassword.setTransformationMethod(null);
                        tv_password.setTransformationMethod(null);
                    }
                    else {
                        tv_confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                        tv_currentPassword.setTransformationMethod(new PasswordTransformationMethod());
                        tv_password.setTransformationMethod(new PasswordTransformationMethod());
                    }
                }
            });
        }
        if(_sessionManager.isLoggedIn()){
            tv_NameDisplay.setText(_sessionManager.getFullName().toUpperCase());
            tv_NameDisplay.setTypeface(MenuActivity.FONT_RUBIK_REGULAR);
            tv_userType.setText(_sessionManager.getUserType().toString());
        }




        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentPassword = tv_currentPassword.getText().toString();
                final String password = tv_password.getText().toString();
                final String confirmPassword = tv_confirmPassword.getText().toString();
                final String newFirstName = tv_firstName.getText().toString().trim();
                final String newLastName = tv_lastName.getText().toString().trim();
                if(newFirstName.equals("") || newLastName.equals("") || Helper.HasSpecialCharacters(newFirstName) || Helper.HasSpecialCharacters(newLastName)) {
                    ErrorDialog ED_FullNameError = new ErrorDialog(getActivity(), MenuActivity._GlobalResource.getString(R.string.error_name_invalid));
                    ED_FullNameError.show();
                    return;
                }

                if ((!newFirstName.equalsIgnoreCase(_sessionManager.getFirstName())
                        || !newLastName.equalsIgnoreCase(_sessionManager.getLastName())) &&
                        password.trim().length()>0)
                {
                    //User details and password are modified
                    ErrorDialog errorDialog = new ErrorDialog(_activity);
                    if (!password.equals(confirmPassword))
                    {
                        errorDialog.setErrorMessage(MenuActivity._GlobalResource.getString(R.string.error_passwordnotmatch));
                        errorDialog.show();
                    }
                    else if (currentPassword.trim().isEmpty())
                    {
                        errorDialog.setErrorMessage(MenuActivity._GlobalResource.getString(R.string.error_shortpassword));
                        errorDialog.show();
                    }
                    else
                    {
                        updateUserProfile(_sessionManager.getUsername(),
                                newFirstName,
                                newLastName,
                                currentPassword,
                                password,
                                confirmPassword);
                    }
                }
                else if ((!newFirstName.equalsIgnoreCase(_sessionManager.getFirstName())
                        || !newLastName.equalsIgnoreCase(_sessionManager.getLastName()))
                        && password.trim().length()==0)
                {
                    //Only user details are modified
                    updateUserProfile(_sessionManager.getUsername(),
                            newFirstName,
                            newLastName);
                }
                else  if ((newFirstName.equalsIgnoreCase(_sessionManager.getFirstName())
                        && newLastName.equalsIgnoreCase(_sessionManager.getLastName()))
                        && password.trim().length()>0)
                {
                    //Only password is changed
                    ErrorDialog errorDialog = new ErrorDialog(_activity);
                    if (!password.equals(confirmPassword))
                    {
                        errorDialog.setErrorMessage(MenuActivity._GlobalResource.getString(R.string.error_passwordnotmatch));
                        errorDialog.show();
                    }
                    else if (currentPassword.trim().isEmpty())
                    {
                        errorDialog.setErrorMessage(MenuActivity._GlobalResource.getString(R.string.error_confirmpassword));
                        errorDialog.show();
                    }
                    else
                        updateUserProfile(_sessionManager.getUsername(),
                                newFirstName,
                                newLastName,
                                currentPassword,
                                password,
                                confirmPassword);

                }
                else
                {
                    InfoDialog infoDialog = new InfoDialog(_activity, MenuActivity._GlobalResource.getString(R.string.error_user_profile_nothing_has_changed));
                    infoDialog.show();
                }
            }
        });

        return _view;
    }

    private void updateUserProfile(String username, String newFirstName, String newLastName)
    {
         new mySQLUpdateUserDetails(getContext(),getActivity(),
                _LoaderDialog,
                false).
            execute(
                username,
                newFirstName,
                newLastName);
        tv_password.setText("");
        tv_currentPassword.setText("");
        tv_confirmPassword.setText("");
        _promptMessage = "";

    }
    private void updateUserProfile(String username, String newFirstName, String newLastName, String currentPassword, String newPassword, String confirmPassword)
    {
        new mySQLUpdateUserDetails(getContext(),
                getActivity(),
                _LoaderDialog,
                true).
        execute(username,
                newFirstName,
                newLastName,
                currentPassword,
                newPassword,
                confirmPassword);

        tv_password.setText("");
        tv_currentPassword.setText("");
        tv_confirmPassword.setText("");
        _promptMessage = "";

    }

    public class FetchFBDPTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... strings) {
            URL imageURL = null;
            try {
                imageURL = new URL(_facebookImg);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Helper.logger(e);
            }
            Bitmap bitmap = null;
            BitmapFactory.Options options = new BitmapFactory.Options();
            try {
                bitmap = BitmapFactory.decodeStream(fetch(imageURL.toString()), null, options);
            } catch (OutOfMemoryError e){
                try{
                    options.inSampleSize = 2;
                    bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    return bitmap;
                }catch (Exception ex){
                    Helper.logger(ex);
                }

            }catch (IOException exc){
                Helper.logger(exc);

            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                IB_profile_loader_circle.setVisibility(View.GONE);
                IB_profile_loader_circle.setImageResource(0);
                userImage.setImageBitmap(result);
                userImage.setVisibility(View.VISIBLE);
            }
        }
        private InputStream fetch(String address) throws MalformedURLException,IOException {
            HttpGet httpRequest = new HttpGet(URI.create(address) );
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = (HttpResponse) httpclient.execute(httpRequest);
            HttpEntity entity = response.getEntity();
            BufferedHttpEntity bufHttpEntity = new BufferedHttpEntity(entity);
            InputStream instream = bufHttpEntity.getContent();
            return instream;
        }
    }

    public void InitializeToolbar(String fragmentName){
        try {
            FAB_SammIcon = (ImageButton) _view.findViewById(R.id.SAMMLogoFAB);
            FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                    drawerLayout.openDrawer(Gravity.LEFT);
                }
            });
            ViewTitle = (TextView) _view.findViewById(R.id.samm_toolbar_title);
            ViewTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
            ViewTitle.setText(fragmentName);
        }catch (Exception ex){
            Helper.logger(ex);
        }
    }

}
