package com.umandalmead.samm_v1;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class UserProfileActivity extends Fragment {

    View _view;
    EditText tv_firstName, tv_lastName, tv_password, tv_confirmPassword, tv_currentPassword;
    public static ImageView userImage;
    Button btn_save;
    SessionManager _sessionManager;
    String _promptMessage="";
    ProgressDialog _progressDialog;
    public static String _facebookImg;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _view = inflater.inflate(R.layout.activity_user_profile, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        tv_firstName = (EditText)_view.findViewById(R.id.edit_firstName);
        tv_lastName = (EditText)_view.findViewById(R.id.edit_lastName);
        tv_password = (EditText)_view.findViewById(R.id.edit_password);
        tv_confirmPassword = (EditText)_view.findViewById(R.id.edit_confirmpassword);
        tv_currentPassword = (EditText) _view.findViewById(R.id.edit_currentPassword);
        userImage = (ImageView) _view.findViewById(R.id.profileImg);
        btn_save = (Button) _view.findViewById(R.id.btn_save);
        _sessionManager = new SessionManager(getContext());
        _progressDialog = new ProgressDialog(getContext());
        _progressDialog.setTitle("Updating...");
        _progressDialog.setMessage("Updating your profile, please wait...");
        _progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        _progressDialog.setCancelable(false);

        tv_firstName.setText(_sessionManager.getFirstName());
        tv_lastName.setText(_sessionManager.getLastName());

        _facebookImg = "http://graph.facebook.com/" + _sessionManager.getUsername().trim() + "/picture?type=large";
        try {
            FetchFBDPTask dptask = new FetchFBDPTask();
            dptask.execute();
        } catch (Exception ex) {
            Toast.makeText(getContext(), "Non-Facebook username!", Toast.LENGTH_LONG).show();
        }


        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _progressDialog.show();
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
                            _progressDialog.hide();
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
                        _progressDialog.hide();
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
        new mySQLUpdateUserDetails(getContext(),getActivity(),_progressDialog,_promptMessage).execute(username, newFirstName, newLastName);
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
                userImage.setImageBitmap(result);
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


}
