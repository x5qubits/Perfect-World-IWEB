package jhs.java.manager;

import com.goldhuman.Common.Conf;
import customProtocol.model.Quotes;
import events.PVPEvent;
import events.Trivia;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import jhs.java.classes.Demon;
import jhs.java.mysql.JHSMySQL;
import org.apache.axis.encoding.Base64;

public class IWeb implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        (new HelloThread()).start();
    }

    public class HelloThread extends Thread {

        @Override
        public void run() {
            ConfigManager cfgManager = ConfigManager.Instance;
            Conf conf = Conf.GetInstance("/etc/iweb.conf", null);
            String[] a = conf.find("DEMONS_CONFIG", "DemonsConfig").split(",");
            for (String a1 : a) {
                String[] split = a1.split("\t");
                String name = split[0].trim();
                String exeName = split[1].trim().toLowerCase();
                Demon demon = new Demon();
                demon.pids = new ArrayList<>();
                demon.name = name;
                demon.realName = exeName;
                demon.startCount = 0;
                demon.status = 0;
                cfgManager.demons.put(demon.realName.toLowerCase(), demon);
            }
            String pathToPvP = conf.find("OTHER_CONFIGS", "JHSAdmin_path") + "WEB-INF"+File.separator+"pvpQuotes.conf";
            String pathToQuotes = conf.find("OTHER_CONFIGS", "JHSAdmin_path")+ "WEB-INF"+File.separator+"botreplay.conf";
            ArrayList<String> allFile = new ArrayList<String>();
            try{
                try(BufferedReader br = new BufferedReader(new FileReader(pathToPvP))) {
                    for(String line; (line = br.readLine()) != null; ) {
                        // process the line.
                        if(!line.startsWith("//"))
                        {
                            allFile.add(line);
                        }
                    }
                    // line is not visible here.
                }
                Quotes.data = allFile.toArray(new String[0]);
            }catch(Exception f0)
            {
                JHSLogingSystem.LogInfo(IWeb.class.getName(), f0.toString());
            }
            allFile = new ArrayList<String>();  
            try{
                try(BufferedReader br = new BufferedReader(new FileReader(pathToQuotes))) {
                    for(String line; (line = br.readLine()) != null; ) {
                        // process the line.
                        if(!line.startsWith("//"))
                        {
                            allFile.add(line);
                        }
                    }
                    // line is not visible here.
                }
            Quotes.answerQuotes = allFile.toArray(new String[0]);
            }catch(Exception f1)
            {
                JHSLogingSystem.LogInfo(IWeb.class.getName(), f1.toString());
            }
            try{
                PVPEvent.eventDuration_keep = Integer.parseInt(conf.find("OTHER_CONFIGS", "pvp_event_duration"));
                PVPEvent.defaultMapId = Integer.parseInt(conf.find("OTHER_CONFIGS", "pvp_event_map_world_id"));
                String[] pvp_items_a = conf.find("OTHER_CONFIGS", "pvp_event_reward_1st").split(",");
                String[] pvp_items_a2 = conf.find("OTHER_CONFIGS", "pvp_event_reward_2nd").split(",");
                String[] pvp_items_a3 = conf.find("OTHER_CONFIGS", "pvp_event_reward_3rd").split(",");
                String[] pvp_event_reward_rest = conf.find("OTHER_CONFIGS", "pvp_event_reward_rest").split(",");
                String[][] pvp_items = new String[4][pvp_items_a.length];
                pvp_items[0] = pvp_items_a;
                pvp_items[1] = pvp_items_a2;
                pvp_items[2] = pvp_items_a3;
                pvp_items[3] = pvp_event_reward_rest;
                PVPEvent.pvpitems = pvp_items;
            }catch(Exception e)
            {
                JHSLogingSystem.LogInfo(IWeb.class.getName(), e.toString());
            }
            try{
            cfgManager.DBIP = conf.find("MYSQL_SERVER", "ip");
            cfgManager.DBNAME = conf.find("MYSQL_SERVER", "database");
            cfgManager.DBUSER = conf.find("MYSQL_SERVER", "user");
            cfgManager.DBPASS = conf.find("MYSQL_SERVER", "pass");
            cfgManager.DBPORT = conf.find("MYSQL_SERVER", "port");
            cfgManager.PWDBNAME = conf.find("MYSQL_SERVER", "pw_database");
            cfgManager.AMALYITICS_UPDATE_INTERVAL = conf.find("OTHER_CONFIGS", "update_anal_int");
            cfgManager.DBSTRING = cfgManager.DBIP + ":" + cfgManager.DBPORT;
            cfgManager.curencyFields = conf.find("MYSQL_SERVER", "curencyFields").split(",");
            cfgManager.keep_chatLogs = Integer.parseInt(conf.find("OTHER_CONFIGS", "keep_chatLogs"));
            cfgManager.record_player_pvp = conf.find("OTHER_CONFIGS", "record_player_pvp").toLowerCase().equals("true");
            cfgManager.record_player_level = conf.find("OTHER_CONFIGS", "record_player_level").toLowerCase().equals("true");
            cfgManager.record_player_sell = conf.find("OTHER_CONFIGS", "record_player_sell").toLowerCase().equals("true");
            cfgManager.record_analytics = conf.find("OTHER_CONFIGS", "record_analytics").toLowerCase().equals("true");
            cfgManager.record_player_mail = conf.find("OTHER_CONFIGS", "record_player_mail").toLowerCase().equals("true");
            cfgManager.canRunTrivia = cfgManager.GetSettings(false).get(7).value.equals("true");
            cfgManager.pvp_player_Hunting_function = conf.find("OTHER_CONFIGS", "pvp_player_Hunting_function").toLowerCase().equals("true");
            cfgManager.pvp_player_Hunting_function_minimum_Kills = Integer.parseInt(conf.find("OTHER_CONFIGS", "pvp_player_Hunting_function_minimum_Kills"));
            cfgManager.pvp_player_Hunting_function_item = conf.find("OTHER_CONFIGS", "pvp_player_Hunting_function_item").split(",");  
            Trivia.trivia_force_wordscramble = conf.find("OTHER_CONFIGS", "trivia_force_wordscramble").toLowerCase().equals("true");
            try{
                cfgManager.pvp_player_Hunting_function_checkPoints =  Base64.encode(conf.find("OTHER_CONFIGS", "pvp_player_Hunting_function_checkPoints").trim().toLowerCase().getBytes("UTF-16LE"));
                cfgManager.pvp_player_Hunting_function_getReward =  Base64.encode(conf.find("OTHER_CONFIGS", "pvp_player_Hunting_function_getReward").trim().toLowerCase().getBytes("UTF-16LE"));
            }catch(Exception s)
            {
                
            }
            ConfigManager.playerLog = Integer.parseInt(conf.find("OTHER_CONFIGS", "pvp_player_Hunting_function_Player_Log"));
            cfgManager.pvp_player_Hunting_function_restrict_worldIds = conf.find("OTHER_CONFIGS", "pvp_player_Hunting_function_restrict_worldIds").split(",");
            
            ServerLogManager.TRIVIA = cfgManager.GetSettings(false).get(7).value.equals("true");
            ServerLogManager.DQSYSTEM = cfgManager.GetSettings(false).get(14).value.equals("true");

            EventManager.Instance.RefreshEvents();
            ServerLogManager.Instance.Init();
            IwebManager.Instance.Init();
            StatisticManager.Instance.Init();
            ItemsManager.Instance.Init();
            Trivia.Init();
            }catch(Exception r)
            {
                
                 JHSLogingSystem.LogInfo(IWeb.class.getName(), r.toString());
            }
        }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        boolean a2 = false;
        try {
            a2 = StatisticManager.Instance.SaveStatistics();
        } catch (Exception ex) {
        }
        try {
            EventManager.Instance.ShutDown();
        } catch (Exception ex) {
        }
        try {
            JHSMySQL.getInstance().close();
        } catch (Exception ex) {
        }
        if (a2) {
            JHSLogingSystem.LogInfo(IWeb.class.getName(), "Saved all - Warning JHS IS DOWN auto events will not run! Bye!!!");
        } else {
            JHSLogingSystem.LogInfo(IWeb.class.getName(), "!Saved all - Warning JHS IS DOWN auto events will not run! Bye!!!");
        }

        try {
            System.exit(0);
        } catch (Exception ex) {
        }
    }
}
