/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author IcyTeck
 */
public enum DayOfTheMonthSetting {
    STATISTICS_WEEK_0(0),
    STATISTICS_WEEK_1(1),
    STATISTICS_WEEK_2(2),
    STATISTICS_WEEK_3(3),
    STATISTICS_WEEK_4(4),
    STATISTICS_WEEK_5(5),
    STATISTICS_WEEK_6(6);

    private int value;
    private static Map map = new HashMap<>();

    private DayOfTheMonthSetting(int value) {
        this.value = value;
    }

    static {
        for (DayOfTheMonthSetting pageType : DayOfTheMonthSetting.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static DayOfTheMonthSetting valueOf(int pageType) {
        return (DayOfTheMonthSetting) map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
