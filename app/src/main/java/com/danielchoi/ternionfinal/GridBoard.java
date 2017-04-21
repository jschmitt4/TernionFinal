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
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Daniel on 4/8/2017.
 */

public class GridBoard extends Activity implements OnTouchListener {

    final static int maxN = 10;
    int score = 0; // Player score adds 200 points for each hit.
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    int white = Color.parseColor("#FFFFFF");
    private View temp,temp2, touchingView;
    private Context context;
    private RelativeLayout gridContainer;
    private LinearLayout linBoardGame, linRow, searchRow;
    private View searchView;
    private boolean atBottom = false;
    public Vibrator vb;
    private int boardID, sizeOfCell, prevShipSize, margin, rowNum, columnNum;
    private Ship ship = new Ship();

    public GridBoard(Context context, int bID){
        super();
        this.context = context;
        boardID = bID;
        setVariables();
        setBoard();
    }

    /**
     * Sets up global variables & on Touch Listeners
     */
    private void setVariables(){
        vb =  (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        linBoardGame = (LinearLayout) ((Activity)context).findViewById(boardID);
        gridContainer = (RelativeLayout) ((Activity)context).findViewById(R.id.gridContainer);
        gridContainer.setOnTouchListener(this);
        linBoardGame.setOnTouchListener(this);
        sizeOfCell = Math.round(ScreenWidth() / (maxN + (1)));
        prevShipSize = 0;
    }

    /**
     * This method alignment and margin of the new grid.
     * It creates grid's rows and columns based on maxN
     * Each grid is a View and stored in ivCell[][]
     * Sets the default background image to a grid.
     */
    public void setBoard(){
        margin = Math.round((sizeOfCell*3)/2);
        RelativeLayout.LayoutParams marginParam = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        marginParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        marginParam.addRule(RelativeLayout.CENTER_HORIZONTAL);
        marginParam.setMargins(0,0,0,(margin));
        gridContainer.setLayoutParams(marginParam);

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

    private void createShips(){
    }

    private void setDefaultShips(){
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
    private float ScreenHeight() {
        RelativeLayout linBoardGame = (RelativeLayout) ((Activity)context).findViewById(R.id.activity_game);
        Resources r = linBoardGame.getResources();
        DisplayMetrics d = r.getDisplayMetrics();
        return d.heightPixels;
    }


    /**
     * This is a temp animation to follow finger movements
     * Might not need this in main game functionality.
     * Game visual feature
     * It calls findViewHelper
     * The temp & temp2 variable are holding previous location to revert back to grid.
     * @param view
     * @param motionEvent
     * @return true
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view == linBoardGame) {
            /**
            if (!defaultSet) {
                createShips();
                setDefaultShips();
            }
             */
            int x = Math.round(motionEvent.getX());
            int y = Math.round(motionEvent.getY());

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    Log.i("GRID", "Coordinates");
                    Log.i("X: ", "" + x);
                    Log.i("Y: ", "" + y);

                    // Create Ships;
                    ship.setShipName("alien_onebyone");
                    ship.setShipSize(1);
                    ship.setShipLocation(new int[]{x, y});
                    ship.setShipPieces(new int[]{R.drawable.alien_onebytwo_top,R.drawable.alien_onebytwo_bottom});
                    Log.i("Ship Log ", ship.toString());

                    if(temp != null) {
                        temp.setBackgroundResource(R.drawable.grid);
                        if(temp2 != null){
                            temp2.setBackgroundResource(R.drawable.grid);
                        }
                    }
                    touchingView = findViewHelper(ship.shipLocation[0], ship.shipLocation[1]);
                    if (touchingView != null) {
                        //touchingView.setBackgroundColor(white);
                        setSurroundingViews(ship.shipPieces[0]);
                        touchingView.setBackgroundResource(ship.shipPieces[0]);
                        temp = touchingView;
                    }
                    break;
//                case MotionEvent.ACTION_MOVE:
//                    temp.setBackgroundResource(R.drawable.grid);
//                    if(temp2 != null){temp2.setBackgroundResource(R.drawable.grid);}
//                    touchingView = findViewHelper(x, y);
//                    if (touchingView != null) {
//                        setSurroundingViews(R.drawable.alien_onebytwo_top);
//                        touchingView.setBackgroundResource(R.drawable.alien_onebytwo_top);
//                        if (touchingView != temp && !atBottom) vb.vibrate(1);
//                        temp = touchingView;
//                    }else if (touchingView == null){
//                        setSurroundingViews(R.drawable.alien_onebytwo_top);
//                        temp.setBackgroundResource(R.drawable.alien_onebytwo_top);
//                    }
//
//                    break;
                case MotionEvent.ACTION_UP:
                    //This is where we would place our figures!
                    break;
            }
        }else{
            int x = Math.round(motionEvent.getX());
            int y = Math.round(motionEvent.getY());
            Log.i("CONTAINER", "COORDINATES");
            Log.i("X: ", "" + x);
            Log.i("Y: ", "" + y);

        }
        return true;
    }
    private boolean checkGridCell(){
        // Test Hitting / Scoring Function
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
                        rowNum = i;
                        columnNum = j;
                        if(searchView == ivCell[i][j]){
                            Log.i("ROW to CELL", "MATCH!");
                            // Test Hitting Function
                            boolean isOccupied = checkGridCell();
                            if(isOccupied){
                                score += 200;
                                Log.i("Score = ", "" + score);
                            }
                        }
                        else Log.i("ROW to CELL", "MisMatch!");
                        return searchView;
                    }//if
                }//for search View
            }//if
        }//for searchRow
        return null;
    }

    /**
     * This currently receives the id from onTouch to create the bottom view.
     * temp hard code.
     * @param id
     */
    private void setSurroundingViews(int id){
        if (id == ship.shipPieces[0]) {
            searchRow = (LinearLayout)  linBoardGame.getChildAt(rowNum+1);
            if(searchRow != null) {
                searchView = searchRow.getChildAt(columnNum);
                searchView.setBackgroundResource(ship.shipPieces[1]);
                temp2 = searchView;
                atBottom = false;
            }else{
                searchRow = (LinearLayout)  linBoardGame.getChildAt(rowNum);
                searchView = searchRow.getChildAt(columnNum);
                temp2 = searchView;
                temp2.setBackgroundResource(ship.shipPieces[1]);
                searchRow = (LinearLayout)  linBoardGame.getChildAt(rowNum-1);
                searchView = searchRow.getChildAt(columnNum);
                touchingView = searchView;
                atBottom = true;
            }
        } else {
            Toast.makeText(context, "False" + Toast.LENGTH_SHORT, Toast.LENGTH_SHORT).show();
        }
    }


    public void hideGrid(){linBoardGame.setVisibility(View.GONE);}
    public void showGrid(){
        linBoardGame.setVisibility(View.VISIBLE);
    }


}
