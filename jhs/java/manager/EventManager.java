/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import Enums.EventType;
import Enums.GameEventsId;
import it.sauronsoftware.cron4j.SchedulingPattern;
import java.text.ParseException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import jhs.java.classes.Event;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import jhs.java.classes.Message;
import jhs.java.mysql.JHSMySQL;
import jhs.java.mysql.JHSMySQLResult;
import net.redhogs.cronparser.CronExpressionDescriptor;
import net.redhogs.cronparser.Options;
import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author IcyTeck
 */
public class EventManager {

    public static final EventManager Instance = new EventManager();
    public HashMap<String, Event> events = new HashMap<>();
    private static final ExecutorService executor = Executors.newCachedThreadPool();

    public EventManager() {

    }

    public void ShutDown() {
        for (Event e : events.values()) {
            e.Stop();
        }
        events.clear();
    }

    public void RefreshEvents() {
        try {
            JHSLogingSystem.LogInfo(DataBaseManager.class.getName(), "Reloading events started.");
            for (Event e : events.values()) {
                e.Stop();
            }
            events.clear();
            JHSMySQL mysql = JHSMySQL.getInstance();
            ConfigManager configs = ConfigManager.Instance;
            mysql.connect(configs.DBSTRING, configs.DBUSER, configs.DBPASS, configs.DBNAME);
            JHSMySQLResult rst = mysql.Query("SELECT * FROM events ORDER BY id DESC");
            while (rst.next()) {
                try {
                    Event event = new Event();
                    event.ID = rst.getString("id");
                    event.Name = rst.getString("name");
                    event.Time = rst.getString("time");
                    event.Parms = rst.getString("parms");
                    event.Value = rst.getString("execute");
                    event.Enabled = Integer.parseInt(rst.getString("enabled")) == 1;
                    event.Type = EventType.valueOf(Integer.parseInt(rst.getString("type")));
                    if (event.Enabled) {
                        event.Init();
                        event.Start();
                    }
                    events.put(event.ID, event);
                } catch (Exception e) {
                    JHSLogingSystem.LogException(EventManager.class.getName(), "Unable to Create Event["+rst.getString("id")+"] - Name["+rst.getString("name")+"] Type["+rst.getString("type")+"] Error:"+ExceptionUtils.getStackTrace(e)+"", e);
                }
            }
            mysql.close();
            JHSLogingSystem.LogInfo(EventManager.class.getName(), "Reloading events Ended.");
        } catch (Exception e) {
            JHSLogingSystem.LogException(EventManager.class.getName(), null, e);
        }
    }

    public Boolean RunScript(Event event) {
        try {
            boolean success = false;
            final String value = event.Value;
            executor.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        Process proc = Runtime.getRuntime().exec(value);
                        proc.waitFor();
                    } catch (Exception e) {
                        JHSLogingSystem.LogException(EventManager.class.getName(), e.toString(), e);
                    }
                }
            });
            JHSLogingSystem.LogInfo(EventManager.class.getName(), "Run Event["+event.ID+"] - Name["+event.Name+"] Type["+event.Type.toString()+"] Sucess["+success+"]");
            return true;
        } catch (Exception e) {
            JHSLogingSystem.LogException(EventManager.class.getName(), "Unable to run Event["+event.ID+"] - Name["+event.Name+"] Parms["+event.Parms.toString()+"] Error:"+ExceptionUtils.getStackTrace(e)+"", e);
            return false;
        }
    }

    public Boolean RunBroadcast(Event event) {
        try {
            String[] full = event.Parms.split(Pattern.quote("|"));
            String[] fristparms = full[0].split(Pattern.quote("_"));
            String[] secondparms = full[1].split(Pattern.quote("_"));
            byte type = (byte) Integer.parseInt(fristparms[0]);
            boolean success = IwebManager.Instance.SendChat(event.Value, type, 0);
            JHSLogingSystem.LogInfo(EventManager.class.getName(), "Run Event["+event.ID+"] - Name["+event.Name+"] Type["+event.Type.toString()+"] Sucess["+success+"]");
            return true;
        } catch (Exception e) {
            JHSLogingSystem.LogException(EventManager.class.getName(), "Unable to run Event["+event.ID+"] - Name["+event.Name+"] Parms["+event.Parms.toString()+"] Error:"+ExceptionUtils.getStackTrace(e)+"", e);
            return false;
        }
    }

    public Boolean MonsterCreator(Event event) {
        try {
            String[] full = event.Parms.split(Pattern.quote("|"));
            String[] fristparms = full[0].split(Pattern.quote("_"));
            String[] secondparms = full[1].split(Pattern.quote("_"));
            Integer ActivateMonsterID = Integer.parseInt(secondparms[0]);
            Integer ActivateMonsterWorld = Integer.parseInt(secondparms[1]);
            Integer ExpValue = Integer.parseInt(secondparms[2]);
            Integer MaxOnline = Integer.parseInt(secondparms[3]);
            byte BroadcastId = Byte.parseByte(secondparms[4]);
            String Lambda = secondparms[5];
            for (String action : fristparms) {
                switch (action) {
                    case "50":
                        IwebManager.Instance.SendChat(event.Value, BroadcastId, 0);
                        break;
                    case "1":
                        IwebManager.Instance.ActivateMonster(ActivateMonsterWorld, ActivateMonsterID);
                        break;
                    case "2":
                        IwebManager.Instance.DisableMonster(ActivateMonsterWorld, ActivateMonsterID);
                        break;
                    case "3":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.DoubleMoney.ordinal(), "true");
                        break;
                    case "4":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.DoubleObject.ordinal(), "true");
                        break;
                    case "5":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.DoubleSP.ordinal(), "true");
                        break;
                    case "6":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoTrade.ordinal(), "true");
                        break;
                    case "7":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoSellPoint.ordinal(), "true");
                        break;
                    case "8":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoMail.ordinal(), "true");
                        break;
                    case "9":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoAuction.ordinal(), "true");
                        break;
                    case "10":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoFaction.ordinal(), "true");
                        break;
                    case "11":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.DoubleMoney.ordinal(), "false");
                        break;
                    case "12":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.DoubleObject.ordinal(), "false");
                        break;
                    case "13":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.DoubleSP.ordinal(), "false");
                        break;
                    case "14":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoTrade.ordinal(), "false");
                        break;
                    case "15":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoSellPoint.ordinal(), "false");
                        break;
                    case "16":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoMail.ordinal(), "false");
                        break;
                    case "17":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoAuction.ordinal(), "false");
                        break;
                    case "18":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.NoFaction.ordinal(), "false");
                        break;
                    case "19":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.Lambda.ordinal(), Lambda);
                        break;
                    case "20":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.MaxOnline.ordinal(), MaxOnline.toString());
                        break;
                    case "21":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.DoubleExp.ordinal(), ExpValue.toString());
                        break;
                     case "51":
                        IwebManager.Instance.ActivateCloseEvents(GameEventsId.ActivatePVPEvent.ordinal(), ExpValue.toString());
                        break;
                }
            }
            boolean success = false;
            JHSLogingSystem.LogInfo(EventManager.class.getName(), "Run Event["+event.ID+"] - Name["+event.Name+"] Type["+event.Type.toString()+"] Sucess["+success+"]");
            return true;
        } catch (Exception e) {
            JHSLogingSystem.LogException(EventManager.class.getName(), "Unable to run Event["+event.ID+"] - Name["+event.Name+"] Parms["+event.Parms.toString()+"] Error:"+ExceptionUtils.getStackTrace(e)+"", e);
            return false;
        }
    }

    public String GetEvents() {
        String retur = "";

        for (Event event : events.values()) {
            try {
                retur += "<tr>"
                        + "<td class=\"v-a-m\">"
                        + "<div class=\"media media-auto\">"
                        + "<div class=\"media-left\">"
                        + "	<i class=\"fa fa-fw fa-calendar-check-o fa-3x\"></i>"
                        + "</div>"
                        + "<div class=\"media-body\">"
                        + "<span class=\"media-heading text-white\">" + event.Name + "</span>"
                        + "<br>"
                        + "<span class=\"media-heading\"><span><small>" + CronExpressionDescriptor.getDescription(event.Time, Options.twentyFourHour(), Locale.US) + "</small></span></span>"
                        + "</div>"
                        + "</div>"
                        + "</td>"
                        + "<td class=\"v-a-m\">"
                        + "<span class=\"label label-gray-light label-outline\"><span>" + event.toFrendlyNameType() + "</span></span>"
                        + "</td>"
                        + "<td class=\"v-a-m\">"
                        + "<span class=\"label label-outline label-" + (event.Enabled ? "success" : "danger") + "\"><span>" + (event.Enabled ? "Enabled" : "Disabled") + "</span></span>"
                        + "</td>"
                        + "<td class=\"text-right v-a-m\">"
                        + "<div class=\"dropdown\">"
                        + "<button class=\"btn btn-default dropdown-toggle\" type=\"button\" data-toggle=\"dropdown\" aria-haspopup=\"true\" aria-expanded=\"false\">"
                        + "<i class=\"fa fa-gear m-r-1\"></i> <span class=\"caret\"></span>"
                        + "</button>"
                        + "<ul class=\"dropdown-menu dropdown-menu-right\">"
                        + "<li><a href=\"javascript: RunEvent('" + event.ID + "')\"><i class=\"fa fa-fw text-gray-lighter fa-play m-r-1\"></i>Run Now</a></li>"
                        + "<li role=\"separator\" class=\"divider\"></li>"
                        + "<li><a href=\"javascript: EditEvent('" + event.ID + "')\"><i class=\"fa fa-fw text-gray-lighter fa-pencil m-r-1\"></i>Edit</a></li>"
                        + "<li><a href=\"javascript: DuplicateEvent('" + event.ID + "')\"><i class=\"fa fa-fw text-gray-lighter fa-copy m-r-1\"></i>Duplicate</a></li>"
                        + "<li role=\"separator\" class=\"divider\"></li>";
                if (!event.Enabled) {
                    retur += "<li><a href=\"javascript: EnableEvent('" + event.ID + "')\"><i class=\"fa fa-fw text-gray-lighter fa-check-square-o m-r-1\"></i>Enable</a></li>";
                } else {
                    retur += "<li><a href=\"javascript: DisableEvent('" + event.ID + "')\"><i class=\"fa fa-fw text-gray-lighter fa-square-o m-r-1\"></i>Disable</a></li>";
                }

                retur += "<li role=\"separator\" class=\"divider\"></li>"
                        + "<li><a href=\"javascript: DeleteEvent('" + event.ID + "')\"><i class=\"fa fa-fw text-gray-lighter fa-trash m-r-1\"></i>Delete</a></li>"
                        + "</ul>"
                        + "</div>"
                        + "</td>"
                        + "</tr>";
            } catch (Exception e) {
                JHSLogingSystem.LogException(EventManager.class.getName(), "Unable to run Event["+event.ID+"] - Name["+event.Name+"] Parms["+event.Parms.toString()+"] Error:"+ExceptionUtils.getStackTrace(e)+"", e);
            }
        }
        return retur;
    }

    public Event GetEventById(String Id) {
        return events.get(Id);
    }

    public Message RunEvent(String Id) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "Event not found.";
        Event event = events.get(Id);
        if (event != null) {
            return event.RunNow();
        }
        return msg;
    }

    public Message ActivateEventById(String Id, Boolean enabled) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "Event not found.";
        Event event = events.get(Id);
        if (event != null) {
            event.Enabled = enabled;
            event.Save();
            RefreshEvents();
            msg.success = true;
            msg.content = Base64.encode(GetEvents().getBytes());
            if (enabled) {
                msg.message = "Event Activated.";
            } else {
                msg.message = "Event Disabled.";
            }
        }
        return msg;
    }

    public Message DuplicateEventById(String Id) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "Event not found.";
        Event event = events.get(Id);
        if (event != null) {
            Event eventd = new Event();
            eventd.Enabled = false;
            eventd.Name = event.Name;
            eventd.Time = event.Time;
            eventd.Parms = event.Parms;
            eventd.Value = event.Value;
            eventd.Type = event.Type;
            eventd.Save();
            RefreshEvents();
            msg.success = true;
            msg.content = Base64.encode(GetEvents().getBytes());
            msg.message = "Event Cloned.";
        }
        return msg;
    }

    public Message DeleteEventByID(String Id) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "NOOP";
        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configsx = ConfigManager.Instance;
        mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.DBNAME);
        String query = "DELETE FROM `events` WHERE `events`.`id` = ?";
        JHSMySQLResult result = mysql.Query(query, Id);
        if (result.SUCCESS) {
            RefreshEvents();
            msg.success = true;
            msg.message = "Event Deleted.";
            msg.content = Base64.encode(GetEvents().getBytes());
        } else {
            msg.success = true;
            msg.message = "Unable to delete event.";
        }
        mysql.close();
        return msg;
    }

    public Message SaveEvent(HttpServletRequest request) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "NOOP";
        try {
            String _ActivateMonsterId = request.getParameter("ActivateMonsterId");
            String _broadcastChannel = request.getParameter("broadcastChannel");
            //String _CMD = request.getParameter("CMD");
            String _eventId = request.getParameter("eventId");
            String _eventName = request.getParameter("eventName");
            String _eventType = request.getParameter("eventType");
            String _eventValue = request.getParameter("eventValue");
            String _ExpValue = request.getParameter("ExpValue");
            String _Lambda = request.getParameter("Lambda");
            String _MaxOnline = request.getParameter("MaxOnline");
            String _paramters = request.getParameter("paramters");
            String _timeSetting = request.getParameter("timeSetting");
            String _eventEnabled = request.getParameter("eventEnabled");
            String _ActivateMonsterWorld = request.getParameter("ActivateMonsterWorld");
            if (_timeSetting == null) {
                msg.success = false;
                msg.message = "Invalid Time Setting.";
                return msg;
            }
            if (!SchedulingPattern.validate(_timeSetting)) {
                msg.success = false;
                msg.message = "Invalid Time Setting.";
                return msg;
            }
            if (_eventType == null) {
                msg.success = false;
                msg.message = "Invalid Event Type.";
                return msg;
            }
            if (_eventName == null) {
                msg.success = false;
                msg.message = "Invalid Event Name.";
                return msg;
            }
            if (_eventName.length() <= 1) {
                msg.success = false;
                msg.message = "Invalid Event Name. Must be above >= 2!";
                return msg;
            }
            Integer IType = Integer.parseInt(_eventType);
            Integer EventId = Integer.parseInt(_eventId);
            EventType eventType = EventType.valueOf(IType);
            Integer ActivateMonsterID = 0;
            Integer ExpValue = 0;
            Integer MaxOnline = 0;
            Integer ActivateMonsterWorld = 0;
            String Lambda = "0";
            Boolean Enabled = Boolean.parseBoolean(_eventEnabled);
            Integer broadcastChannel = 0;
            List<String> parameters = null;
            switch (eventType) {
                case RUN_SCRIPT:
                    if (_eventValue.length() < 4) {
                        msg.success = false;
                        msg.message = "Invalid Script Value.";
                        return msg;
                    }
                    break;
                case RUN_BROADCAST:
                    if (_eventValue == null) {
                        msg.success = false;
                        msg.message = "Broadcast has to be more then one character.";
                        return msg;
                    }
                    if (_eventValue.length() < 1) {
                        msg.success = false;
                        msg.message = "Broadcast has to be more then one character.";
                        return msg;
                    }
                    if (_broadcastChannel == null) {
                        msg.success = false;
                        msg.message = "Please select a broadcast channel.";
                        return msg;
                    }
                    break;
                case RUN_INGAME_EVENT:
                    if (_paramters == null) {
                        msg.success = false;
                        msg.message = "Invalid Parameters.";
                        return msg;
                    }
                    parameters = Arrays.asList(_paramters.split(","));
                    if (parameters.size() == 0) {
                        msg.success = false;
                        msg.message = "Invalid Parameters.";
                        return msg;
                    }
                    if (parameters.contains("50")) {
                        if (_eventValue == null) {
                            msg.success = false;
                            msg.message = "Broadcast has to be more then one character.";
                            return msg;
                        }
                        if (_eventValue.length() < 1) {
                            msg.success = false;
                            msg.message = "Broadcast has to be more then one character.";
                            return msg;
                        }
                        if (_broadcastChannel == null) {
                            msg.success = false;
                            msg.message = "Please select a broadcast channel.";
                            return msg;
                        }
                        broadcastChannel = Integer.parseInt(_broadcastChannel);

                    }
                    if (parameters.contains("1") || parameters.contains("2")) {
                        if (_ActivateMonsterId == null) {
                            msg.success = false;
                            msg.message = "Monster Control Id can not be null.";
                            return msg;
                        }

                        if (_ActivateMonsterId.length() < 1) {
                            msg.success = false;
                            msg.message = "Monster Control Id can not be null.";
                            return msg;
                        }
                        if (_ActivateMonsterWorld == null) {
                            msg.success = false;
                            msg.message = "Monster World Tag can not be null.";
                            return msg;
                        }
                        if (_ActivateMonsterWorld.length() < 1) {
                            msg.success = false;
                            msg.message = "Monster World Tag can not be null.";
                            return msg;
                        }
                        ActivateMonsterID = Integer.parseInt(_ActivateMonsterId);
                        ActivateMonsterWorld = Integer.parseInt(_ActivateMonsterWorld);
                    }
                    if (parameters.contains("19")) {
                        if (_Lambda == null) {
                            msg.success = false;
                            msg.message = "Lambda can not be null.";
                            return msg;
                        }

                        if (_Lambda.length() < 1) {
                            msg.success = false;
                            msg.message = "Lambda can not be null.";
                            return msg;
                        }
                        Lambda = _Lambda;
                    }
                    if (parameters.contains("20")) {
                        if (_MaxOnline == null) {
                            msg.success = false;
                            msg.message = "Max Players Online can not be null.";
                            return msg;
                        }

                        if (_MaxOnline.length() < 1) {
                            msg.success = false;
                            msg.message = "Max Players Online can not be null.";
                            return msg;
                        }
                        MaxOnline = Integer.parseInt(_MaxOnline);
                    }
                    if (parameters.contains("21")) {
                        if (_ExpValue == null) {
                            msg.success = false;
                            msg.message = "Exp Value can not be null.";
                            return msg;
                        }

                        if (_ExpValue.length() < 1) {
                            msg.success = false;
                            msg.message = "Exp Value can not be null.";
                            return msg;
                        }
                        ExpValue = Integer.parseInt(_ExpValue);
                    }
                    break;
                default:
                    msg.success = false;
                    msg.message = "Invalid Event Type.";
                    return msg;
            }
            String parameterData = "|0_0_0_0_0";
            if (null == eventType) {
                parameterData = "";
            } else {
                switch (eventType) {
                    case RUN_BROADCAST:
                        parameterData = _broadcastChannel + parameterData;
                        break;
                    case RUN_INGAME_EVENT:
                        parameterData = _paramters.replace(",", "_") + "|" + ActivateMonsterID + "_" + ActivateMonsterWorld + "_" + ExpValue + "_" + MaxOnline + "_" + broadcastChannel + "_" + Lambda;
                        break;
                    default:
                        parameterData = "";
                        break;
                }
            }

            Event event = GetEventById(_eventId);
            if (event != null) {
                //Update
                event.Name = _eventName;
                event.Parms = parameterData;
                event.Type = eventType;
                event.Value = _eventValue;
                event.Enabled = Enabled;
                event.Time = _timeSetting;
                event.Save();
            } else {
                //Create New.
                event = new Event();
                event.ID = "0";
                event.Name = _eventName;
                event.Parms = parameterData;
                event.Type = eventType;
                event.Value = _eventValue;
                event.Enabled = Enabled;
                event.Time = _timeSetting;
                event.Save();
            }
            RefreshEvents();
            msg.content = CronExpressionDescriptor.getDescription(event.Time, Options.twentyFourHour(), Locale.US) + ".<br><small>(This is only informational and not 100% acurate please vist:<a href=\"http://www.sauronsoftware.it/projects/cron4j/manual.php#p01\" target=\"blank\">here</a> for more info.)</small>";
            msg.success = true;
            msg.message = "Event has been saved.";

        } catch (NumberFormatException | ParseException e) {
            JHSLogingSystem.LogException(EventManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
            msg.success = false;
            msg.message = "Could not save the event! Error:" + e.toString();
        }
        return msg;
    }
}
