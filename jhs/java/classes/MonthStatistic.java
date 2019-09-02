/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import Enums.DayOfTheMonthSetting;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import jhs.java.manager.ConfigManager;
import jhs.java.manager.JHSLogingSystem;
import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author IcyTeck
 */
public class MonthStatistic {

    private MonthStatisticHolder monthStatisticHolder = new MonthStatisticHolder();
    private final int data_id = 5;
    private Setting settings = null;

    public MonthStatistic() {
        settings = ConfigManager.Instance.GetSettings(false).get(data_id);
        if (settings.value == null || settings.value != null && settings.value.length() <= 0) {
            Create();
        } else {
            ObjectMapper mapper = new ObjectMapper();
            try {
                monthStatisticHolder = mapper.readValue(new String(Base64.decode(settings.value)), MonthStatisticHolder.class);
                JHSLogingSystem.LogInfo(MonthStatistic.class.getName(), "MonthStatistic: Loaded");
            } catch (IOException ex) {
                JHSLogingSystem.LogException(MonthStatistic.class.getName(), ExceptionUtils.getStackTrace(ex), ex);
                Create();
            }
        }
    }

    /**
     * *
     * RESET CURRENT WEEK
     */
    public void CheckLogic() {
        int current_week = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
        if (monthStatisticHolder.month != current_week) {
            Map<String, Integer> names = Calendar.getInstance().getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
            switch (current_week) {
                case 1:
                    for (Map.Entry<String, Integer> entry : names.entrySet()) {
                        DayHolder ds = new DayHolder();
                        for (int h = 0; h < 24; h++) {
                            ds.HouryMaxOnline.put(h, 0);
                        }
                        ds.DayId = entry.getValue();
                        ds.DayName = entry.getKey();
                        ds.WeakName = DayOfTheMonthSetting.valueOf(1).name();
                        monthStatisticHolder.week_1.put(ds.DayId, ds);
                    }
                    break;
                case 2:
                    for (Map.Entry<String, Integer> entry : names.entrySet()) {
                        DayHolder ds = new DayHolder();
                        for (int h = 0; h < 24; h++) {
                            ds.HouryMaxOnline.put(h, 0);
                        }
                        ds.DayId = entry.getValue();
                        ds.DayName = entry.getKey();
                        ds.WeakName = DayOfTheMonthSetting.valueOf(1).name();
                        monthStatisticHolder.week_2.put(ds.DayId, ds);
                    }
                    break;
                case 3:
                    for (Map.Entry<String, Integer> entry : names.entrySet()) {
                        DayHolder ds = new DayHolder();
                        for (int h = 0; h < 24; h++) {
                            ds.HouryMaxOnline.put(h, 0);
                        }
                        ds.DayId = entry.getValue();
                        ds.DayName = entry.getKey();
                        ds.WeakName = DayOfTheMonthSetting.valueOf(1).name();
                        monthStatisticHolder.week_3.put(ds.DayId, ds);
                    }
                    break;
                case 4:
                    for (Map.Entry<String, Integer> entry : names.entrySet()) {
                        DayHolder ds = new DayHolder();
                        for (int h = 0; h < 24; h++) {
                            ds.HouryMaxOnline.put(h, 0);
                        }
                        ds.DayId = entry.getValue();
                        ds.DayName = entry.getKey();
                        ds.WeakName = DayOfTheMonthSetting.valueOf(1).name();
                        monthStatisticHolder.week_4.put(ds.DayId, ds);
                    }
                    break;
            }
            monthStatisticHolder.month = current_week;
            Save();
        }
    }

    public Boolean Save() {
        settings = ConfigManager.Instance.GetSettings(false).get(data_id);
        if (settings != null) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                settings.value = Base64.encode(mapper.writeValueAsString(monthStatisticHolder).getBytes());//mapper.writeValueAsString(monthStatisticHolder);

                return settings.Save();
            } catch (IOException ex) {
                JHSLogingSystem.LogException(MonthStatistic.class.getName(), ex.toString(), ex);
            }
        }
        return false;
    }

    private void Create() {
        monthStatisticHolder = new MonthStatisticHolder();
        Map<String, Integer> names = Calendar.getInstance().getDisplayNames(Calendar.DAY_OF_WEEK, Calendar.SHORT, Locale.ENGLISH);
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            DayHolder ds = new DayHolder();
            for (int h = 0; h < 24; h++) {
                ds.HouryMaxOnline.put(h, 0);
            }
            ds.DayId = entry.getValue();
            ds.DayName = entry.getKey();
            ds.WeakName = DayOfTheMonthSetting.valueOf(0).name();
            monthStatisticHolder.week_0.put(ds.DayId, ds);
        }        
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            DayHolder ds = new DayHolder();
            for (int h = 0; h < 24; h++) {
                ds.HouryMaxOnline.put(h, 0);
            }
            ds.DayId = entry.getValue();
            ds.DayName = entry.getKey();
            ds.WeakName = DayOfTheMonthSetting.valueOf(1).name();
            monthStatisticHolder.week_1.put(ds.DayId, ds);
        }
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            DayHolder ds = new DayHolder();
            for (int h = 0; h < 24; h++) {
                ds.HouryMaxOnline.put(h, 0);
            }
            ds.DayId = entry.getValue();
            ds.DayName = entry.getKey();
            ds.WeakName = DayOfTheMonthSetting.valueOf(2).name();
            monthStatisticHolder.week_2.put(ds.DayId, ds);
        }
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            DayHolder ds = new DayHolder();
            for (int h = 0; h < 24; h++) {
                ds.HouryMaxOnline.put(h, 0);
            }
            ds.DayId = entry.getValue();
            ds.DayName = entry.getKey();
            ds.WeakName = DayOfTheMonthSetting.valueOf(3).name();
            monthStatisticHolder.week_3.put(ds.DayId, ds);
        }
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            DayHolder ds = new DayHolder();
            for (int h = 0; h < 24; h++) {
                ds.HouryMaxOnline.put(h, 0);
            }
            ds.DayId = entry.getValue();
            ds.DayName = entry.getKey();
            ds.WeakName = DayOfTheMonthSetting.valueOf(4).name();
            monthStatisticHolder.week_4.put(ds.DayId, ds);
        }
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            DayHolder ds = new DayHolder();
            for (int h = 0; h < 24; h++) {
                ds.HouryMaxOnline.put(h, 0);
            }
            ds.DayId = entry.getValue();
            ds.DayName = entry.getKey();
            ds.WeakName = DayOfTheMonthSetting.valueOf(5).name();
            monthStatisticHolder.week_5.put(ds.DayId, ds);
        }        
        for (Map.Entry<String, Integer> entry : names.entrySet()) {
            DayHolder ds = new DayHolder();
            for (int h = 0; h < 24; h++) {
                ds.HouryMaxOnline.put(h, 0);
            }
            ds.DayId = entry.getValue();
            ds.DayName = entry.getKey();
            ds.WeakName = DayOfTheMonthSetting.valueOf(6).name();
            monthStatisticHolder.week_6.put(ds.DayId, ds);
        }        
        
        
        
        
        
        
        JHSLogingSystem.LogInfo(MonthStatistic.class.getName(), "MonthStatistic:Create:" + Save());
    }

    public HashMap<Integer, DayHolder> Get() {
        CheckLogic();
        int current_week = Calendar.getInstance().get(Calendar.WEEK_OF_MONTH);
        switch (current_week) {
            case 0:
                return monthStatisticHolder.week_0;
            case 1:
                return monthStatisticHolder.week_1;
            case 2:
                return monthStatisticHolder.week_2;
            case 3:
                return monthStatisticHolder.week_3;
            case 4:
                return monthStatisticHolder.week_4;
            case 5:
                return monthStatisticHolder.week_5;
            case 6:
                return monthStatisticHolder.week_6;                
        }

        return null;
    }

    public DayHolder subtractDay(int day) {

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_WEEK, day);
        int current_week = cal.get(Calendar.WEEK_OF_MONTH);
        switch (current_week) {
            case 0:
                return monthStatisticHolder.week_0.get(cal.get(Calendar.DAY_OF_WEEK));            
            case 1:
                return monthStatisticHolder.week_1.get(cal.get(Calendar.DAY_OF_WEEK));
            case 2:
                return monthStatisticHolder.week_2.get(cal.get(Calendar.DAY_OF_WEEK));
            case 3:
                return monthStatisticHolder.week_3.get(cal.get(Calendar.DAY_OF_WEEK));
            case 4:
                return monthStatisticHolder.week_4.get(cal.get(Calendar.DAY_OF_WEEK));
            case 5:
                return monthStatisticHolder.week_5.get(cal.get(Calendar.DAY_OF_WEEK));
            case 6:
                return monthStatisticHolder.week_6.get(cal.get(Calendar.DAY_OF_WEEK));
                
        }
        return null;
    }

    public HashMap<Integer, DayHolder> subtractWeek(int week) {
        CheckLogic();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, week);
        int current_week = cal.get(Calendar.WEEK_OF_MONTH);
        switch (current_week) {
            case 0:
                return monthStatisticHolder.week_0;
            case 1:
                return monthStatisticHolder.week_1;
            case 2:
                return monthStatisticHolder.week_2;
            case 3:
                return monthStatisticHolder.week_3;
            case 4:
                return monthStatisticHolder.week_4;
            case 5:
                return monthStatisticHolder.week_5;
            case 6:
                return monthStatisticHolder.week_6;  
        }

        return null;
    }

    public DayHolder getDay() {
        CheckLogic();
        int current_day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);
        HashMap<Integer, DayHolder> online = Get();
        if (online == null) {
            return null;
        }
        return online.get(current_day);
    }

    public DayHolder getDayById(Integer subtractDay) {
        CheckLogic();
        HashMap<Integer, DayHolder> online = Get();
        if (online == null) {
            return null;
        }
        return online.get(subtractDay);
    }
}
