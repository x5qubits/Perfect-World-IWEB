/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import java.util.HashMap;
import java.util.List;
import jhs.java.manager.ConfigManager;
import jhs.java.manager.JHSLogingSystem;
import jhs.java.mysql.JHSMySQL;
import jhs.java.mysql.JHSMySQLResult;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author IcyTeck
 */
public class Player {

    public Integer RoleId = 0;
    public Integer UserId = 0;
    public String Name = "-1";
    public Integer Level = 0;
    public Integer killed = 0;
    public Integer eventpoints = 0;
    public Integer cls = 0;    
    public Integer kills = 0;
    public Integer dqpoints_que = 0;
    public Integer dqpoints = 0;
    public Integer worldtag = -1;
    public Integer pvpEvent = 0;
    public Integer PVPPointsLog = 0;
    public Integer[] LastKilledPlayers = new Integer[2];
    public Integer PVPPoints = 0;
    public Integer IsOnline = 0;
    public long loginTime;
    public long PlayTime;
    
    public String getName() {
        try{
        return StringEscapeUtils.unescapeHtml(this.Name);
        }catch(Exception e)
        {
            return this.Name;
        }
    }
    
    public void Load() {
        LastKilledPlayers = new Integer[ConfigManager.playerLog]; 
        try {
            JHSMySQL mysql = JHSMySQL.getInstance();
            ConfigManager configsx = ConfigManager.Instance;
            mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.DBNAME);
            JHSMySQLResult rst = mysql.Query("SELECT * FROM players WHERE roleId=?", RoleId.toString());
            if (rst.next()) {
                killed = Integer.parseInt(rst.getString("killed"));
                eventpoints = Integer.parseInt(rst.getString("eventpoints"));
                kills = Integer.parseInt(rst.getString("kills"));
                dqpoints_que = Integer.parseInt(rst.getString("dqpointsque"));
                dqpoints = Integer.parseInt(rst.getString("dqpoints"));
                pvpEvent = Integer.parseInt(rst.getString("pvpEvent"));
                PVPPoints = Integer.parseInt(rst.getString("PVPPoints"));
                PVPPointsLog = Integer.parseInt(rst.getString("PVPPointsLog"));
                IsOnline = Integer.parseInt(rst.getString("IsOnline"));
                PlayTime = Long.parseLong(rst.getString("PlayTime"));
            }
            mysql.close();
        } catch (Exception e) {
            JHSLogingSystem.LogException(Player.class.getName() + ".Load", "Unable to load player RoleId:"+RoleId+" Reson:"+e.toString(), e);
        }
    }

    public Boolean Save() {
        boolean saved = false;
        try {
            JHSMySQL mysql = JHSMySQL.getInstance();
            ConfigManager configsx = ConfigManager.Instance;
            mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.DBNAME);
            String query = "INSERT INTO `players` (`roleid`, `userid`, `name`, `level`, `eventpoints`, `cls`, `kills`, `killed`, `dqpointsque`, `dqpoints`, `pvpEvent`, `PVPPoints`, `PVPPointsLog`, `PlayTime`, `IsOnline`) VALUES"
                    + "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)"
                    + " ON DUPLICATE KEY UPDATE name=?, level=?, kills=?, killed=?, dqpointsque=?, dqpoints=?, eventpoints=?, cls=?, pvpEvent=?, PVPPoints=?, PVPPointsLog=?, PlayTime=?, IsOnline=?;";
            saved = mysql.Query(query, RoleId.toString(), UserId.toString(), Name, Level.toString(), eventpoints.toString(), cls.toString(), kills.toString(), killed.toString(), dqpoints_que.toString(), dqpoints.toString(), pvpEvent.toString(), PVPPoints.toString(), PVPPointsLog.toString(), PlayTime+"", IsOnline.toString(), Name, Level.toString(), kills.toString(), killed.toString(), dqpoints_que.toString(), dqpoints.toString(), eventpoints.toString(), cls.toString(), pvpEvent.toString(), PVPPoints.toString(), PVPPointsLog.toString(), PlayTime+"", IsOnline.toString()).SUCCESS;
            mysql.close();
        } catch (Exception e) {
            JHSLogingSystem.LogException(Player.class.getName() + ".Save", ExceptionUtils.getStackTrace(e), e);
        }
        return saved;
    }
}
