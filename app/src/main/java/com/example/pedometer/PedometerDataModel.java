package com.example.pedometer;

import io.realm.RealmObject;

public class PedometerDataModel extends RealmObject {
    private int date;
    private int steps;

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getSteps() {
        return steps;
    }

    public void setSteps(int steps) {
        this.steps = steps;
    }

    public void addSteps(int steps) {
        this.steps += steps;
    }
}
