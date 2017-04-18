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
    private int gridSize, width, height, widthGridCount, heightGridCount;


    public ShipsCanvas(Context context, int size, int shipId) {
        super(context);
        gridSize = size;
        shipBitmap = BitmapFactory.decodeResource(getResources(), shipId);
        setGridCounts(shipId);
        setDimensions(widthGridCount, heightGridCount);
        shipBitmap = Bitmap.createScaledBitmap(shipBitmap, width, height, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(shipBitmap != null){
            canvas.drawBitmap(shipBitmap,0, 0, null);
        }

    }
    private void setGridCounts(int id){
        if(id == R.drawable.alien_onebytwo) {
            widthGridCount = 1;
            heightGridCount = 2;
        }else if(id  == R.drawable.alien_twobytwo) {
            widthGridCount = 2;
            heightGridCount = 2;
        }else if(id  == R.drawable.alien_threebyfour) {
            widthGridCount = 3;
            heightGridCount = 4;
        }else{
            widthGridCount = 1;
            heightGridCount = 1;
        }
    }

    private void setDimensions(int wC, int hC){
        width  = gridSize * wC;
        height = gridSize * hC;
    }

    public int getShipWidth() {return width;}
    public int getShipHeight() {return height;}
    public int getWidthGridCount() {return widthGridCount;}
    public int getHeightGridCount() {return heightGridCount;}
}
