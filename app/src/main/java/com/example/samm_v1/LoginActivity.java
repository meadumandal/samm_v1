package com.example.samm_v1;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.samm_v1.EntityObjects.Destination;
import com.example.samm_v1.EntityObjects.User;
import com.example.samm_v1.POJO.Directions;
import com.example.samm_v1.POJO.UserPOJO;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

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
    private static String TAG = "mead";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);
            super.onCreate(savedInstanceState);
        if(MenuActivity.isOnline()) {
            setContentView(R.layout.activity_login);
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


            if (sessionManager.isLoggedIn()) {
                startActivity(new Intent(LoginActivity.this, MenuActivity.class));
                finish();
            }
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            btn_SignIn.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    progressBar.setVisibility(View.VISIBLE);
                    final String username = usernameField.getText().toString();
                    final String password = passwordField.getText().toString();
                    if (username.trim().isEmpty() || password.trim().isEmpty()) {
                        Toast.makeText(LoginActivity.this, "Invalid username and password", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.VISIBLE);
                    } else {
                        if (Patterns.EMAIL_ADDRESS.matcher(username).matches()) {
                            signIn(username, password);
                        } else {
//                        Toast.makeText(LoginActivity.this, "Invalid username and password", Toast.LENGTH_SHORT).show();
                            String url = "http://meadumandal.website/sammAPI/";
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(url)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            RetrofitUserDetails service = retrofit.create(RetrofitUserDetails.class);
                            Call<UserPOJO> call = service.getUserDetails(username);
                            call.enqueue(new Callback<UserPOJO>() {
                                @Override
                                public void onResponse(Response<UserPOJO> response, Retrofit retrofit) {
                                    try {
                                        signIn(response.body().getEmailAddress(), password);
                                        }
                                        //_markeropt.title(response.body().getRoutes().get(0).getLegs().get(0).getDuration().getText());
                                    catch (Exception e) {
                                        Log.d(TAG, "There is an error");
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Throwable t) {
                                    Log.d(TAG, t.toString());
                                }
                            });


//                            userDatabaseRef = userDatabase.getReference();
//                            userDatabaseRef.child("users").child(username)
//                                    .addValueEventListener(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(DataSnapshot dataSnapshot) {
//                                            if (dataSnapshot != null) {
//                                                String emailAddress = dataSnapshot.child("emailAddress").getValue().toString();
//                                                signIn(emailAddress, password);
//                                            } else {
//                                                Toast.makeText(getApplicationContext(), getString(R.string.error_username_not_exist), Toast.LENGTH_LONG).show();
//                                            }
//                                        }
//
//                                        @Override
//                                        public void onCancelled(DatabaseError databaseError) {
//
//                                        }
//                                    });
//                    userDatabaseRef.  child("users").child(username).child("firstName").setValue("test");
                        }
                    }

                }
            });
        }
        else{
            MenuActivity.HideNetCheckerDialog(getApplicationContext());
        }
    }
    private void signIn(final String email, String password)
    {
        userDatabaseRef = userDatabase.getReference();
        auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressBar.setVisibility(View.GONE);
                        if(!task.isSuccessful())
                        {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            try{
                                Query query = userDatabaseRef.child("users").orderByChild("emailAddress").equalTo(email);
                                query.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {

                                        if(dataSnapshot != null)
                                        {
                                            String value = dataSnapshot.getValue().toString();
                                            String username = value.substring(1, value.indexOf('='));
                                            userDatabaseRef.child("users").child(username)
                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                                            if(dataSnapshot!=null)
                                                            {
                                                                String firstName = dataSnapshot.child("firstName").getValue().toString();
                                                                String lastName = dataSnapshot.child("lastName").getValue().toString();
                                                                String username = dataSnapshot.child("username").getValue().toString();
                                                                sessionManager.CreateLoginSession(firstName,lastName, username,email,false);
                                                                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                                                                startActivity(intent);
                                                                finish();
                                                            }
                                                        }

                                                        @Override
                                                        public void onCancelled(DatabaseError databaseError) {

                                                        }
                                                    });
                                        }
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            catch(Exception ex)
                            {
                                Log.e(TAG, ex.getMessage());
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
        Intent SignUpForm = new Intent(LoginActivity.this, SignUpActivity.class);
        startActivity(SignUpForm);
    }
}
