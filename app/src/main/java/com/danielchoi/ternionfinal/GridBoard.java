package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
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
    public int score = 0; // Player score adds 200 points for each hit.
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    public int white = Color.parseColor("#FFFFFF");
    private View temp,temp2, touchingView;
    private Context context;
    private RelativeLayout gridContainer;
    private LinearLayout linBoardGame, linRow, searchRow;
    private View searchView;
    private boolean atBottom = false;
    public Vibrator vb;
    private Point point;
    private int boardID, sizeOfCell, prevShipSize, margin, rowNum, columnNum;
    private Ship scout_One, scout_Two, cruiser, carrier, motherShip;
    private Ship ships[];
    private ArrayList<Point> occupiedCells;

    public GridBoard(Context context, int bID){
        super();
        this.context = context;
        boardID = bID;
        setVariables();
        setBoard();
        createShips();
        setDefaultShips();
    }

    /**
     * Sets up global variables & on Touch Listeners
     */
    private void setVariables(){
        vb =  (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        linBoardGame = (LinearLayout) ((Activity)context).findViewById(boardID);
        gridContainer = (RelativeLayout) ((Activity)context).findViewById(R.id.gridContainer);
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

        for (int row = 0; row < maxN; row++) {
            linRow = new LinearLayout(context);
            for (int col = 0; col < maxN; col++) {
                ivCell[row][col] = new ImageView(context);
                ivCell[row][col].setBackgroundResource(R.drawable.grid);
                linRow.addView(ivCell[row][col], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }
    }

    private void createShips(){
        //context,size, headLocation, bodyLocations
        point = new Point(9,0); //row, cell
        scout_One  = new Ship(1, point, maxN , "Scout One");
        point = new Point(9,1);
        scout_Two  = new Ship(1, point, maxN, "Scout Two");
        point = new Point(8,2);
        cruiser    = new Ship(2, point, maxN, "Cruiser");
        point = new Point(8,3);
        carrier    = new Ship(4, point, maxN, "Carrier");
        point = new Point(6,5);
        motherShip = new Ship(12, point, maxN, "MotherShip");
        ships = new Ship []{scout_One, scout_Two, cruiser, carrier, motherShip};
    }

    private void setDefaultShips(){
        occupiedCells = new ArrayList<>();
        int row, col;
        for(Ship s : ships) {
            for (int i = 0; i < s.getShipSize(); i++) {
                row = s.getBodyLocationPoints()[i].x;
                col = s.getBodyLocationPoints()[i].y;
                ivCell[row][col].setBackgroundResource(s.getBodyResources()[i]);
                updateOccupiedCells(s.getBodyLocationPoints());
            }
        }
    }

    private void updateOccupiedCells(Point pointsArray[]){
        //occupiedCells.clear();
        for(int x = 0; x < pointsArray.length; x++){
            occupiedCells.add(pointsArray[x]);
        }
    }

    /**
     * This method get's the screen size from the resources
     * @return the width in pixels
     */
    private float ScreenWidth() {
        RelativeLayout linBoardGame = (RelativeLayout) ((Activity)context).findViewById(R.id.gridContainer);
        Resources r = linBoardGame.getResources();
        DisplayMetrics d = r.getDisplayMetrics();
        return d.widthPixels;
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
            int x = Math.round(motionEvent.getX());
            int y = Math.round(motionEvent.getY());

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    /**
                    if(temp != null) {
                        temp.setBackgroundResource(R.drawable.grid);
                        if(temp2 != null){
                            temp2.setBackgroundResource(R.drawable.grid);
                        }
                    }
                    */
                    touchingView = findViewHelper(x, y);

                    if (touchingView != null) {
                        //touchingView.setBackgroundColor(white);
                        //setSurroundingViews(R.drawable.alien_onebytwo_top);
                        //touchingView.setBackgroundResource(R.drawable.alien_onebytwo_top);
                        //temp = touchingView;

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
//                    break;

                case MotionEvent.ACTION_UP:
                    //This is where we would place our figures!


                    break;
            }

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
        for (int row = 0; row < maxN; row++) {
            searchRow = (LinearLayout) linBoardGame.getChildAt(row); //Current row it is checking

            if (y > searchRow.getTop() && y < searchRow.getBottom()) {//If the Y coordinates are within the row Check which cell
                for (int col = 0; col < maxN; col++) {
                    searchView = searchRow.getChildAt(col); //Current View of the current searchRow
                    if (x > searchView.getLeft() && x < searchView.getRight()) {//If the x coordinates are within the view, View found!
                        rowNum = row;
                        columnNum = col;
                        if(searchView == ivCell[row][col]){ //View found
                            Point p = checkIfOccupied(row, col);
                            if(p != null){  //found ship part
                                findWhichShip(p);
                            }
                            // Test Hitting Function
                            boolean isOccupied = checkGridCell();
                            if(isOccupied){
                                score += 200;
                                Log.i("Score = ", "" + score);
                            }
                        }
                        return null;
                    }//if
                }//for search View
            }//if
        }//for searchRow
        return null;
    }

    private Point checkIfOccupied(int row, int col){
        for(int i = 0; i < occupiedCells.size(); i++){
            if(occupiedCells.get(i).x == row && occupiedCells.get(i).y == col){
                return occupiedCells.get(i);
            }
        }
        return null;
    }

    private void findWhichShip(Point p){
        int row, col;
        for(Ship s : ships) {
            for (int i = 0; i < s.getShipSize(); i++) {
                row = s.getBodyLocationPoints()[i].x;
                col = s.getBodyLocationPoints()[i].y;
                if(row == p.x && col == p.y){
                    Point head = s.getHeadCoordinatePoint();
                    Toast.makeText(context, "Touched: " + s.getShipName(), Toast.LENGTH_SHORT).show();
                    touchingView = ivCell[head.x][head.y];
                }
            }
        }
    }


    /**
     * This currently receives the id from onTouch to create the bottom view.
     * temp hard code.
     * @param id
     */
    private void setSurroundingViews(int id){
        if (id == R.drawable.alien_onebytwo_top) {
            searchRow = (LinearLayout)  linBoardGame.getChildAt(rowNum+1);
            if(searchRow != null) {
                searchView = searchRow.getChildAt(columnNum);
                searchView.setBackgroundResource(R.drawable.alien_onebytwo_bottom);
                temp2 = searchView;
                atBottom = false;
            }else{//This handles if it overflows.
                searchRow = (LinearLayout)  linBoardGame.getChildAt(rowNum);
                searchView = searchRow.getChildAt(columnNum);
                temp2 = searchView;
                temp2.setBackgroundResource(R.drawable.alien_onebytwo_bottom);
                searchRow = (LinearLayout)  linBoardGame.getChildAt(rowNum-1);
                searchView = searchRow.getChildAt(columnNum);
                touchingView = searchView;
                atBottom = true;
            }
        } else {
            Toast.makeText(context, "False", Toast.LENGTH_SHORT).show();
        }
    }


    public void hideGrid(){linBoardGame.setVisibility(View.GONE);}
    public void showGrid(){
        linBoardGame.setVisibility(View.VISIBLE);
    }


}
