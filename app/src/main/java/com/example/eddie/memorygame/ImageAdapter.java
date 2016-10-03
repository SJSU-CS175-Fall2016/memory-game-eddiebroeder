package com.example.eddie.memorygame;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created by Eddie on 9/15/2016.
 */
public class ImageAdapter extends BaseAdapter implements Serializable {

    private Context context;
    private final int MATCH = -2;
    private final int NOMATCH = -1;
    private final int FIRST_PICK = -3;
    private final int tux = R.drawable.tux;
    private final int blank = R.drawable.blank;
    private int openTileIndex = -1;
    private int[] tiles = {
            tux, tux, tux, tux,
            tux, tux, tux, tux,
            tux, tux, tux, tux,
            tux, tux, tux, tux,
            tux, tux, tux, tux
    };
    private int[] images = {
            R.drawable.check, R.drawable.emojii,
            R.drawable.bird, R.drawable.icon,
            R.drawable.turtle, R.drawable.notepad,
            R.drawable.icon, R.drawable.check,
            R.drawable.usb, R.drawable.turtle,
            R.drawable.usb, R.drawable.windows,
            R.drawable.bird, R.drawable.emojii,
            R.drawable.mario, R.drawable.grad,
            R.drawable.windows, R.drawable.mario,
            R.drawable.grad, R.drawable.notepad
    };

    public ImageAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return tiles.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    public int[] getImages() {
        return images;
    }

    public int[] getTiles() {
        return tiles;
    }

    public int getOpenTileIndex() {
        return openTileIndex;
    }

    public void setImages(int[] images) {
        this.images = images;
    }

    public void setTiles(int[] tiles) {
        this.tiles = tiles;
    }

    public void setOpenTileIndex(int index) {
        this.openTileIndex = index;
    }

    public void blankMatchedTiles(int[] indices) {
        for (int i = 0; i < indices.length; i++) {
           tiles[indices[i]] = blank;
        }
    }

    public void blankMatchedTiles(ArrayList<Integer> indices) {
        for (int i = 0; i < indices.size(); i++) {
            tiles[indices.get(i)] = blank;
        }
    }

    public void resetTiles(int index1, int index2) {
        tiles[index1] = tux;
        tiles[index2] = tux;
    }

    public int[] flipTile(int index) {
        if (openTileIndex != -1 && images[index] == images[openTileIndex] && index != openTileIndex) {
            int index2 = openTileIndex;
            openTileIndex = -1;
            tiles[index] = images[index];
            return new int[] {MATCH, index, index2};
        }
        else if (openTileIndex != -1 && images[index] != openTileIndex && index != openTileIndex) {
            int index2 = openTileIndex;
            openTileIndex = -1;
            tiles[index] = images[index];
            return new int[] {NOMATCH, index, index2};
        }
        openTileIndex = index;
        tiles[index] = images[index];
        return new int[] {FIRST_PICK, index};
    }

    public void shuffle() {
        Random rnd = ThreadLocalRandom.current();
        for (int i = images.length - 1; i > 0; i--) {
            int index = rnd.nextInt(i + 1);
            int tmp = images[index];
            images[index] = images[i];
            images[i] = tmp;
        }
    }

    public void shuffle(int tileCount) {
        int tilesLeft = 10 - (tileCount * 2); // get amount of tiles left
        int oldTiles[] = new int[tiles.length];
        for(int i = 0; i < tiles.length; i++)
            oldTiles[i] = tiles[i];
        Random rnd = ThreadLocalRandom.current();
        int tmpIndex = rnd.nextInt(oldTiles.length - 1);

        for (int i = 0; i < tilesLeft; i++) {
            if (tiles[i] == blank) {
                while (oldTiles[tmpIndex] == blank || tmpIndex <= i)
                    tmpIndex = rnd.nextInt(tiles.length);
                tiles[i] = oldTiles[tmpIndex];
                tiles[tmpIndex] = blank;
                oldTiles[tmpIndex] = blank;
            }
        }
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ImageView imageView;
        if (view == null) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new GridView.LayoutParams(250,250));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(8, 8, 8, 8);
        }
        else {
            imageView = (ImageView) view;
        }
        imageView.setImageResource(tiles[i]);
        return imageView;
    }
}
