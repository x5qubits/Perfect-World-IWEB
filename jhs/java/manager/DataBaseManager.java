/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import customProtocol.model.MyLinkedHashMap;
import java.util.HashMap;
import jhs.java.classes.Message;
import jhs.java.classes.Player;
import jhs.java.mysql.JHSMySQL;
import jhs.java.mysql.JHSMySQLResult;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import protocol.GameDB;
import protocol.RoleBean;

/**
 *
 * @author IcyTeck
 */
public class DataBaseManager {

    public static final DataBaseManager Instance = new DataBaseManager();
    public MyLinkedHashMap<String, Player> playerBase = new MyLinkedHashMap<>(1000, .75f, true);

    public DataBaseManager() {
    }

    public Player GetPlayer(String playerId) {
        Player player = new Player();
        try {
            if (playerBase.containsKey(playerId)) {
                return playerBase.get(playerId);
            } else {
                int roleId = Integer.parseInt(playerId);
                if (roleId > 0) {
                    player = GetPlayerFromGameDb(roleId);
                    playerBase.put(playerId, player);
                }
            }
        } catch (Exception ex) {
            JHSLogingSystem.LogException(DataBaseManager.class.getName()+".GetPlayer", ExceptionUtils.getStackTrace(ex) +"-"+playerId, ex);
        }
        return player;
    }

    private Player GetPlayerFromGameDb(int roleId) {
        Player player = new Player();
        try {
            RoleBean character;
            ConfigManager configs = ConfigManager.Instance;
            character = GameDB.get(roleId);
            if (character != null) {
                player.UserId = character.base.userid;
                player.Name = StringEscapeUtils.escapeHtml(character.base.name.getString());
                player.RoleId = roleId;
                player.Level = character.status.level;
                player.cls = character.base.cls;
                player.eventpoints = 0;
                player.kills = 0;
                player.killed = 0;
                player.worldtag = character.status.worldtag;
                if (configs.record_player_pvp) {
                    player.Load(); //Load KillsAnd Deaths
                }
            }else{
                JHSLogingSystem.LogInfo(DataBaseManager.class.getName()+".GetPlayerFromGameDb", "Unable to find player"+roleId); 
            }

        } catch (Exception ex) {
            JHSLogingSystem.LogException(DataBaseManager.class.getName()+".GetPlayerFromGameDb", ExceptionUtils.getStackTrace(ex), ex);
        }
        return player;
    }

    public boolean SavePlayers() {
        JHSLogingSystem.LogInfo(DataBaseManager.class.getName()+".SavePlayers", "Saving players count:" + playerBase.size());
        int count = 0;
        for (Player player : playerBase.values()) {
            if (player.Save()) {
                count++;
            }
        }
        JHSLogingSystem.LogInfo(DataBaseManager.class.getName()+".SavePlayers", "Saved count:" + count);
        return true;
    }

    public String GetGmsPermision(String finalGmID) {
        String ret = "";
        String delque = "SELECT * FROM auth WHERE userid=?;";
        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configs = ConfigManager.Instance;
        mysql.connect(configs.DBSTRING, configs.DBUSER, configs.DBPASS, configs.PWDBNAME);
        JHSMySQLResult resu = mysql.Query(delque, finalGmID);
        while (resu.next()) {
            ret += resu.getString("rid") + ",";
        }
        mysql.close();
        try {
            return ret.substring(0, ret.length() - 1);
        } catch (Exception e) {
            return "";
        }
    }

    public Message SaveGmId(String finalGmID, String data, Integer type) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "NOPE.";

        String zone = "1";
        String query = "INSERT INTO auth (userid, zoneid, rid) VALUES ";
        switch (type) {
            case 2:
                int x = 0;
                while (x < 12) {
                    query += "(" + finalGmID + ", " + zone + ", " + x + "),";
                    x++;
                }
                x = 100;
                while (x < 106) {
                    query += "(" + finalGmID + ", " + zone + ", " + x + "),";
                    x++;
                }
                x = 200;
                while (x < 215) {
                    query += "(" + finalGmID + ", " + zone + ", " + x + "),";
                    x++;
                }
                x = 500;
                while (x < 518) {
                    query += "(" + finalGmID + ", " + zone + ", " + x + "),";
                    x++;
                }
                query += "(" + finalGmID + ", " + zone + ", 519);";
                msg.success = true;
                break;
            case 0:
                query = "INSERT INTO `auth` (`userid`, `zoneid`, `rid`) VALUES \n"
                        + "(" + finalGmID + ", " + zone + ", 1),\n"
                        + "(" + finalGmID + ", " + zone + ", 2),\n"
                        + "(" + finalGmID + ", " + zone + ", 3),\n"
                        + "(" + finalGmID + ", " + zone + ", 4),\n"
                        + "(" + finalGmID + ", " + zone + ", 5),\n"
                        + "(" + finalGmID + ", " + zone + ", 6),\n"
                        + "(" + finalGmID + ", " + zone + ", 104),\n"
                        + "(" + finalGmID + ", " + zone + ", 500),\n"
                        + "(" + finalGmID + ", " + zone + ", 501);";
                msg.success = true;
                break;
            case 1:
                query = "INSERT INTO `auth` (`userid`, `zoneid`, `rid`) VALUES \n"
                        + "(" + finalGmID + ", " + zone + ", 0),\n"
                        + "(" + finalGmID + ", " + zone + ", 1),\n"
                        + "(" + finalGmID + ", " + zone + ", 2),\n"
                        + "(" + finalGmID + ", " + zone + ", 3),\n"
                        + "(" + finalGmID + ", " + zone + ", 4),\n"
                        + "(" + finalGmID + ", " + zone + ", 5),\n"
                        + "(" + finalGmID + ", " + zone + ", 6),\n"
                        + "(" + finalGmID + ", " + zone + ", 11),\n"
                        + "(" + finalGmID + ", " + zone + ", 100),\n"
                        + "(" + finalGmID + ", " + zone + ", 101),\n"
                        + "(" + finalGmID + ", " + zone + ", 102),\n"
                        + "(" + finalGmID + ", " + zone + ", 103),\n"
                        + "(" + finalGmID + ", " + zone + ", 104),\n"
                        + "(" + finalGmID + ", " + zone + ", 105),\n"
                        + "(" + finalGmID + ", " + zone + ", 200),\n"
                        + "(" + finalGmID + ", " + zone + ", 206),\n"
                        + "(" + finalGmID + ", " + zone + ", 500),\n"
                        + "(" + finalGmID + ", " + zone + ", 501);";
                msg.success = true;
                break;
            case 3:
                if (data.length() > 0) {
                    String[] split = data.split(",");

                    if (split.length <= 0) {
                        msg.message = "Invalid FormData.";
                    }
                    for (String split1 : split) {
                        query += "(" + finalGmID + ", " + zone + ", " + split1 + "),";
                    }
                    query = query.substring(0, query.length() - 1);
                    msg.success = true;
                } else {
                    msg.message = "Invalid FormData.";
                }

                break;
            default:
                msg.message = "Invalid FormData.";
        }
        if (msg.success) {
            String delque = "DELETE FROM `auth` WHERE `userid`=?";
            JHSMySQL mysql = JHSMySQL.getInstance();
            ConfigManager configs = ConfigManager.Instance;
            mysql.connect(configs.DBSTRING, configs.DBUSER, configs.DBPASS, configs.PWDBNAME);
            mysql.Query(delque, finalGmID);
            mysql.Query(query);
            mysql.close();
            msg.content = GetGmsPermision(finalGmID);
            msg.message = "Saved.";
        }
        return msg;
    }

    public HashMap<String, String> permissionsAdvanced = new HashMap<String, String>() {
        {
            put("0", "Player/Item/IP ID");
            put("1", "Stelth/Invincible On");
            put("2", "Online Status");
            put("3", "Hide Wisper");
            put("4", "Teleport to player");
            put("5", "Teleport player to GM");
            put("6", "Teleport by ctrl+clicking map");
            put("11", "Show online Players Count");
            put("100", "Ban player account/character");
            put("101", "Mute player account/character");
            put("102", "Ban trading for a player");
            put("103", "Ban selling for a player");
            put("104", "GM announcement broadcast");
            put("105", "Restart gameserver");
            put("200", "Create Monster");
            put("201", "Delete Monster");
            put("202", "Morph into Monster");
            put("204", "GM Administrator");
            put("205", "Set same-ip connection limit");
            put("206", "Activate Monster Creator");
            put("207", "Forbid trade for all players");
            put("208", "Forbid auction for all players");
            put("209", "Forbid ingame mail for all players");
            put("210", "Forbid all Faction activity");
            put("211", "Set double money drop");
            put("212", "Set double item drop");
            put("213", "Set double SP reward");
            put("214", "Forbid point card selling");
            put("500", "GM TAG/DO NOT REMOVE");
            put("501", "Edit character data");
            put("502", "Check status of server");
            put("503", "Check logs");
            put("504", "Forcefully reboot game server");
            put("505", "Forcefully reboot database server");
            put("506", "Find ID of char name");
            put("507", "Character data view");
            put("508", "Character online status");
            put("509", "Send mail with item to user");
            put("510", "See ban history");
            put("511", "See cubigold payments");
            put("512", "See cubigold amount");
            put("513", "Add cubigold");
            put("514", "View by Account username");
            put("515", "Edit account username");
            put("516", "Remove ban");
            put("517", "Get Role list");
            put("518", "Change Account password");
        }
    };

}
