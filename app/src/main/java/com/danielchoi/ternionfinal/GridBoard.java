package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Intent;
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

import java.util.ArrayList;

/**
 * Created by Daniel on 4/8/2017.
 */

public class GridBoard extends Activity implements OnTouchListener {

    public static final int activityRef = 2000;
    final static int maxN = 10;
    public int score = 0; // Player score adds 200 points for each hit.
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    public int white = Color.parseColor("#FFFFFF");
    private Context context;
    private RelativeLayout gridContainer;
    private LinearLayout linBoardGame, linRow, searchRow;
    private View searchView, touchingView;
    private boolean moved = false;
    public Vibrator vb;
    private Point point;
    private int boardID, sizeOfCell, prevShipSize, margin;
    private Ship scout_One, scout_Two, cruiser, carrier, motherShip, selectedShip;
    private Ship ships[];
    private ArrayList<Point> occupiedCells;
    private MotionStatus status;
    private enum MotionStatus{SETUP, DOWN, MOVE, UP};

    public GridBoard(Context context, int bID){
        super();
        this.context = context;
        boardID = bID;
        setVariables();
        setBoard();
        createShips();
        setShips();
        status = MotionStatus.SETUP;

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
        occupiedCells = new ArrayList<>();
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

    /**
     * This initializes all the Ship classes with a starting coordinate
     */
    private void createShips(){
        //context,size, headLocation, bodyLocations
        point = new Point(maxN-1,0); //row, cell
        scout_One  = new Ship(1, point, maxN , "Scout One");
        point = new Point(maxN-1,1);
        scout_Two  = new Ship(1, point, maxN, "Scout Two");
        point = new Point(maxN-2,2);
        cruiser    = new Ship(2, point, maxN, "Cruiser");
        point = new Point(maxN-2,3);
        carrier    = new Ship(4, point, maxN, "Carrier");
        point = new Point(maxN-4,5);
        motherShip = new Ship(12, point, maxN, "MotherShip");
        ships = new Ship []{scout_One, scout_Two, cruiser, carrier, motherShip};
    }

    /**
     * This class clears the grid of all ship images
     * Then it replaces them on the new location
     * To handle movements
     */
    private void setShips(){
        int row, col;
        //This clears the board. To "Invalidate"
        for (int x = 0; x < maxN; x++) {
            for (int y = 0; y < maxN; y ++) {
                ivCell[x][y].setBackgroundResource(R.drawable.grid);
            }
        }

        for(Ship s : ships) {
            for (int i = 0; i < s.getShipSize(); i++) {
                row = s.getBodyLocationPoints()[i].x;
                col = s.getBodyLocationPoints()[i].y;
                ivCell[row][col].setBackgroundResource(s.getBodyResources()[i]);
                updateOccupiedCells(s.getBodyLocationPoints());
            }
        }
    }

    /**
     * This updates the occupiedCells ArrayList of Points
     * To keep track of which Points are occupied
     * @param pointsArray
     */
    private void updateOccupiedCells(Point pointsArray[]){
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
     * This is handling setting the ships to their appropriate cells
     * For set up phase
     * It calls findViewHelper
     * @param view
     * @param motionEvent
     * @return true
     */
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        if(view == linBoardGame) {
            int touchX = Math.round(motionEvent.getX());
            int touchY = Math.round(motionEvent.getY());

            switch (motionEvent.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    status = MotionStatus.DOWN;
                    selectedShip = null;
                    findViewHelper(touchX, touchY);
                    break;
               case MotionEvent.ACTION_MOVE:
                   status = MotionStatus.MOVE;
                   findViewHelper(touchX, touchY);

                   if(selectedShip != null) {
                       //TODO: Need to try to handle this so that it only fires if ship moves.
                       //Not priority but will help us with resources and allow us to use vibrate and sounds
                       //vb.vibrate(10);
                       Log.i("Clearing and reset","!");
                       occupiedCells.clear();
                       setShips();
                   }
                    break;
                case MotionEvent.ACTION_UP:
                    status = MotionStatus.UP;
                    break;
            }

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
    private void findViewHelper(int x, int y) {
        for (int row = 0; row < maxN; row++) {
            searchRow = (LinearLayout) linBoardGame.getChildAt(row); //Current row it is checking

            if (y > searchRow.getTop() && y < searchRow.getBottom()) {//If the Y coordinates are within the row Check which cell
                for (int col = 0; col < maxN; col++) {
                    searchView = searchRow.getChildAt(col);    //Current View of the current searchRow
                    if (x > searchView.getLeft() && x < searchView.getRight()) {//If the x coordinates are within the view, View found!
                        if(searchView == ivCell[row][col]){ //View found
                            checkIfOccupied(row, col);
                        }//if
                      
                    }//if
                }//for search View
            }//if
        }//for searchRow
    }

    /**
     * This takes the row/col coordinates and compares with the occupiedCells
     * ArrayList to if there is a ship part in that coordinate.
     * This selects the ship ON_DOWN
     * ON_MOVE: update future coordinates if nothing is in the way
     *
     * @param row
     * @param col
     * @return
     */
    private void checkIfOccupied(int row, int col){
        if(status == MotionStatus.DOWN) {
            for (int i = 0; i < occupiedCells.size(); i++) {
                if (occupiedCells.get(i).x == row && occupiedCells.get(i).y == col) {
                    Log.i("OCCUPIED", ": TRUE");
                    Point p = new Point(row, col);
                    selectedShip = findWhichShip(p); //Touching View Updated
                }
            }
            Log.i("OCCUPIED", ": FALSE");

        }else{//MotionStatus.MOVE
             if(selectedShip != null){//Need to make sure none of the current ship parts will overlap another.
                 int rowHolder = selectedShip.getHeadCoordinatePoint().x;
                 int colHolder = selectedShip.getHeadCoordinatePoint().y;
                 int tempRow, tempCol;
                 selectedShip.moveShipTo(row, col);
                 for(Ship s : ships) {
                     if(s != selectedShip) {

                         for (int i = 0; i < selectedShip.getShipSize(); i++) {
                             tempRow = selectedShip.getBodyLocationPoints()[i].x;
                             tempCol = selectedShip.getBodyLocationPoints()[i].y;

                             for(int j = 0; j < s.getShipSize();j++){
                                if(tempRow == s.getBodyLocationPoints()[j].x && tempCol == s.getBodyLocationPoints()[j].y){
                                    selectedShip.moveShipTo(rowHolder, colHolder);
                                }
                             }//for
                         }//for
                     }
                 }//for
             }
        }
    }

    /**
     * This is called after we found out that ship part has been selected
     * This returns the Ship object that was selected.
     * AND updates the touchingview to be headCoordinate of the ship.
     * @param p
     * @return
     */
    private Ship findWhichShip(Point p){
        int row, col;
        for(Ship s : ships) {
            for (int i = 0; i < s.getShipSize(); i++) {
                row = s.getBodyLocationPoints()[i].x;
                col = s.getBodyLocationPoints()[i].y;
                if(row == p.x && col == p.y){
                    Point head = s.getHeadCoordinatePoint();
                    touchingView = ivCell[head.x][head.y];
                    return s;
                }
            }
        }
        return null;
    }

    public void hideGrid(){linBoardGame.setVisibility(View.GONE);}
    public void showGrid(){
        linBoardGame.setVisibility(View.VISIBLE);
    }


}
