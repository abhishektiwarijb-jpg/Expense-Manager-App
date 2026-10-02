package com.exceldroid.expensemanager;

import static android.view.WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowInsetsController;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Objects;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i  = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        },4000);

        /// //////////////////////////////////system bar color
        Window window = getWindow();
        window.setStatusBarColor(getResources().getColor(R.color.activityColor));
        window.setNavigationBarColor(getResources().getColor(R.color.activityColor));

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.R){
            Objects.requireNonNull(window.getInsetsController()).setSystemBarsAppearance(0, WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS |APPEARANCE_LIGHT_NAVIGATION_BARS);
        }
        else
        {
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
        }
    }
}