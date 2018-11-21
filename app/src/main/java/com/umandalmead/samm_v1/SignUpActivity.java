package com.umandalmead.samm_v1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.umandalmead.samm_v1.EntityObjects.FirebaseEntities.User;
import com.umandalmead.samm_v1.EntityObjects.Users;
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
    private Button btn_signUp;
    FirebaseDatabase userDatabase;
    DatabaseReference userDatabaseReference;
    SessionManager sessionManager;
   // private TextView link_driver;
    private static String TAG = "mead";

    private Constants _constants = new Constants();
    private ImageButton FAB_SammIcon;
    private TextView ViewTitle;
    private ShimmerLayout _SL_NewAccount;
    public static Boolean FromNavigatationDrawer=false;
    private Resources _resources;
    private EditText edit_firstName,edit_lastName,edit_emailAddress,edit_username,edit_password,edit_confirmPassword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        auth = FirebaseAuth.getInstance();
        btn_signUp = (Button)findViewById(R.id.btn_save);
        sessionManager = new SessionManager(getApplicationContext());
        _SL_NewAccount = (ShimmerLayout) findViewById(R.id.SL_NewAccount);
        _SL_NewAccount.startShimmerAnimation();
        _resources = getResources();
        edit_firstName = (EditText) findViewById(R.id.edit_firstName);
        edit_lastName = (EditText) findViewById(R.id.edit_lastName);
        edit_emailAddress = (EditText) findViewById(R.id.edit_address);
        edit_username = (EditText) findViewById(R.id.textRoute);
        edit_password = (EditText) findViewById(R.id.edit_password);
        edit_confirmPassword = (EditText) findViewById(R.id.edit_confirmpassword);
        CheckBox checkbox_showpassword = (CheckBox) findViewById(R.id.checkBox_showPassword);

        checkbox_showpassword.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    edit_password.setTransformationMethod(null);
                    edit_confirmPassword.setTransformationMethod(null);
                }
                else {
                    edit_password.setTransformationMethod(new PasswordTransformationMethod());
                    edit_confirmPassword.setTransformationMethod(new PasswordTransformationMethod());
                }
            }
        });



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
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String value = extras.getString("IsFromNavDrawer");
            if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false")) {
                FromNavigatationDrawer= Boolean.valueOf(value);
            }
        }
        InitializeToolbar(MenuActivity._GlobalResource.getString(R.string.title_signup_activity));

        btn_signUp.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                try
                {
                    edit_firstName = (EditText) findViewById(R.id.edit_firstName);
                    edit_lastName = (EditText) findViewById(R.id.edit_lastName);
                    edit_emailAddress = (EditText) findViewById(R.id.edit_address);
                    edit_username = (EditText) findViewById(R.id.textRoute);
                    edit_password = (EditText) findViewById(R.id.edit_password);
                    edit_confirmPassword = (EditText) findViewById(R.id.edit_confirmpassword);


                    String firstName="",
                            lastName="",
                            emailAddress="",
                            username="",
                            password="",
                            confirmPassword="";

                    firstName = edit_firstName.getText().toString();
                    lastName = edit_lastName.getText().toString();
                    emailAddress = edit_emailAddress.getText().toString().trim();
                    username = edit_username.getText().toString();
                    password = edit_password.getText().toString();
                    confirmPassword = edit_confirmPassword.getText().toString();
                    final Users user =new Users(username, emailAddress, firstName, lastName, password, confirmPassword);

                    Users.validateRegistrationDetails(user);


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
                                    try
                                    {
                                        Retrofit retrofit = new Retrofit.Builder()
                                                .baseUrl(_constants.WEB_API_URL)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                                        RetrofitDatabase service = retrofit.create(RetrofitDatabase.class);
                                        Call<UserPOJO> call = service.getUserDetails(user.username, user.emailAddress);
                                        call.enqueue(new Callback<UserPOJO>() {
                                            @Override
                                            public void onResponse(Response<UserPOJO> response, Retrofit retrofit) {

                                                if (response.body() == null)
                                                {
                                                    final LoaderDialog SignUpDialog = new LoaderDialog(SignUpActivity.this, _resources.getString(R.string.title_signup_activity), _resources.getString(R.string.dialog_signup_inprogress));
                                                    SignUpDialog.show();

                                                    auth.createUserWithEmailAndPassword(user.emailAddress, user.password)
                                                            .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {

                                                                    if(!task.isSuccessful())
                                                                    {
                                                                        SignUpDialog.dismiss();
                                                                        ErrorDialog errorDialog = new ErrorDialog(SignUpActivity.this);
                                                                        errorDialog.setErrorMessage(task.getException().getMessage());
                                                                        errorDialog.show();
                                                                    }
                                                                    else
                                                                    {
//                                                                    sessionManager.CreateLoginSession(firstName, lastName, username, emailAddress,  "", false, Constants.PASSENGER_USERTYPE);
                                                                        final FirebaseAuth.AuthStateListener _authListener = new FirebaseAuth.AuthStateListener() {
                                                                            @Override
                                                                            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                                                                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                                                                if (firebaseUser != null) {
                                                                                    //User account creation succesded, send verification email, and redirect to log in page.
                                                                                    _SL_NewAccount.stopShimmerAnimation();
                                                                                    sendVerificationEmail();
                                                                                    saveUserDetails(user.firstName, user.lastName, user.username, user.emailAddress);
                                                                                    FirebaseAuth.getInstance().removeAuthStateListener(this);
                                                                                } else {
                                                                                    //User signed out.
                                                                                    _SL_NewAccount.stopShimmerAnimation();
                                                                                    FirebaseAuth.getInstance().removeAuthStateListener(this);
                                                                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                                                                }
                                                                            }
                                                                        };
                                                                        FirebaseAuth.getInstance().addAuthStateListener(_authListener);
                                                                        SignUpDialog.dismiss();
                                                                    }

                                                                }
                                                            });
                                                }
                                                else {
                                                    ErrorDialog errorDialog = new ErrorDialog(SignUpActivity.this);
                                                    errorDialog.setErrorMessage(getString(R.string.error_username_alreadyexists));
                                                    errorDialog.show();
                                                }

                                            }

                                            @Override
                                            public void onFailure(Throwable t) {
                                                Log.d(TAG, t.toString());
                                            }
                                        });
                                    }
                                    catch(Exception ex)
                                    {
                                        ErrorDialog errorDialog = new ErrorDialog(SignUpActivity.this);
                                        errorDialog.setErrorMessage("Error occured");
                                        errorDialog.show();
                                    }



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
                catch (Exception ex)
                {
                    ErrorDialog errorDialog = new ErrorDialog(SignUpActivity.this);
                    errorDialog.setErrorMessage(ex.getMessage());
                    errorDialog.show();
                }

            }
        });
    }

    @Override
    protected void onResume()
    {
        super.onResume();
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
                            InfoDialog ID_VerificationEmail = new InfoDialog(SignUpActivity.this, getString(R.string.success_please_verify_email));
                            ID_VerificationEmail.show();
                            ID_VerificationEmail.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                @Override
                                public void onDismiss(DialogInterface dialogInterface) {
                                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                                    finish();
                                }
                            });
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
                if(!FromNavigatationDrawer) {
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    startActivity(new Intent(SignUpActivity.this, LoginActivity.class));
                    finish();
                }else{
                    startActivity(new Intent(SignUpActivity.this, MenuActivity.class));
                    overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
                    finish();
                }
            }
        });
        ViewTitle = (TextView) findViewById(R.id.samm_toolbar_title);
        ViewTitle.setTypeface(MenuActivity.FONT_ROBOTO_CONDENDSED_BOLD);
        ViewTitle.setText(fragmentName);
    }

}
