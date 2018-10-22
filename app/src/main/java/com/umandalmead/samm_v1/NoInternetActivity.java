package com.umandalmead.samm_v1;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class NoInternetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_internet);
        Button btn_tryAgain = (Button) findViewById(R.id.btnTryAgain);
        MenuActivity.buttonEffect(btn_tryAgain);
        btn_tryAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Helper.isOnline(NoInternetActivity.this,getApplicationContext()))
                    startActivity(new Intent(NoInternetActivity.this, LoginActivity.class));
            }
        });
    }
}
