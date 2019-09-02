/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;
import jhs.java.classes.Demon;
import jhs.java.classes.GSMAP;
import jhs.java.classes.Setting;
import jhs.java.mysql.JHSMySQL;
import jhs.java.mysql.JHSMySQLResult;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author IcyTeck
 */
public class ConfigManager {

    private AtomicLong transactionId = new AtomicLong();
    private String FirewallTemplate = "";
    private String EnableFirewall = "";
    private String DisableFirewall = "";
    private String configs = "";
    public static ConfigManager Instance = new ConfigManager();
    public HashMap<Integer, Setting> settings = new HashMap<>();
    public HashMap<String, Demon> demons = new HashMap<>();
    public ArrayList<GSMAP> maps = new ArrayList<>();
    private Boolean mapsLoaded = false;
    public String DBIP = "";
    public String DBNAME = "";
    public String DBUSER = "";
    public String DBPASS = "";
    public String DBPORT = "";
    public String DBSTRING = "";
    public String PWDBNAME = "";
    public String AMALYITICS_UPDATE_INTERVAL = "5";
    private Boolean settingsLoaded = false;
    public Integer keep_chatLogs = 0;
    public Boolean record_player_pvp = false;
    public Boolean record_player_sell = false;
    public Boolean record_analytics = false;
    public Boolean record_player_mail = false;
    public Boolean record_player_level = false;
    public String[] curencyFields = null;
    public Boolean canRunTrivia = false;
    public Boolean pvp_player_Hunting_function = false;
    public String[] pvp_player_Hunting_function_item;
    public int pvp_player_Hunting_function_minimum_Kills;
    public String pvp_player_Hunting_function_checkPoints = "";
    public String pvp_player_Hunting_function_getReward = "";
    public String[] pvp_player_Hunting_function_restrict_worldIds;
    public static Integer playerLog = 2;
    public Long GetNewTransactionId() {
        return transactionId.getAndIncrement();
    }

    public ConfigManager() {

    }

    public Boolean UpdateSetings(HashMap<Integer, Setting> xsettings) {
        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configsx = ConfigManager.Instance;
        mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.DBNAME);

        for (Setting demon : xsettings.values()) {
            mysql.Query("UPDATE settings SET value=? WHERE id=?", demon.value, demon.id + "");
        }
        settings = xsettings;
        canRunTrivia = GetSettings(false).get(7).value.equals("true");
        mysql.close();
        return true;
    }

    public HashMap<Integer, Setting> GetSettings(Boolean isReload) {
        if (!isReload && settingsLoaded) {
            return settings;
        }

        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configs = ConfigManager.Instance;
        mysql.connect(configs.DBSTRING, configs.DBUSER, configs.DBPASS, configs.DBNAME);
        JHSMySQLResult rst = mysql.Query("SELECT * FROM settings ORDER BY id DESC");
        while (rst.next()) {
            Setting setting = new Setting();
            setting.id = Integer.parseInt(rst.getString("id"));
            setting.name = rst.getString("name");
            setting.value = rst.getString("value");
            settings.put(setting.id, setting);
            settingsLoaded = true;
        }
        mysql.close();
        return settings;
    }

    public HashMap<String, Demon> GetFreshDemons() {
        for (Demon demon : demons.values()) {
            demon.pids = new ArrayList<>();
            demon.startCount = 0;
        }
        return demons;
    }

    public ArrayList<GSMAP> GetFreshMaps() {
        if (!mapsLoaded) {
            return new ArrayList<>();
        }

        for (int i = 0; i < maps.size(); i++) {
            maps.get(i).PIDID = "0";
        }

        return maps;
    }

    public void LoadMaps(String path) {
        if (mapsLoaded) {
            return;
        }

        try {
            FileReader fr = new FileReader(path);
            BufferedReader br = new BufferedReader(fr);
            String line2 = "";
            while ((line2 = br.readLine()) != null) {
                if (line2.contains("\t") && !line2.startsWith("/") && !line2.startsWith("#")) {
                    String[] split = line2.split("\t");
                    if (split.length > 2) {
                        GSMAP map = new GSMAP();
                        map.UID = split[0];
                        map.AltName = split[1];
                        map.proc = split[2];
                        map.name = split[3];
                        maps.add(map);
                    }
                }
            }
            br.close();
            mapsLoaded = true;
        } catch (Exception ex) {
            JHSLogingSystem.LogException(ConfigManager.class.getName(), ExceptionUtils.getStackTrace(ex), ex);
        }
    }

}
