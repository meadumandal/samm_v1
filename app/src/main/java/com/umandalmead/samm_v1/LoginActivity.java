package com.umandalmead.samm_v1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.umandalmead.samm_v1.EntityObjects.FirebaseEntities.User;
import com.umandalmead.samm_v1.POJO.UserPOJO;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class LoginActivity extends AppCompatActivity{

    private EditText usernameField, passwordField;
    private Button btn_SignIn;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseDatabase userDatabase;
    private DatabaseReference userDatabaseRef;
    SessionManager sessionManager;
    public Boolean IsOnline;
    public ProgressDialog InitialLoading;
    private CallbackManager callbackManager;
    private static final String EMAIL = "email";
    private TextView forgotPasswordTextView;
    private LoginButton facebookLoginButton;
    private static String TAG = "mead";
    ProgressDialog LoginProgDiag;
    private static Helper _helper = new Helper();
    private Constants _constants = new Constants();
    private MediaPlayer _buttonClick;



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);

    }

    @Override

    protected void onCreate(Bundle savedInstanceState) {

        setTheme(R.style.SplashTheme);
        super.onCreate(savedInstanceState);


        if(MenuActivity.isOnline()) {

            Log.i(TAG, "device is online");
            FacebookSdk.sdkInitialize(getApplicationContext());
            setContentView(R.layout.activity_login);
            try {
                PackageInfo info = getPackageManager().getPackageInfo(
                        "com.example.samm_v1",
                        PackageManager.GET_SIGNATURES);
                for (Signature signature : info.signatures) {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
                }
            } catch (PackageManager.NameNotFoundException e) {
                _helper.logger(e);

            } catch (NoSuchAlgorithmException e) {
                _helper.logger(e);
            }

            callbackManager = CallbackManager.Factory.create();

            forgotPasswordTextView = (TextView) findViewById(R.id.txtForgotPassword);
            facebookLoginButton = (LoginButton) findViewById(R.id.login_button_fb);
            facebookLoginButton.setReadPermissions("email", "public_profile");
            MenuActivity.buttonEffect(facebookLoginButton);
            MenuActivity.buttonEffect(forgotPasswordTextView);
            _buttonClick = MediaPlayer.create(this, R.raw.button_click);

            facebookLoginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayButtonClickSound();
                }
            });


            facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(final LoginResult loginResult) {
                    ShowLogInProgressDialog("Facebook");
                    GraphRequest request =  GraphRequest.newMeRequest(
                            loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                                @Override
                                public void onCompleted(JSONObject me, GraphResponse response) {

                                    if (response.getError() != null) {
                                        // handle error
                                    } else {

                                        String user_lastname = me.optString("last_name");
                                        String user_firstname = me.optString("first_name");
                                        String user_email =response.getJSONObject().optString("email");


                                        handleFacebookAccessToken(loginResult.getAccessToken(), user_lastname,user_firstname,user_email);

                                    }
                                }
                            });

                    Bundle parameters = new Bundle();
                    parameters.putString("fields", "last_name,first_name,email");
                    request.setParameters(parameters);
                    request.executeAsync();
                }

                @Override
                public void onCancel() {
                    Log.d(TAG, "facebook:onCancel");
                    // ...
                }

                @Override
                public void onError(FacebookException error) {
                    Log.d(TAG, "facebook:onError", error);
                    // ...
                }
            });
            if (userDatabase == null && userDatabaseRef == null) {
                userDatabase = FirebaseDatabase.getInstance();
            }
            if (sessionManager == null)
                sessionManager = new SessionManager(getApplicationContext());

            if (auth == null)
                auth = FirebaseAuth.getInstance();


            usernameField = (EditText) findViewById(R.id.username);
            passwordField = (EditText) findViewById(R.id.password);
            btn_SignIn = (Button) findViewById(R.id.email_sign_in_button);
            progressBar = (ProgressBar) findViewById(R.id.progressBar);
            MenuActivity.buttonEffect(btn_SignIn);
            forgotPasswordTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayButtonClickSound();
                    try
                    {
                        final LoaderDialog FP_Loader = new LoaderDialog(LoginActivity.this, "Please wait...", "Sending password reset link to your e-mail");
                        FP_Loader.show();
                        if (usernameField.getText().toString().trim().length() == 0)
                        {
                            FP_Loader.dismiss();
                            ErrorDialog dialog=new ErrorDialog(LoginActivity.this, "Please enter an email address.");
                            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                            dialog.show();
                        }
                        else {
                            if (Patterns.EMAIL_ADDRESS.matcher(usernameField.getText().toString()).matches())
                            {
                                FirebaseAuth.getInstance().sendPasswordResetEmail(usernameField.getText().toString())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FP_Loader.dismiss();
                                                    InfoDialog dialog=new InfoDialog(LoginActivity.this, "Password reset link has been sent to your e-mail");
                                                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                                    dialog.show();
                                                }
                                                else
                                                {
                                                    Helper.logger(task.getException());
                                                }
                                            }
                                        })
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {


                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                FP_Loader.dismiss();
                                                _helper.logger(e);
                                            }
                                        });

                            }
                            else {
                                FP_Loader.dismiss();
                                ErrorDialog dialog=new ErrorDialog(LoginActivity.this, "Please enter an e-mail address");
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                                dialog.show();
                            }


                        }
                    }
                    catch(Exception ex)
                    {
                        Helper.logger(ex);
                    }


                }
            });


            if (sessionManager.isLoggedIn()) {
                ShowLogInProgressDialog("SAMM");
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();


            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            btn_SignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PlayButtonClickSound();
                    ShowLogInProgressDialog("User");
                    final String username = usernameField.getText().toString();
                    final String password = passwordField.getText().toString();
                    if (username.trim().isEmpty() || password.trim().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Invalid username and password", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    } else {
                        String url = _constants.WEB_API_URL + _constants.USERS_API_FOLDER;
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();
                        RetrofitDatabase service = retrofit.create(RetrofitDatabase.class);
                        Call<UserPOJO> call = service.getUserDetails(username, username);
                        call.enqueue(new Callback<UserPOJO>() {
                            @Override
                            public void onResponse(final Response<UserPOJO> response, Retrofit retrofit) {
                                try {
                                    if (response.body() == null) {
                                        progressBar.setVisibility(View.GONE);
                                        Toast.makeText(LoginActivity.this, "Username does not exist", Toast.LENGTH_LONG).show();
                                    } else {
                                        if (!response.body().getEmailAddress().toLowerCase().equals(_constants.DRIVER_EMAILADDRESS)) {
                                            if(response.body().getUserType().equals(Constants.ADMIN_USERTYPE))
                                            {
                                                MessageDigest md = MessageDigest.getInstance("MD5");
                                                md.update(password.getBytes());
                                                byte[] digest = md.digest();

                                                StringBuffer sb = new StringBuffer();
                                                for(byte b: digest)
                                                {
                                                    sb.append(String.format("%02x", b & 0xff));
                                                }
                                                //String hashedPassword = new BigInteger(1, md.digest()).toString(16);
                                                String hashedPassword = sb.toString();
                                                if (hashedPassword.toLowerCase().equals(response.body().getPassword().toLowerCase()))
                                                {
                                                    signIn(response.body().getEmailAddress(), Constants.ADMIN_PASSWORD, response.body().getLastName(), response.body().getFirstName(), response.body().getUsername(), "");
                                                }
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(), "Admin Password is incorrect", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                            else
                                            {
                                                signIn(response.body().getEmailAddress(), password, response.body().getLastName(), response.body().getFirstName(), response.body().getUsername(), "");
                                            }

                                        }
                                        else{
                                                FirebaseDatabase _firebaseDatabase = FirebaseDatabase.getInstance();
                                                DatabaseReference _driverDatabaseReference = _firebaseDatabase.getReference("drivers");
                                                _driverDatabaseReference.child(response.body().getDeviceId().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        if (dataSnapshot != null) {
                                                            if (dataSnapshot.child("connections") == null)
                                                                signIn(response.body().getEmailAddress(), password, response.body().getLastName(), response.body().getFirstName(), response.body().getUsername(), response.body().getDeviceId().toString());
                                                            else if (dataSnapshot.child("connections").getValue() == null)
                                                                signIn(response.body().getEmailAddress(), password, response.body().getLastName(), response.body().getFirstName(), response.body().getUsername(), response.body().getDeviceId().toString());
                                                            else if (!Boolean.valueOf(dataSnapshot.child("connections").getValue().toString()) == true)
                                                                signIn(response.body().getEmailAddress(), password, response.body().getLastName(), response.body().getFirstName(), response.body().getUsername(), response.body().getDeviceId().toString());
                                                            else {
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(LoginActivity.this, "Concurrent Login is not allowed. This driver account is already used on other device.", Toast.LENGTH_LONG).show();
                                                            }
//                                                        {
//                                                            signIn(response.body().getEmailAddress(), password, response.body().getLastName(), response.body().getFirstName(), response.body().getUsername());
//                                                        }
                                                        }
                                                    }

                                                    @Override
                                                    public void onCancelled(DatabaseError databaseError) {

                                                    }
                                                });
                                            }

                                        }
                                }
                                //_markeropt.title(response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText());
                                catch (Exception ex) {
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_LONG).show();
                                    Helper.logger(ex);
                                }
                            }

                            @Override
                            public void onFailure(Throwable t) {
                                Log.d(Constants.LOG_TAG, t.toString());
                            }
                        });

//                        String url = "http://meadumandal.website/sammAPI/";
//                        Retrofit retrofit = new Retrofit.Builder()
//                                .baseUrl(url)
//                                .addConverterFactory(GsonConverterFactory.create())
//                                .build();
//                        RetrofitDatabase service = retrofit.create(RetrofitDatabase.class);
//                        Call<UserPOJO> call = service.getUserDetails(username, username);
//                        call.enqueue(new Callback<UserPOJO>() {
//                            @Override
//                            public void onResponse(Response<UserPOJO> response, Retrofit retrofit) {
//                                try {
//                                    signIn(response.body().getEmailAddress(), password, response.body().getLastName(), response.body().getFirstName(), response.body().getUsername());
//                                    }
//                                    //_markeropt.title(response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText());
//                                catch (Exception e) {
//                                    Log._drawable(LOG_TAG, "There is an error");
//                                    e.printStackTrace();
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Throwable t) {
//                                Log._drawable(LOG_TAG, t.toString());
//                            }
//                        });
                    }

                }
            });

        }
        else{
            Log.i(TAG, "device is not online");
            _helper.showNoInternetPrompt(this);
        }
    }
    private void signIn(final String param_email, String param_password, final String param_lastname, final String param_firstname, final String param_username, final String param_deviceId)
    {
        userDatabaseRef = userDatabase.getReference();
        auth.signInWithEmailAndPassword(param_email, param_password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Helper.logger(task.getException());
                        }
                        else
                        {
                            try{

                                String firstName = param_firstname;
                                String lastName = param_lastname;
                                String username =  param_username;
                                String deviceId = param_deviceId;
                                boolean isDriver = false;
                                if(param_email.equals(_constants.DRIVER_EMAILADDRESS))
                                    isDriver = true;

                                userDatabaseRef.child(sessionManager.getUsername()).removeValue();
                                if(checkIfEmailVerified()){
                                    sessionManager.CreateLoginSession(firstName,lastName, username,param_email,isDriver, false, deviceId, false);
                                    Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                    startActivity(intent);
                                    finish();
                                    Toast.makeText(LoginActivity.this, ("Logged in as "+ username ), Toast.LENGTH_LONG).show();
                                }
                                else{
                                    Toast.makeText(LoginActivity.this, ("Unable to log in, E-mail address is not yet verified."), Toast.LENGTH_LONG).show();
                                }

                            }
                            catch(Exception ex)
                            {
                                Helper.logger(ex);
                            }

                        }
                    }
                });


    }

//    public void SignIn(View v)
//    {
//        String username  = usernameField.getText().toString();
//        String password = passwordField.getText().toString();
////        HashMap<String,String> parameters = new HashMap<>();
////        parameters.put("URL", "http://meadumandal.website/commuteBuddyAPI/signIn.php?");
////        parameters.put("username", username);
////        parameters.put("password", password);
////        Helper helper = new Helper();
////        new mySQLDataProvider(getApplicationContext(), LoginActivity.this, "Signing In...").execute(parameters);
//
////        Intent menu = new Intent(LoginActivity.this, MenuActivity.class);
////      startActivity(menu);
//    }


    public void SignUp(View v)
    {
        MenuActivity.buttonEffect(v);
        try {
            Intent SignUpForm = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(SignUpForm);
            finish();
        }catch(Exception ex){
            String test = ex.getMessage();
        }
    }
    private void handleFacebookAccessToken(final AccessToken token, final String LastName, final String FirstName, final String Email) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        final AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = auth.getCurrentUser();
                            saveUserDetails(FirstName,LastName,token.getUserId().toString(),Email);
                            sessionManager.CreateLoginSession(FirstName,LastName,token.getUserId().toString(),Email,false, false, "", true);
                            LoginProgDiag.hide();
                            Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(LoginActivity.this, ("Logged in as "+ FirstName +" "+ LastName), Toast.LENGTH_LONG).show();


                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed : " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void saveUserDetails(String firstName, String lastName, String username, String emailAddress)
    {
        User user = new User(username, firstName, lastName, emailAddress);
            userDatabase = FirebaseDatabase.getInstance();
            userDatabaseRef = userDatabase.getReference("users");

        userDatabaseRef.child(username).setValue(user);
        new mySQLSignUp(getApplicationContext(), this).execute(username, firstName, lastName, emailAddress);
    }
    private void ShowLogInProgressDialog(String From){
        LoaderDialog LD_FBLoginLoader = new LoaderDialog(LoginActivity.this,From +" Log In",  "Please wait as we log you in...");
        LD_FBLoginLoader.show();
    }
    private Boolean checkIfEmailVerified()
    {

        Boolean result =false;
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userEmailAddress = user.getEmail().toLowerCase();
        if(userEmailAddress.equals(_constants.ADMIN_EMAILADDRESS) || userEmailAddress.equals(_constants.DRIVER_EMAILADDRESS))
            return true;
        if (user.isEmailVerified())
        {
            result = true;
        }
        else
        {
            FirebaseAuth.getInstance().signOut();
        }
        return  result;
    }
    private void PlayButtonClickSound(){
        _buttonClick.start();
    }
}
