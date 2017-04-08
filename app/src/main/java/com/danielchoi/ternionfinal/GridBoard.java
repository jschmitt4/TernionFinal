package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by Daniel on 4/8/2017.
 */

public class GridBoard extends Activity implements OnTouchListener {


    final static int maxN = 10;
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    int white = Color.parseColor("#FFFFFF");
    View temp;
    Context context;
    LinearLayout linBoardGame, linRow, searchRow;
    ImageView searchView;
    public Vibrator vb;

    public GridBoard(Context context){
        super();
        this.context = context;
    }


    public void setBoard(){
        linBoardGame = (LinearLayout) ((Activity)context).findViewById(R.id.boardLayout);
        linBoardGame.setOnTouchListener(this);
        vb =  (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        int sizeOfCell = Math.round(ScreenWidth() / (maxN + (1)));
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeOfCell * maxN, sizeOfCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeOfCell, sizeOfCell);

        for (int x = 0; x < maxN; x++) {
            linRow = new LinearLayout(context);
            for (int y = 0; y < maxN; y++) {
                ivCell[x][y] = new ImageView(context);
                ivCell[x][y].setBackgroundResource(R.drawable.grid);
                linRow.addView(ivCell[x][y], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }

    }

    private float ScreenWidth() {
        RelativeLayout linBoardGame = (RelativeLayout) ((Activity)context).findViewById(R.id.activity_game);
        Resources r = linBoardGame.getResources();
        DisplayMetrics d = r.getDisplayMetrics();

        return d.widthPixels;
    }
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int x = Math.round(motionEvent.getX());
        int y = Math.round(motionEvent.getY());
        Log.i("ONTOUCH", "ACTION_DOWN");
        Log.i("X: ", "" + x);
        Log.i("Y: ", "" + y);
        switch (motionEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                if(temp != null)temp.setBackgroundResource(R.drawable.grid);
                View down = findView(x, y);
                if(down != null){
                    down.setBackgroundColor(white);
                    temp = down;}
                break;
            case MotionEvent.ACTION_MOVE:
                temp.setBackgroundResource(R.drawable.grid);
                View move = findView(x, y);
                if(move != null && move!= temp)vb.vibrate(1);
                if(move != null) {
                    move.setBackgroundColor(white);
                    temp = move;}
                break;
            case MotionEvent.ACTION_UP:
                //This is where we would place out figures!
                break;
        }

        return true;
    }

    @Nullable
    private View findView(int x, int y) {
        for (int i = 0; i < maxN; i++) {
            searchRow = (LinearLayout) linBoardGame.getChildAt(i);
            if (y > searchRow.getTop() && y < searchRow.getBottom()) {
                for (int j = 0; j < maxN; j++) {
                    searchView = (ImageView) searchRow.getChildAt(j);
                    if (x > searchView.getLeft() && x < searchView.getRight()) {
                        return searchView;
                    }//if
                }//for
            }//if
        }//for
        return null;
    }


    public void hideGrid(){
        linBoardGame.setVisibility(View.GONE);

    }
    public void showGrid(){
        linBoardGame.setVisibility(View.VISIBLE);
    }
}
