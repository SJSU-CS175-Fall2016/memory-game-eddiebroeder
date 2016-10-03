package com.example.eddie.memorygame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("TEST", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    public void playGame(View view) {
        Intent playIntent = new Intent(this, PlayActivity.class);
        startActivity(playIntent);
    }

    public void closeFragment(View view) {
        View v = findViewById(R.id.ruleFragment);
        v.setVisibility(View.GONE);
    }
}
