/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import java.util.HashMap;

/**
 *
 * @author IcyTeck
 */
public class DayHolder {

    public HashMap<Integer, Integer> HouryMaxOnline = new HashMap<>(24);
    public String DayName = "Monday";
    public String WeakName = "Monday";
    public Integer DayUniqueOnline = 0;
    public Integer DayTotalRegistredUsers = 0;
    public Integer DayId;
    public HashMap<String, Integer> uniqueUsers = new HashMap<>(); //PlayerId TimeSpentIngame Day Unique Users Cumulated Time

    public DayHolder() {

    }

    /*
    public void CalculateStatistic() {
        Integer[] foos = Hours.values().toArray(new Integer[Hours.values().size()]);
        int total = 0;
        int avg = 0;
        for (Integer foo : foos) {
            total += foo;
            avg = total / foos.length;
        }
        DayTotalRegistredUsers = avg;
    }
     */
    public void SetMaxOnline(Integer value) {
        DayUniqueOnline = value;
    }

}
