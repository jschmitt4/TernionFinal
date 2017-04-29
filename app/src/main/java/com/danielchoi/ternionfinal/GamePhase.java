package com.danielchoi.ternionfinal;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;

import java.util.ArrayList;

/**
 * Created by Daniel on 4/29/2017.
 * Not sure if I want to use this class yet
 * This class wont have access to the grids... so probably not.
 * but maybe.
 */

public class GamePhase extends Activity {
    private Context context;
    private ArrayList<Point> shipLocations;
    private Ship[] ships;

    public GamePhase(Context context, ArrayList<Point> shipLocations, Ship[] ships){
        this.context = context;
        this.shipLocations = shipLocations;
        this.ships = ships;

    }




}
