package com.umandalmead.samm_v1;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

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
    private ImageButton FAB_SammIcon, IB_profile_loader_circle;
    private TextView ViewTitle,tv_isFacebook,tv_NameDisplay, tv_userTypeDisplay;
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
        tv_confirmPassword = (EditText)_view.findViewById(R.id.edit_confirmpassword);
        tv_currentPassword = (EditText) _view.findViewById(R.id.edit_currentPassword);
        tv_isFacebook = (TextView) _view.findViewById(R.id.tv_isFacebook);
        userImage = (CircleImageView) _view.findViewById(R.id.profileImg);
        IB_profile_loader_circle = (ImageButton)  _view.findViewById(R.id.IB_profile_loader_circle);
        btn_save = (Button) _view.findViewById(R.id.btn_save);
        tv_NameDisplay = (TextView) _view.findViewById(R.id.txtUserFullname);
        LL_UserCredentialsHolder = (LinearLayout) _view.findViewById(R.id.LL_UserCredentialsHolder);
        _sessionManager = new SessionManager(getContext());
        SL_FB_InfoMessageShimmer = (ShimmerLayout) _view.findViewById(R.id.SL_FBUser_Profile_InfoMessage);

        _LoaderDialog = new LoaderDialog(this.getActivity(), "Updating", "Updating your profile, please wait...");
        _LoaderDialog.setCancelable(false);

        tv_firstName.setText(_sessionManager.getFirstName());
        tv_lastName.setText(_sessionManager.getLastName());
        Animation rotation;
        rotation = AnimationUtils.loadAnimation(this.getActivity(), R.anim.rotate);
        rotation.setFillAfter(true);
        this.IB_profile_loader_circle.startAnimation(rotation);

        if (_sessionManager.isFacebook())
        {
            LL_UserCredentialsHolder.setVisibility(View.GONE);
            tv_isFacebook.setVisibility(View.VISIBLE);
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
            userImage.setVisibility(View.GONE);
            IB_profile_loader_circle.setVisibility(View.GONE);
            SL_FB_InfoMessageShimmer.setVisibility(View.GONE);
        }
        if(_sessionManager.isLoggedIn()){
            tv_NameDisplay.setText(_sessionManager.getFullName().toUpperCase());
        }




        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _LoaderDialog.show();
                String currentPassword = tv_currentPassword.getText().toString();
                final String password = tv_password.getText().toString();
                final String confirmPassword = tv_confirmPassword.getText().toString();
                final String newFirstName = tv_firstName.getText().toString().trim();
                final String newLastName = tv_lastName.getText().toString().trim();
                if (password.trim().length()>0)
                {
                    if(password.equals(confirmPassword))
                    {
                        if(password.length() < 6)
                        {
                            Toast.makeText(getContext(), String.format(getString(R.string.error_shortpassword), "6"), Toast.LENGTH_LONG).show();
                            _LoaderDialog.hide();
                            return;
                        }
                        else {

                            final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                            // Get auth credentials from the user for re-authentication. The example below shows
                            // email and password credentials but there are multiple possible providers,
                            // such as GoogleAuthProvider or FacebookAuthProvider.
                            AuthCredential credential = EmailAuthProvider
                                    .getCredential(_sessionManager.getEmail(), currentPassword);

                            // Prompt the user to re-provide their sign-in credentials
                            user.reauthenticate(credential)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                user.updatePassword(password).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            _promptMessage += " Password updated\n";

                                                        } else {
                                                            _promptMessage+= "Error updating password\n";
                                                        }
                                                        updateUserDetails(_sessionManager.getUsername(), newFirstName, newLastName);
                                                    }
                                                });
                                            } else {
                                                _promptMessage+= task.getException().getMessage().toString() + "\n";
                                                updateUserDetails(_sessionManager.getUsername(), newFirstName, newLastName);
                                            }
                                        }
                                    });

                        }

                    }
                    else
                    {
                        Toast.makeText(getContext(),"Password mismatch", Toast.LENGTH_LONG).show();
                        _LoaderDialog.hide();
                    }
                }
                else
                {
                    updateUserDetails(_sessionManager.getUsername(), newFirstName, newLastName);
                }
            }
        });

        return _view;
    }

    private void updateUserDetails(String username, String newFirstName, String newLastName)
    {
        new mySQLUpdateUserDetails(getContext(),getActivity(),_LoaderDialog,_promptMessage).execute(username, newFirstName, newLastName);
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

                }

            }catch (IOException exc){

            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            if(result!=null) {
                IB_profile_loader_circle.setVisibility(View.GONE);
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
        FAB_SammIcon = (ImageButton) _view.findViewById(R.id.SAMMLogoFAB);
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DrawerLayout drawerLayout = (DrawerLayout) ((MenuActivity) getActivity()).findViewById(R.id.drawer_layout);
                drawerLayout.openDrawer(Gravity.LEFT);
            }
        });
        ViewTitle = (TextView) _view.findViewById(R.id.samm_toolbar_title);
        ViewTitle.setText(fragmentName);
    }
}
