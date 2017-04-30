package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
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
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Daniel on 4/8/2017.
 */

public class GridBoard extends Activity implements OnTouchListener {

    public Vibrator vb;
    private Point point;
    private Context context;
    final static int maxN = 8;
    private MotionStatus status;
    private boolean moved, player, hit, lockGrid;
    private Ship ships[], selectedShip;
    private RelativeLayout gridContainer;
    private ArrayList<Point> occupiedCells;
    private int boardID, sizeOfCell, margin, gridID;
    private enum MotionStatus{SETUP, DOWN, MOVE, UP}
    private LinearLayout linBoardGame, linRow, searchRow;
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    private TextView shipTV;
    private boolean AIisAttacking = false;
    private boolean isNewCell = false;
    Vector[][] aiSelection = new Vector[maxN][maxN]; // 2d Vector for A.I. to randomly choose hits on grid
    Vector<Vector> aiAttacks = new Vector(); // Stores aiSelection Vector to track previous hits


    public GridBoard(Context context, int bID, boolean player, int cellCount){
        super();
        this.context = context;
        boardID = bID;
        this.player = player; //False will be AI, True will be player
        //maxN = cellCount; //Makes it crash for some reason
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
        shipTV= (TextView) ((Activity)context).findViewById(R.id.textView);
        linBoardGame.setOnTouchListener(this);
        sizeOfCell = Math.round(ScreenWidth() / (maxN + (1)));
        occupiedCells = new ArrayList<>();
        moved = false;
        hit = false;
        gridID = R.drawable.grid;
        if(!player)lockGrid = true;
        else lockGrid = false;

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
        Ship scout_One, scout_Two, cruiser, carrier, motherShip;
        point = new Point(maxN-1,0); //row, cell
        scout_One  = new Ship(1, point, maxN , "Scout One", player);
        point = new Point(maxN-1,1);
        scout_Two  = new Ship(1, point, maxN, "Scout Two", player);
        point = new Point(maxN-2,2);
        cruiser    = new Ship(2, point, maxN, "Cruiser", player);
        point = new Point(maxN-2,3);
        carrier    = new Ship(4, point, maxN, "Carrier", player);
        point = new Point(maxN-4,5);
        motherShip = new Ship(12, point, maxN, "MotherShip", player);

        //Need an algorithm to set enemy ship locations at random without overlaps

        ships = new Ship []{scout_One, scout_Two, cruiser, carrier, motherShip};
    }

    /**
     * This class clears the grid of all ship images
     * Then it replaces them on the new location
     * To handle movements
     */
    private void setShips(){
        //This clears the board. To "Invalidate"
        for (int x = 0; x < maxN; x++) {
            for (int y = 0; y < maxN; y ++) {
                ivCell[x][y].setBackgroundResource(gridID);
            }
        }
        for(Ship s : ships) {
            for (int i = 0; i < s.getShipSize(); i++) {
                ivCell[s.getBodyLocationPoints()[i].x][s.getBodyLocationPoints()[i].y].setBackgroundResource(s.getBodyResources()[i]);
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
                    if(selectedShip != null) {
                        shipTV.setText(selectedShip.getShipName());
                    }
                    break;
               case MotionEvent.ACTION_MOVE:
                   status = MotionStatus.MOVE;
                   if(!lockGrid){
                       findViewHelper(touchX, touchY);
                       if(selectedShip != null) {
                           //TODO: Need to try to handle this so that it only fires if ship ACTUALLY moves.
                           //Not priority but will help us with resources and allow us to use vibrate and sounds
                           //And so that it doesnt do unneccesary loops and clears.
                           //vb.vibrate(10);
                           Log.i("Clearing and reset","!");
                           occupiedCells.clear();
                           setShips();
                       }
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
     * If a matching view is found, It calls the check if occupied method.
     * @param x
     * @param y
     * @return the found view
     */
    @Nullable
    private void findViewHelper(int x, int y) {
        View searchView;
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
     * This takes the row/col coordinates from findViewHelper
     * and compares with the occupiedCell ArrayList to if there is a ship part in that coordinate.
     * ON_DOWN: If it is occupied it calls the findWhichShip.
     * ON_MOVE: It moves the ship to that location.
     *          compares the new location to see if there is any ships in the way
     *          if there is it reverts back to the previous position.
     * After this method runs, it should traverse back into the ONTOUCH events.
     *
     * @param row
     * @param col
     * @return
     */
    private void checkIfOccupied(int row, int col){
        // A.I. Attacking Check Cells
        if(AIisAttacking) {
            Log.i("Attack A.I.", "Checking Cell");
            AIisAttacking = false;
        }
        if(status == MotionStatus.DOWN) {
            for (int i = 0; i < occupiedCells.size(); i++) {
                if (occupiedCells.get(i).x == row && occupiedCells.get(i).y == col) {
                    Log.i("OCCUPIED", ": TRUE");
                    Point p = new Point(row, col);
                    selectedShip = findWhichShip(p); //Touching View Updated
                    hit = true;
                }
            }
            if(selectedShip == null)Log.i("OCCUPIED", ": FALSE");


        }else if(status == MotionStatus.MOVE){//MotionStatus.MOVE
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
     *  A.I. Logic for Enemy
     *  Loop is continued to check if A.I. has selected the grid cell before
     *  if it has not then it performs the attack and adds it to its vector of
     *  previous attacks. 
      */

    private void enemyAttack() {
        // Loop until A.I. selects a cell it has not chosen before.
        int counter = 0;
        int myRow=0, myCol=0;
        boolean selectionFound = false;

        while (selectionFound == true || counter < aiAttacks.size()) {
            // Select random row and col
            Random newRow = new Random();
            myRow = newRow.nextInt(maxN);
            Random newCol = new Random();
            myCol = newCol.nextInt(maxN);
            String place = "" + myRow + myCol;
            aiSelection[myRow][myCol].add(place);

            while (counter < aiAttacks.size()) {
                // Check if grid has been selected before
                if (aiAttacks.get(counter).equals(aiSelection)) {
                    selectionFound = true;
                    counter = 0;
                    break;
                }
                counter++;
            }
        }
        aiAttacks.add(aiSelection[myRow][myCol]);
        if(AIisAttacking) {
            checkIfOccupied(myRow, myCol);
        }
    }
    /**
     * This is called from check if occupied on MOTION.DOWN
     * This returns the Ship object that was selected.
     * AND updates the touchingview to be headCoordinate of the ship.
     * This should never return null at this point. But we can handle it if it does.
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
                    //Point head = s.getHeadCoordinatePoint();
                    //touchingView = ivCell[head.x][head.y];
                    Log.i("VIEW FOUND: ","!!!!!!!!!!!!!!!!");
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
    public boolean getHit(){return hit;}
    public void setHit(){hit = false;}
    public void setLockGrid(boolean lock){lockGrid = lock;}
    public int getMarginSize(){return margin;}
    public Ship[] getShips(){ return ships;}
    public ArrayList<Point> getShipsPosition(){return occupiedCells;}


}
