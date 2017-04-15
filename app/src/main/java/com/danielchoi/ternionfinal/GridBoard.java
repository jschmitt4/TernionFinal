package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.annotation.Nullable;
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


    final static int maxN = 12;
    private View[][] ivCell = new View[maxN][maxN];
    int white = Color.parseColor("#FFFFFF");
    View temp;
    Context context;
    LinearLayout linBoardGame, linRow, searchRow;
    View searchView;
    public Vibrator vb;
    public int boardID, sizeOfCell;

    public GridBoard(Context context, int bID){
        super();
        this.context = context;
        boardID = bID;
    }


    public void setBoard(){
        linBoardGame = (LinearLayout) ((Activity)context).findViewById(boardID);
        linBoardGame.setOnTouchListener(this);
        RelativeLayout.LayoutParams marginParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        vb =  (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        sizeOfCell = Math.round(ScreenWidth() / (maxN + (1)));
        marginParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        marginParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        marginParam.setMargins(0,0,0,(Math.round(sizeOfCell*3)/2));
        linBoardGame.setLayoutParams(marginParam);
        LinearLayout.LayoutParams lpRow = new LinearLayout.LayoutParams(sizeOfCell * maxN, sizeOfCell);
        LinearLayout.LayoutParams lpCell = new LinearLayout.LayoutParams(sizeOfCell, sizeOfCell);

        for (int x = 0; x < maxN; x++) {
            linRow = new LinearLayout(context);
            for (int y = 0; y < maxN; y++) {
                ivCell[x][y] = new View(context);
                ivCell[x][y].setBackgroundResource(R.drawable.grid);
                linRow.addView(ivCell[x][y], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }

        setDefaultShips();

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
                View down = findViewHelper(x, y);
                if(down != null){
                    down.setBackgroundColor(white);
                    temp = down;}
                break;
            case MotionEvent.ACTION_MOVE:
                temp.setBackgroundResource(R.drawable.grid);
                View move = findViewHelper(x, y);
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
    private View findViewHelper(int x, int y) {
        for (int i = 0; i < maxN; i++) {
            searchRow = (LinearLayout) linBoardGame.getChildAt(i);
            if (y > searchRow.getTop() && y < searchRow.getBottom()) {
                for (int j = 0; j < maxN; j++) {
                    searchView = searchRow.getChildAt(j);
                    if (x > searchView.getLeft() && x < searchView.getRight()) {
                        return searchView;
                    }//if
                }//for
            }//if
        }//for
        return null;
    }

    private void setDefaultShips(){
        //Still working on getting this to work.

        ImageView ship = new ImageView(context);
        RelativeLayout rl = (RelativeLayout) ((Activity)context).findViewById(R.id.activity_game);
        rl.addView(ship);
        ship.setImageResource(R.drawable.alien_onebyone);
        ship.setX(ivCell[9][9].getX());
        ship.setY(ivCell[1][1].getY());
        ship.getLayoutParams().height = sizeOfCell;
        ship.getLayoutParams().width = sizeOfCell;

    }

    public void hideGrid(){linBoardGame.setVisibility(View.GONE);}

    public void showGrid(){
        linBoardGame.setVisibility(View.VISIBLE);
    }
}
