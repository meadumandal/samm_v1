package com.umandalmead.samm_v1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.umandalmead.samm_v1.EntityObjects.FirebaseEntities.User;
import com.umandalmead.samm_v1.POJO.UserPOJO;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.supercharge.shimmerlayout.ShimmerLayout;
import retrofit.Call;
import retrofit.Callback;
import retrofit.GsonConverterFactory;
import retrofit.Response;
import retrofit.Retrofit;

public class SignUpActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btn_signUp;
    FirebaseDatabase userDatabase;
    DatabaseReference userDatabaseReference;
    SessionManager sessionManager;
   // private TextView link_driver;
    private static String TAG = "mead";
    private ProgressDialog SignUpProgDiag;
    private Constants _constants = new Constants();
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    private ShimmerLayout _SL_NewAccount;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        btn_signUp = (Button)findViewById(R.id.btn_save);
        progressBar= (ProgressBar) findViewById(R.id.progressBar);
        sessionManager = new SessionManager(getApplicationContext());
        _SL_NewAccount = (ShimmerLayout) findViewById(R.id.SL_NewAccount);
        _SL_NewAccount.startShimmerAnimation();
       // link_driver = (TextView) findViewById(R.id.linkDriver);



        if(userDatabase == null && userDatabaseReference ==null)
        {
            userDatabase = FirebaseDatabase.getInstance();
            userDatabaseReference = userDatabase.getReference("users");
        }
//        link_driver.setOnClickListener(new View.OnClickListener(){
//
//            @Override
//            public void onClick(View view) {
//                Intent i = new Intent(SignUpActivity.this, SignUpDriverActivity.class);
//                startActivity(i);
//            }
//        });
        InitializeToolbar(MenuActivity._GlobalResource.getString(R.string.title_signup_activity));
        btn_signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                AlertDialog.Builder builder;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    builder = new AlertDialog.Builder(SignUpActivity.this, android.R.style.Theme_Material_Dialog_Alert);
                } else {
                    builder = new AlertDialog.Builder(SignUpActivity.this);
                }
                builder.setTitle("Create account")
                        .setMessage("Submit details?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                EditText edit_firstName = (EditText) findViewById(R.id.edit_firstName);
                                EditText edit_lastName = (EditText) findViewById(R.id.edit_lastName);
                                EditText edit_emailAddress = (EditText) findViewById(R.id.edit_address);
                                EditText edit_username = (EditText) findViewById(R.id.textRoute);
                                final EditText edit_password = (EditText) findViewById(R.id.edit_password);
                                EditText edit_confirmPassword = (EditText) findViewById(R.id.edit_confirmpassword);

                                final String firstName, lastName, emailAddress, username, confirmPassword;
                                final String password;
                                firstName = edit_firstName.getText().toString();
                                lastName = edit_lastName.getText().toString();
                                emailAddress = edit_emailAddress.getText().toString().trim();
                                username = edit_username.getText().toString();
                                password = edit_password.getText().toString();
                                confirmPassword = edit_confirmPassword.getText().toString();

                                //VALIDATIONS
                                if(TextUtils.isEmpty(username)){
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.error_field_required), "Username"), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(Helper.CheckForSpecialCharacters(username)){
                                    Toast.makeText(getApplicationContext(), "Username must not contain special characters", Toast.LENGTH_LONG).show();
                                    return;

                                }
                                if (TextUtils.isEmpty(emailAddress))
                                {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.error_field_required), "E-mail Address"), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(password))
                                {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.error_field_required), "Password"), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(password.length() < 6)
                                {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.error_shortpassword), "6"), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(firstName))
                                {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.error_field_required), "First name"), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(TextUtils.isEmpty(lastName))
                                {
                                    Toast.makeText(getApplicationContext(), String.format(getString(R.string.error_field_required), "Last name"), Toast.LENGTH_LONG).show();
                                    return;
                                }
                                if(!password.equals(confirmPassword))
                                {
                                    Toast.makeText(getApplicationContext(), getString(R.string.error_passwordnotmatch), Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(_constants.WEB_API_URL)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                RetrofitDatabase service = retrofit.create(RetrofitDatabase.class);
                                Call<UserPOJO> call = service.getUserDetails(username, emailAddress);
                                call.enqueue(new Callback<UserPOJO>() {
                                    @Override
                                    public void onResponse(Response<UserPOJO> response, Retrofit retrofit) {
                                        try {
                                            if (response.body() == null)
                                            {
                                                ShowSignUpProgressDialog();

                                                    auth.createUserWithEmailAndPassword(emailAddress, password)
                                                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                SignUpProgDiag.dismiss();
                                                                if(!task.isSuccessful())
                                                                {
                                                                    Toast.makeText(SignUpActivity.this, task.getException().getMessage().toString(), Toast.LENGTH_LONG).show();
                                                                }
                                                                else
                                                                {
                                                                    sessionManager.CreateLoginSession(firstName, lastName, username, emailAddress,  "", false, Constants.PASSENGER_USERTYPE);
                                                                    final FirebaseAuth.AuthStateListener _authListener = new FirebaseAuth.AuthStateListener() {
                                                                        @Override
                                                                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                                                            if (user != null) {
                                                                               //User account creation succesded, send verification email, and redirect to log in page.
                                                                                _SL_NewAccount.stopShimmerAnimation();
                                                                                sendVerificationEmail();
                                                                                saveUserDetails(firstName, lastName, username, emailAddress);
                                                                            } else {
                                                                                //User signed out.
                                                                                _SL_NewAccount.stopShimmerAnimation();
                                                                                FirebaseAuth.getInstance().removeAuthStateListener(this);
                                                                                startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                            }
                                                                        }
                                                                    };
                                                                    FirebaseAuth.getInstance().addAuthStateListener(_authListener);
                                                                }

                                                            }
                                                        });
                                            }
                                            else {
                                                Toast.makeText(getApplicationContext(), getString(R.string.error_username_alreadyexists), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                        //_markeropt.title(response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText());
                                        catch (Exception ex) {
                                            Helper.logger(ex,true);

                                        }
                                    }

                                    @Override
                                    public void onFailure(Throwable t) {
                                        Log.d(TAG, t.toString());
                                    }
                                });

                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                // do nothing
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();

            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
       // SignUpProgDiag.dismiss();
        progressBar.setVisibility(View.GONE);
    }
    private void sendVerificationEmail()
    {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                            Toast.makeText(getApplicationContext(), getString(R.string.success_please_verify_email), Toast.LENGTH_LONG).show();
                            finish();
                        }
                        else
                        {
                            Toast.makeText(getApplicationContext(), getString(R.string.error_verification_email_not_sent), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    private void saveUserDetails(String firstName, String lastName, String username, String emailAddress)
    {
        User user = new User(username, firstName, lastName, emailAddress);
        userDatabaseReference.child(username).setValue(user);
        new mySQLSignUp(getApplicationContext(), this).execute(username, firstName, lastName, emailAddress, Constants.EMAIL_AUTH_TYPE);
    }
    private void ShowSignUpProgressDialog(){
        SignUpProgDiag = new ProgressDialog(SignUpActivity.this);
        SignUpProgDiag.setMax(100);
        SignUpProgDiag.setMessage("Please wait as we create your account...");
        SignUpProgDiag.setTitle("Sign Up");
        SignUpProgDiag.setIndeterminate(false);
        SignUpProgDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        SignUpProgDiag.setCancelable(false);
        SignUpProgDiag.show();
    }
    public void LoginLinkClicked(View v){
        startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
        finish();
    }
    public void InitializeToolbar(String fragmentName){
        FAB_SammIcon = (ImageButton) findViewById(R.id.SAMMLogoFAB);
        FAB_SammIcon.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        FAB_SammIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _SL_NewAccount.stopShimmerAnimation();
                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                finish();
            }
        });
        ViewTitle = (TextView) findViewById(R.id.samm_toolbar_title);
        ViewTitle.setText(fragmentName);
    }

}
