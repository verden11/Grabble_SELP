package io.github.verden11.grabble.Helper;


import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by verden on 06/11/16.
 */

public class TodaysKML {


    private ArrayList<Integer> points;
    private ArrayList<Character> letters;
    private ArrayList<LatLng> location;


    public TodaysKML(ArrayList<Integer> mPoints, ArrayList<Character> mLetters, ArrayList<LatLng> mLocation) {
        points = mPoints;
        letters = mLetters;
        location = mLocation;
    }

    public ArrayList<Integer> getPoints() {
        return points;
    }

    public ArrayList<LatLng> getLocation() {
        return location;
    }

    public ArrayList<Character> getLetters() {

        return letters;
    }

    public void setLocation(ArrayList<LatLng> location) {
        this.location = location;
    }

    public void setLetters(ArrayList<Character> letters) {

        this.letters = letters;
    }

    public void setPoints(ArrayList<Integer> points) {

        this.points = points;
    }
}
