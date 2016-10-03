package com.example.eddie.memorygame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity {

    private static final int ONE_SECOND = 1000;
    private static final int MATCH = -2;
    private static final int NOMATCH = -1;
    final ImageAdapter adapter = new ImageAdapter(this);
    ArrayList<Integer> matchedTiles = new ArrayList<>();
    int points[] = {0};
    Animation slide, bounce;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        adapter.shuffle();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        final TextView pointsView = (TextView) findViewById(R.id.pointsText);
        final GridView gridView = (GridView) findViewById(R.id.tileGrid);
        bounce = AnimationUtils.loadAnimation(this, R.anim.bounce);
        gridView.setAnimation(bounce);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                slide = AnimationUtils.loadAnimation(view.getContext(), R.anim.slide);
                view.setAnimation(slide);
                slide.startNow();

                if (matchedTiles.contains(position))
                    return;
                Handler handler = new Handler();
                final int pick[] = adapter.flipTile(position);
                if (pick[0] == NOMATCH) {
                    gridView.setAdapter(adapter);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.resetTiles(pick[1], pick[2]);
                            gridView.setAdapter(adapter);
                        }
                    }, ONE_SECOND);
                }
                else if (pick[0] == MATCH) { // define constant for MATCH maybe?
                    matchedTiles.add(pick[1]);
                    matchedTiles.add(pick[2]);
                    points[0]++;
                    pointsView.setText("Points: " + points[0]);
                    gridView.setAdapter(adapter);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            adapter.blankMatchedTiles(new int[]{pick[1], pick[2]});
                            gridView.setAdapter(adapter);
                        }
                    }, ONE_SECOND);
                    if (points[0] == 10)
                        Toast.makeText(getApplicationContext(), "You Win!", Toast.LENGTH_LONG).show();
                }
                else {
                    gridView.setAdapter(adapter);
                }
            }
        });
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.restart) {
            SharedPreferences preferences = getSharedPreferences("TEST", Context.MODE_PRIVATE);
            preferences.edit().clear().commit();
            Intent intent = getIntent();
            finish();
            startActivity(intent);
            return true;
        }
        else if (id == R.id.shuffle) {
            adapter.shuffle();
            adapter.shuffle(points[0]);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {

        super.onStop();
        SharedPreferences preferences = getSharedPreferences("TEST", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        int[] oldImages = adapter.getImages();
        int[] oldTiles = adapter.getTiles();
        for (int i = 0; i < 20; i++) {
            editor.putInt("image_" + i, oldImages[i]);
            editor.putInt("tile_" + i, oldTiles[i]);
        }
        for (int i = 0; i < matchedTiles.size(); i++) {
            editor.putInt("matched_" + i, matchedTiles.get(i));
        }
        editor.putInt("openTileIndex", adapter.getOpenTileIndex());
        editor.putInt("points", points[0]);
        editor.putString("notFirstRun", "true");
        editor.commit();
    }

    @Override
    protected void onResume() {

        super.onResume();
        SharedPreferences preferences = getSharedPreferences("TEST", Context.MODE_PRIVATE);

        if (!preferences.contains("notFirstRun")) {
            return;
        }
        int[] images = new int[20];
        int[] tiles = new int[20];
        ArrayList<Integer> matched = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            images[i] = preferences.getInt("image_" + i, images[i]);
            tiles[i] = preferences.getInt("tile_" + i, tiles[i]);
        }
        adapter.setImages(images);
        adapter.setTiles(tiles);
        int i = 0;
        int tmp = 0;
        while (preferences.contains("matched_" + i)) {
            tmp = preferences.getInt("matched_" + i, tmp);
            matchedTiles.add(tmp);
            i++;
        }
        adapter.blankMatchedTiles(matchedTiles);
        points[0] = preferences.getInt("points", points[0]);
        int openTileIndex = 0;
        openTileIndex = preferences.getInt("openTileIndex", openTileIndex);
        adapter.setOpenTileIndex(openTileIndex);
    }
}
