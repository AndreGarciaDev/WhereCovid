package com.asadeltacom.wherecovid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        Handler handle = new Handler();
        handle.postDelayed(new Runnable() {
            @Override public void run() {
                mostrarMaps();
            }
        }, 2000);

    }

    private void mostrarMaps() {
        Intent intent = new Intent(SplashScreenActivity.this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

}
