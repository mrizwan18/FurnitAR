package com.razi.furnitar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class splash_activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_activity);

        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        Animation animation1 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.move35);
        animation1.setInterpolator(new LinearInterpolator());
        animation1.setDuration(700);
        Animation animation2 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.move35);
        animation2.setInterpolator(new LinearInterpolator());
        animation2.setDuration(700);
        Animation animation3 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.move35);
        animation3.setInterpolator(new LinearInterpolator());
        animation3.setDuration(700);
        Animation animation4 = AnimationUtils.loadAnimation(getBaseContext(), R.anim.move50);
        animation4.setInterpolator(new LinearInterpolator());
        animation4.setDuration(700);
        final ImageView splash1 = findViewById(R.id.splash1);
        final ImageView splash2 = findViewById(R.id.splash2);
        final ImageView splash3 = findViewById(R.id.splash3);
        final ImageView splash4 = findViewById(R.id.splash4);

        new CountDownTimer(300, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
            }

            @Override
            public void onFinish() {
                splash1.startAnimation(animation1);
                splash1.setVisibility(View.VISIBLE);
                new CountDownTimer(300, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        splash2.startAnimation(animation2);
                        splash2.setVisibility(View.VISIBLE);
                        new CountDownTimer(300, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                            }

                            @Override
                            public void onFinish() {
                                splash3.startAnimation(animation3);
                                splash3.setVisibility(View.VISIBLE);
                                new CountDownTimer(300, 100) {
                                    @Override
                                    public void onTick(long millisUntilFinished) {
                                    }

                                    @Override
                                    public void onFinish() {
                                        splash4.startAnimation(animation4);
                                        splash4.setVisibility(View.VISIBLE);
                                        new CountDownTimer(1800, 100) {
                                            @Override
                                            public void onTick(long millisUntilFinished) {
                                            }

                                            @Override
                                            public void onFinish() {
                                                int firstTimeLogin = sharedPref.getInt("firstTimeLogin", 0);
                                                if(firstTimeLogin==0) {
                                                    SharedPreferences.Editor editor = sharedPref.edit();
                                                    editor.putInt("firstTimeLogin", 1);
                                                    editor.apply();
                                                    startActivity(new Intent(splash_activity.this, onBoarding.class));
                                                }
                                                else
                                                    startActivity(new Intent(splash_activity.this, Login.class));
                                                finish();
                                            }
                                        }.start();
                                    }
                                }.start();
                            }
                        }.start();
                    }
                }.start();

            }
        }.start();


    }
}
