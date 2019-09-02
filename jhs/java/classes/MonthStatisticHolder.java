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
public class MonthStatisticHolder {
    public HashMap<Integer, DayHolder> week_0 = new HashMap<>();
    public HashMap<Integer, DayHolder> week_1 = new HashMap<>();
    public HashMap<Integer, DayHolder> week_2 = new HashMap<>();
    public HashMap<Integer, DayHolder> week_3 = new HashMap<>();
    public HashMap<Integer, DayHolder> week_4 = new HashMap<>();
    public HashMap<Integer, DayHolder> week_5 = new HashMap<>();
    public HashMap<Integer, DayHolder> week_6 = new HashMap<>();
    public Integer month = 0;
}
