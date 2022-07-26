package com.lizyaver.instagram.Activity;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;

import com.lizyaver.instagram.R;

public class SplashActivity extends AppCompatActivity {

    private ProgressBar _ProgressBar;
    ImageView _logomatirialuiux;
    private int progressStatus = 0;
    private Handler handler = new Handler();
    Animation aniFade;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        /////*     initialize view   */////
        _ProgressBar = findViewById (R.id.progress_bar);
        _logomatirialuiux = findViewById(R.id.id_materialuiux_ImageView);
        aniFade = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        _logomatirialuiux.startAnimation(aniFade);

    }
    @Override
    protected void onStart() {
        super.onStart();
        startloading();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startloading();

    }

    private void startloading() {
        // Start long running operation in a background thread
        new Thread(new Runnable() {
            public void run() {
                while (progressStatus < 100) {
                    progressStatus += 4;
                    // Update the progress bar and display the
                    //current value in the text view
                    handler.post(new Runnable() {
                        public void run() {

                            _ProgressBar.setProgress(progressStatus);
                            if (progressStatus == 100){
                                Intent i = new Intent(SplashActivity.this, StartActivity.class);
                                startActivity(i);

                            }
                        }
                    });
                    try {
                        // Sleep for 200 milliseconds.
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }
}