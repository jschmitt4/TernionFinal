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

import java.util.ArrayList;

/**
 * Created by Daniel on 4/8/2017.
 */

public class GridBoard extends Activity implements OnTouchListener {

    final static int maxN = 10;
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    int white = Color.parseColor("#FFFFFF");
    private View temp, touchingView;
    private Context context;
    private ArrayList<ShipsCanvas> ships;
    private RelativeLayout homeLayout;
    private LinearLayout linBoardGame, linRow, searchRow;
    private View searchView;
    public Vibrator vb;
    private int boardID, sizeOfCell;
    private int alienShipsId[]={
            R.drawable.mushroom,       R.drawable.crater,
            R.drawable.alien_onebyone, R.drawable.alien_onebytwo,
            R.drawable.alien_twobytwo, R.drawable.alien_threebyfour,};

    public GridBoard(Context context, int bID){
        super();
        this.context = context;
        boardID = bID;
        setVariables();
        setBoard();
        setDefaultShips();
    }

    /**
     * Sets up global variables & on Touch Listeners
     */
    private void setVariables(){
        vb =  (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);

        linBoardGame = (LinearLayout) ((Activity)context).findViewById(boardID);
        linBoardGame.setOnTouchListener(this);
        sizeOfCell = Math.round(ScreenWidth() / (maxN + (1)));
        ships = new ArrayList< >();
    }

    /**
     * This method alignment and margin of the new grid.
     * It creates grid's rows and columns based on maxN
     * Each grid is a View and stored in ivCell[][]
     * Sets the default background image to a grid.
     */
    public void setBoard(){
        RelativeLayout.LayoutParams marginParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        marginParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        marginParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        marginParam.setMargins(0,0,0,(Math.round(sizeOfCell*3)/2));
        linBoardGame.setLayoutParams(marginParam);
        homeLayout = (RelativeLayout)((Activity)context).findViewById(R.id.activity_game);

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

    private void setDefaultShips(){
        //Still working on getting this to work.
        for(int i = 0; i <alienShipsId.length; i++){
            ShipsCanvas shipsCanvas = new ShipsCanvas(context, sizeOfCell, alienShipsId[i]);
            ships.add(shipsCanvas);
            //ships.get(i).setOnClickListener((View.OnClickListener) context);
            homeLayout.addView(ships.get(i));
            ships.get(i).setX(125*i);//Actual Coordniates would go here
            ships.get(i).setY(175*i);


            Log.i("Ships ID: ", ""+ships.get(i).getX());
            }

    }

    /**
     * This method get's the screen size from the resources
     * @return the width in pixels
     */
    private float ScreenWidth() {
        RelativeLayout linBoardGame = (RelativeLayout) ((Activity)context).findViewById(R.id.activity_game);
        Resources r = linBoardGame.getResources();
        DisplayMetrics d = r.getDisplayMetrics();
        return d.widthPixels;
    }

    /**
     * This is a temp animation to follow finger movements
     * Might not need this in main game functionality.
     * Game visual feature
     * It calls findViewHelper
     * @param view
     * @param motionEvent
     * @return true
     */
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
                touchingView = findViewHelper(x, y);
                if(touchingView != null){
                    touchingView .setBackgroundColor(white);
                    temp = touchingView ;}
                break;
            case MotionEvent.ACTION_MOVE:
                temp.setBackgroundResource(R.drawable.grid);
                touchingView  = findViewHelper(x, y);
                if(touchingView  != null && touchingView != temp)vb.vibrate(1);
                if(touchingView  != null) {
                    touchingView .setBackgroundColor(white);
                    temp = touchingView ;}
                break;
            case MotionEvent.ACTION_UP:
                //This is where we would place our figures!
                break;
        }
        return true;
    }

    /**
     * A nested for loop that scans each cell in the row to find which view is being touched
     * Takes in the x & y coordinates that was touched
     * @param x
     * @param y
     * @return the found view
     */
    @Nullable
    private View findViewHelper(int x, int y) {
        for (int i = 0; i < maxN; i++) {
            searchRow = (LinearLayout) linBoardGame.getChildAt(i); //Current row it is checking

            if (y > searchRow.getTop() && y < searchRow.getBottom()) {//If the Y coordinates are within the row Check which cell
                for (int j = 0; j < maxN; j++) {
                    searchView = searchRow.getChildAt(j); //Current View of the current searchRow
                    if (x > searchView.getLeft() && x < searchView.getRight()) {//If the x coordinates are within the view, View found!
                        return searchView;
                    }//if
                }//for search View
            }//if
        }//for searchRow
        return null;
    }


    /**
     * Hides the grid
     */
    public void hideGrid(){linBoardGame.setVisibility(View.GONE);}

    /**
     * Shows the grid
     */
    public void showGrid(){
        linBoardGame.setVisibility(View.VISIBLE);
    }


}
