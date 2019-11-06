package com.evolve.evx.EntityObjects;

/**
 * Created by MeadRoseAnn on 2/6/2018.
 */

public class PassengerQueueingHistory {
    public String time;
    public int count;

    //public String terminal;

    public PassengerQueueingHistory(String time,int count )//, String terminal)
    {
        this.time = time;
        this.count = count;
        //this.terminal = terminal;
    }
}
