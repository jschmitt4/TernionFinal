package com.danielchoi.ternionfinal;

import android.util.Log;

import java.util.Arrays;

/**
 * Created by cbfannin on 4/14/17.
 */

public class Ship {
    public String shipName;
    public int shipSize;
    public int[] shipLocation;

    public String getShipName() {
        return shipName;
    }

    public void setShipName(String shipName) {
        this.shipName = shipName;
    }

    public int getShipSize() {
        return shipSize;
    }

    public void setShipSize(int shipSize) {
        this.shipSize = shipSize;
    }

    public int[] getShipLocation() {
        return shipLocation;
    }

    public void setShipLocation(int[] shipLocation) {
        this.shipLocation = shipLocation;
    }

    @Override
    public String toString() {
        return
            "Ship{" +
            "shipName='" + shipName + '\'' +
            ", shipSize=" + shipSize +
            ", shipLocation=" + Arrays.toString(shipLocation) +
            '}';
    }
}
