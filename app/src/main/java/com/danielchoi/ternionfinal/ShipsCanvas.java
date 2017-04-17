package com.danielchoi.ternionfinal;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by Daniel on 4/14/2017.
 */

public class ShipsCanvas extends View {
    private Bitmap shipBitmap;
    private int gridSize, shipId;


    public ShipsCanvas(Context context, int size, int shipId) {
        super(context);
        gridSize = size;
        this.shipId = shipId;
        shipBitmap = BitmapFactory.decodeResource(getResources(), shipId);
        createScaledBitmap();

    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(shipBitmap != null){
            canvas.drawBitmap(shipBitmap,0, 0, null);
        }

    }

    /**
     * This converts all the drawables to bitmaps to the proper size in relation to the screen.
     * Then adds them to the bitmapList
     */
    private void createScaledBitmap(){
        if(shipBitmap != null)
            if(shipId == R.drawable.alien_onebytwo) {
                shipBitmap = Bitmap.createScaledBitmap(shipBitmap, gridSize, gridSize*2, true);
            }else if(shipId == R.drawable.alien_twobytwo) {
                shipBitmap = Bitmap.createScaledBitmap(shipBitmap, gridSize*2, gridSize*2, true);
            }else if(shipId == R.drawable.alien_threebyfour) {
                shipBitmap = Bitmap.createScaledBitmap(shipBitmap, gridSize*3, gridSize*4, true);
            }else{
                shipBitmap = Bitmap.createScaledBitmap(shipBitmap, gridSize, gridSize, true);
            }
    }
}
