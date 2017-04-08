package com.danielchoi.ternionfinal;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ActionMode;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

public class GameActivity extends AppCompatActivity
implements View.OnTouchListener {

    final static int maxN = 10;
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    private Context context;
    int white = Color.parseColor("#FFFFFF");
    View temp;
    LinearLayout linBoardGame, linRow, searchRow;
    ImageView searchView;
    private int[] drawCell = new int[1];// hit, player, enemy, background, miss

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        context = getApplicationContext();
        loadResources();
        designBoardGame();
    }

    private void designBoardGame() {
        //layoutparams to optimize size of cell

        linBoardGame = (LinearLayout) findViewById(R.id.boardLayout);
        linBoardGame.setOnTouchListener(this);

        int sizeOfCell = Math.round(ScreenWidth() / (maxN + (1)));
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeOfCell * maxN, sizeOfCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeOfCell, sizeOfCell);

        for (int x = 0; x < maxN; x++) {
            linRow = new LinearLayout(this);
            for (int y = 0; y < 10; y++) {
                ivCell[x][y] = new ImageView(this);
                ivCell[x][y].setBackgroundResource(R.drawable.grid);
                linRow.addView(ivCell[x][y], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }

    }

    private float ScreenWidth() {
        RelativeLayout linBoardGame = (RelativeLayout) findViewById(R.id.activity_game);
        Resources r = linBoardGame.getResources();
        DisplayMetrics d = r.getDisplayMetrics();

        return d.widthPixels;
    }

    private void loadResources() {
        drawCell[0] = R.drawable.blank;

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
                    View down = findView(x, y);
                    down.setBackgroundColor(white);
                    temp = down;
                    break;
            case MotionEvent.ACTION_MOVE:
                    temp.setBackgroundResource(R.drawable.grid);
                    View move = findView(x, y);
                    if(move != null) {
                        move.setBackgroundColor(white);
                        temp = move;}
                    break;
            case MotionEvent.ACTION_UP:
                    temp.setBackgroundResource(R.drawable.grid);
                    //This is where we would place out figures!
                    break;
        }

        return true;
    }


    @Nullable
    private View findView(int x, int y) {
        for (int i = 0; i < 10; i++) {
            searchRow = (LinearLayout) linBoardGame.getChildAt(i);
            if (y > searchRow.getTop() && y < searchRow.getBottom()) {
                for (int j = 0; j < 10; j++) {
                    searchView = (ImageView) searchRow.getChildAt(j);
                    if (x > searchView.getLeft() && x < searchView.getRight()) {
                        return searchView;
                    }//if
                }//for
            }//if
        }//for

        return null;
    }

}