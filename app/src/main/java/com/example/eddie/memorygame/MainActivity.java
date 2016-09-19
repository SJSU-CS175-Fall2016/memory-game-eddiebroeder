package com.example.eddie.memorygame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("TEST", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();
    }

    public void viewRules(View view) {
        Intent rulesIntent = new Intent(this, RuleActivity.class);
        startActivity(rulesIntent);
    }

    public void playGame(View view) {
        Intent playIntent = new Intent(this, PlayActivity.class);
        startActivity(playIntent);
    }
    // use intent.getExtra("score") to retrieve the score (I think)
    // and/or setResult from the PlayActivity (but is getExtra the way to get that result? I don't think so, that's putExtra I think)
    // to get result from setResult, start the activity with startActivityForResult()
}
