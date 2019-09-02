/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import events.Trivia;
import it.sauronsoftware.cron4j.Scheduler;
import java.util.Calendar;
import java.util.Collections;
import jhs.java.classes.DayHolder;
import jhs.java.classes.MonthStatistic;
import jhs.java.classes.StatisticOutput;
import jhs.java.mysql.JHSMySQL;
import jhs.java.mysql.JHSMySQLResult;
import org.apache.commons.lang.exception.ExceptionUtils;
import protocol.DeliveryDB;

/**
 *
 * @author IcyTeck
 */
public class StatisticManager {

    public final long ONE_MINUTE_MILISECONDS = 60000;
    public final long TOW_MINUTE_MILISECONDS = ONE_MINUTE_MILISECONDS * 2;
    public final long TREE_MINUTE_MILISECONDS = ONE_MINUTE_MILISECONDS * 3;
    public final long FIVE_MINUTE_MILISECONDS = ONE_MINUTE_MILISECONDS * 5;
    public final long THEN_MINUTE_MILISECONDS = ONE_MINUTE_MILISECONDS * 10;
    public final long ONE_HOUR_MILISECONDS = ONE_MINUTE_MILISECONDS * 60;
    public static final StatisticManager Instance = new StatisticManager();
    private Scheduler scheduler = null;
    private MonthStatistic online = null;
    private ConfigManager configs = null;

    public void Init() {
        configs = ConfigManager.Instance;
        if (!configs.record_analytics) {
            return;
        }
        online = new MonthStatistic();
        scheduler = new Scheduler();
        Runnable runable = new Runnable() {
            @Override
            public void run() {
                StatisticManager.Instance.UpdateStatistics2();
            }
        };
        scheduler.schedule("*/" + ConfigManager.Instance.AMALYITICS_UPDATE_INTERVAL + " * * * *", runable);
        scheduler.start();
        UpdateStatistics2();
    }

    public Integer UserCount() {
        if (!configs.record_analytics) {
            return 0;
        }
        int count = 0;
        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configsx = ConfigManager.Instance;
        mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.PWDBNAME);
        JHSMySQLResult rst = mysql.Query("SELECT COUNT(ID) as cCount FROM users");
        if (rst.next()) {
            count = Integer.parseInt(rst.getString("cCount"));
        }
        mysql.close();
        return count;
    }

    public StatisticOutput getStatistics(int Id) {
        StatisticOutput stO = new StatisticOutput();
        try{
        //ArrayList<String> RushHours = new ArrayList<>();
        switch (Id) {
            case 100:
                //GET ALL WEAK
                int unique_last_users = 0;
                int unique_this_users = 0;
                int total_this_users = 0;
                int total_last_users = 0;
                int total_this_players = 0;
                int total_last_players = 0;

                for (DayHolder day : online.Get().values()) {
                    int maxDay = Collections.max(day.HouryMaxOnline.values());
                    unique_this_users += day.uniqueUsers.size();
                    if (day.DayTotalRegistredUsers > total_this_users) {
                        total_this_users = day.DayTotalRegistredUsers;
                    }
                    total_this_players += maxDay;
                    stO.THIS_WEEK_REGISTERED.add(day.DayTotalRegistredUsers);
                    stO.THIS_WEEK_ONLINE_ARRAY.add(maxDay);
                }
                for (DayHolder day : online.subtractWeek(-1).values()) {
                    int maxDay = Collections.max(day.HouryMaxOnline.values());
                    unique_last_users += day.uniqueUsers.size();
                    if (day.DayTotalRegistredUsers > total_last_users) {
                        total_last_users = day.DayTotalRegistredUsers;
                    }
                    total_last_players += maxDay;
                    stO.LASTWEEK_REGISTERED.add(day.DayTotalRegistredUsers);
                    stO.LAST_WEEK_ONLINE_ARRAY.add(maxDay);
                }

                int newUsers = unique_this_users - unique_last_users;
                if (newUsers < 0) {
                    newUsers = 0;
                }
                int newOnline = total_this_users - total_last_users; //10-200 = 
                if (newOnline < 0) {
                    newOnline = 0;
                }
                int diffOnline = total_this_players - total_last_players; //10-200 = 
                if (diffOnline < 0) {
                    diffOnline = 0;
                }
                stO.NEW_LOGGEDIN_UNIQUE_ACCOUNTS = newUsers;
                stO.NEW_REGISTERED_ACCOUNTS_THIS_WEEK = newOnline;
                stO.Total_Registred_Accounts_ThisWeek = total_this_users;
                stO.Total_Registred_Accounts_LastWeek = total_last_users;
                stO.total_this_players = total_this_players;
                stO.total_last_players = total_last_players;
                stO.diffOnline = diffOnline;
                stO.Total_Registred_Accounts_LastWeek = total_last_users;

                stO.PPDAY = online.subtractDay(-2).HouryMaxOnline.values();
                stO.PDAY = online.subtractDay(-1).HouryMaxOnline.values();
                stO.TDAY_TIME = online.getDay().HouryMaxOnline.values();
                stO.DayTotalRegistredUsers = online.getDay().DayTotalRegistredUsers;
                stO.YerstudayDayTotalRegistredUsers = online.subtractDay(-1).DayTotalRegistredUsers;
                diffOnline = stO.DayTotalRegistredUsers - stO.YerstudayDayTotalRegistredUsers;
                if (diffOnline < 0) {
                    diffOnline = 0;
                }
                stO.YerstudayDayTotalRegistredDiff = diffOnline;
                stO.U1 = online.getDay().uniqueUsers.size();
                int curOn = 0;
                try {
                    Integer[] user = new Integer[3];
                    if (DeliveryDB.GetMaxOnlineNum(user)) {
                        curOn = user[2];
                    }
                } catch (Exception e) {
                }

                diffOnline = stO.U1 - online.subtractDay(-1).uniqueUsers.size();
                if (diffOnline < 0) {
                    diffOnline = 0;
                }

                stO.U2 = diffOnline;

                diffOnline = stO.U1 - online.subtractDay(-2).uniqueUsers.size();
                if (diffOnline < 0) {
                    diffOnline = 0;
                }

                stO.U3 = diffOnline;
                stO.U4 = curOn;
                break;
            //GET ALL MONTH
            //current_day
            case 101:
                break;
            //GET DAY
            default:
                break;
        }
        }catch(Exception e)
        {
             JHSLogingSystem.LogInfo(StatisticManager.class.getName(), e.toString());
        }
        return stO;
    }

    public boolean SaveStatistics() {
        if (!configs.record_analytics) {
            return true;
        }
        online.Save();
        scheduler.stop();
        return true;
    }

    public void UpdateStatistics2() {
        if (!configs.record_analytics) {
            return;
        }
        if (Trivia.firstStart) {
            Trivia.firstStart = false;
        }
        try {
            int current_hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
            DayHolder todayStatistic = online.getDay();
            if (todayStatistic == null) {
                return;
            }
            int TotalUsers = UserCount();
            todayStatistic.DayTotalRegistredUsers = TotalUsers;
            Integer[] user = new Integer[3];
            if (DeliveryDB.GetMaxOnlineNum(user)) {
                int curOn = user[2];
                int current_value = todayStatistic.HouryMaxOnline.get(current_hour);
                if (current_value < curOn) {
                    todayStatistic.SetMaxOnline(curOn); //= user[2];   
                    todayStatistic.HouryMaxOnline.put(current_hour, curOn);
                    online.Save();
                    JHSLogingSystem.LogInfo(StatisticManager.class.getName(), "TimeSpan Taken: UsersOnline[" + curOn + "]");
                } else {
                    todayStatistic.HouryMaxOnline.put(current_hour, current_value);
                    online.Save();
                    JHSLogingSystem.LogInfo(StatisticManager.class.getName(), "TimeSpan Taken: Status[Less Then Before] HOUR["+current_hour+"] TimeSpan Online[" + current_value + "] - Game Users Online[" + curOn + "]");
                }
            } else {
                JHSLogingSystem.LogInfo(StatisticManager.class.getName(), "Server Offline");
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(StatisticManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }

    public void UpdateStatistic(String PlayerId, Integer timespent, Boolean Login) {
        if (!configs.record_analytics) {
            return;
        }
        try {
            DayHolder todayStatistic = online.getDay();
            if (todayStatistic == null) {
                return;
            }
            if (!Login) {
                //Daily Most Active Player
                if (todayStatistic.uniqueUsers.containsKey(PlayerId)) {
                    int time = timespent + todayStatistic.uniqueUsers.get(PlayerId);
                    todayStatistic.uniqueUsers.put(PlayerId, time);
                } else {
                    todayStatistic.uniqueUsers.put(PlayerId, timespent);
                }
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(StatisticManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }
}
