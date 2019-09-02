/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import Enums.EventType;
import it.sauronsoftware.cron4j.Scheduler;
import jhs.java.manager.ConfigManager;
import jhs.java.manager.JHSLogingSystem;
import jhs.java.manager.EventManager;
import jhs.java.mysql.JHSMySQL;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author IcyTeck
 */
public class Event {

    public String ID = "0";
    public String Name = "New Event";
    public String Time = "*/1 * * * *";
    public String Parms = "";
    public Boolean Enabled = false;
    public String Value = "";
    public EventType Type = EventType.RUN_BROADCAST;
    private Scheduler scheduler = null;

    public Event() {
    }

    public void Init() {
        scheduler = new Scheduler();
    }

    public void Start() {
        try {
            Runnable runable = null;
            final Event xvalue = this;
            switch (Type) {
                case RUN_SCRIPT:
                    runable = new Runnable() {
                        @Override
                        public void run() {
                            EventManager.Instance.RunScript(xvalue);
                        }
                    };
                    break;
                case RUN_BROADCAST:
                    runable = new Runnable() {
                        @Override
                        public void run() {
                            EventManager.Instance.RunBroadcast(xvalue);
                        }
                    };
                    break;
                case RUN_INGAME_EVENT:
                    runable = new Runnable() {
                        @Override
                        public void run() {
                            EventManager.Instance.MonsterCreator(xvalue);
                        }
                    };
                    break;
            }
            scheduler.schedule(this.Time, runable);
            scheduler.start();
            JHSLogingSystem.LogInfo(Event.class.getName(), "Run Registered Succesfuly["+ID+"] - {"+Name+"} type[{"+Type.toString()+"}]");
        } catch (Exception e) {
            JHSLogingSystem.LogException(Event.class.getName(), "Faill to register Event["+ID+"] - {"+Name+"} type["+Type.toString()+"] Exception:{"+ExceptionUtils.getStackTrace(e)+"}", e);
        }
    }

    public void Stop() {
        try {
            scheduler.stop();
        } catch (Exception e) {
        }
    }

    public String toFrendlyNameType() {
        switch (Type) {
            case RUN_SCRIPT:
                return "Server Interaction";
            case RUN_BROADCAST:
                return "Broadcast";
            case RUN_INGAME_EVENT:
                return "In-Game";
            default:
                return "Unknown";
        }
    }

    public void Save() {
        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configsx = ConfigManager.Instance;
        mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.DBNAME);
        String query = "INSERT INTO `events` (`id`, `name`, `time`, `type`, `parms`, `enabled`, `execute`) VALUES"
                + "(?, ?, ?, ?, ?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE name=?, Type=?, time=?, parms=?, enabled=?, execute=?;";
        mysql.Query(query, ID, Name, Time, Type.getValue() + "", Parms, (Enabled ? "1" : "0"), Value, Name, Type.getValue() + "", Time, Parms, (Enabled ? "1" : "0"), Value);
        mysql.close();
    }

    public Message RunNow() {

        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "Can't run this event.";
        switch (Type) {
            case RUN_SCRIPT:
                msg.success = EventManager.Instance.RunScript(this);
                break;
            case RUN_BROADCAST:
                msg.success = EventManager.Instance.RunBroadcast(this);
                break;
            case RUN_INGAME_EVENT:
                msg.success = EventManager.Instance.MonsterCreator(this);
                break;
        }
        if (msg.success) {
            msg.message = "Event runned successfuly.";
        }
        return msg;

    }
}
