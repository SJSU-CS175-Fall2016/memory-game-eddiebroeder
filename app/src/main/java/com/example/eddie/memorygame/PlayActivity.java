package com.example.eddie.memorygame;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;

public class PlayActivity extends AppCompatActivity {

    private static final int ONE_SECOND = 1000;
    private static final int MATCH = -2;
    private static final int NOMATCH = -1;
    private static final Integer[] testArray = new Integer[] {0, 1, 2};
    final ImageAdapter adapter = new ImageAdapter(this);
    ArrayList<Integer> matchedTiles = new ArrayList<>();
    int points[] = {0};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        adapter.shuffle();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        final TextView pointsView = (TextView) findViewById(R.id.pointsText);
        final GridView gridView = (GridView) findViewById(R.id.tileGrid);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
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
