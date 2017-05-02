package com.danielchoi.ternionfinal;

import android.graphics.Point;

/**
 * Created by charles and daniel on 4/14/17.
 */

public class Ship {
    private int     shipSize, width, height, maxGridSize;
    private Point   headCoordinatePoint;
    private Point[] bodyLocationPoints;
    private int[]   bodyResources;
    private String  shipName;
    public boolean  rotated = false;
    public boolean  player; //true is player; false is enemy


    public Ship(int shipSize, Point p, int maxGridSize, String shipName, boolean player){
        this.setShipSize(shipSize);
        this.headCoordinatePoint = p;
        this.maxGridSize = maxGridSize;
        this.shipName = shipName;
        this.player = player;
        bodyLocationPoints = new Point[shipSize];
        setBodyResources();
        setBodyLocationPoints();
    }

    private void setBodyResources(){
        switch(shipSize){
            case 1:
                if(player)
                    bodyResources = new int[] {
                            R.drawable.moon_onebyone};
                else if(!player)
                bodyResources = new int[] {
                        R.drawable.alien_onebyone};

                width   = 1;
                height  = 1;
                break;
            case 2:
                if(player)
                    bodyResources = new int[] {
                            R.drawable.moon_onebytwo_top,
                            R.drawable.moon_onebytwo_bottom};

                else if(!player)
                bodyResources = new int[] {
                        R.drawable.alien_onebytwo_top,
                        R.drawable.alien_onebytwo_bottom};
                width   = 1;
                height  = 2;
                break;
            case 4:
                if(player)
                    bodyResources = new int[] {
                            R.drawable.moon_twobytwo_topleft, R.drawable.moon_twobytwo_topright,
                            R.drawable.moon_twobytwo_bottom_left, R.drawable.moon_twobytwo_bottom_right};

                else if(!player)
                bodyResources = new int[] {
                        R.drawable.alien_twobytwo_topleft, R.drawable.alien_twobytwo_topright,
                        R.drawable.alien_twobytwo_bottomleft, R.drawable.alien_twobytwo_bottomright};
                width   = 2;
                height  = 2;
                break;
            case 12:
                if(player)
                    bodyResources = new int[]{
                            R.drawable.moon_threebyfour_one,    R.drawable.moon_threebyfour_two,      R.drawable.moon_threebyfour_three,
                            R.drawable.moon_threebyfour_four,  R.drawable.moon_threebyfour_five,      R.drawable.moon_threebyfour_six,
                            R.drawable.moon_threebyfour_seven, R.drawable.moon_threebyfour_eight,     R.drawable.moon_threebyfour_nine,
                            R.drawable.moon_threebyfour_ten,   R.drawable.moon_threebyfour_twelve,    R.drawable.moon_threebyfour_eleven};

                else if(!player)
                bodyResources = new int[]{
                        R.drawable.alien_threebyfour_one,   R.drawable.alien_threebyfour_two,       R.drawable.alien_threebyfour_three,
                        R.drawable.alien_threebyfour_four,  R.drawable.alien_threebyfour_five,      R.drawable.alien_threebyfour_six,
                        R.drawable.alien_threebyfour_seven, R.drawable.alien_threebyfour_eight,     R.drawable.alien_threebyfour_nine,
                        R.drawable.alien_threebyfour_ten,   R.drawable.alien_threebyfour_eleven,    R.drawable.alien_threebyfour_twelve};
                width   = 3;
                height  = 4;
                break;
        }

    }

    /**
     * This set the rest of the ships body based in the location of the head
     */
    private void setBodyLocationPoints(){
        int i = 0;
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                bodyLocationPoints[i] = new Point(getHeadCoordinatePoint().x + row, getHeadCoordinatePoint().y + col);
                i++;
            }
        }
    }

    public void moveShipTo(int row, int col){
        if(!rotated){
            while(row > (maxGridSize-height)){row--;} // Handles border boundaries
            while(col > (maxGridSize-width)){col--;}
        }
        headCoordinatePoint.set(row, col);
        setBodyLocationPoints();
    }

    public int getShipSize() {
        return shipSize;
    }

    public void setShipSize(int shipSize) {
        this.shipSize = shipSize;
    }

    public int[] getBodyResources() {
        return bodyResources;
    }

    public Point[] getBodyLocationPoints() {
        return bodyLocationPoints;
    }

    public Point getHeadCoordinatePoint() {
        return headCoordinatePoint;
    }

    public void setHeadCoordinatePoint(Point headCoordinatePoint) {this.headCoordinatePoint = headCoordinatePoint;}

    public String getShipName(){return shipName;}
}
