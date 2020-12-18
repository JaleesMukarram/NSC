package com.example.nustsocialcircle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

public class Splash extends AppCompatActivity {

    private Button GetStartedBTN;
    private ProgressBar LoadingPB;
    private ImageView NUSTImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initializeViews();


        onReady();

    }

    private void initializeViews() {

        GetStartedBTN = this.findViewById(R.id.BTNSplashGetStarted);
        LoadingPB = this.findViewById(R.id.PBSplashLoading);
        NUSTImage = this.findViewById(R.id.IVSplashBackgroundShowing);
    }

    private void onReady() {

        LoadingPB.setVisibility(View.INVISIBLE);
        GetStartedBTN.setVisibility(View.VISIBLE);

        NUSTImage.animate().scaleX(2).scaleY(2).alpha(11).setDuration(2000);

        GetStartedBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Splash.this, SignIn.class));
            }
        });

    }
}
