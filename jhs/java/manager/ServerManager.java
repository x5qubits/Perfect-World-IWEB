/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import Enums.NotificationType;
import com.goldhuman.Common.Conf;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jhs.java.classes.Message;
import jhs.java.classes.Notification;
import java.util.logging.*;
import jhs.java.classes.ChatMessage;

/**
 *
 * @author IcyTeck
 */
public class ServerManager {

    public static ServerManager Instance = new ServerManager();
    private int lastNotification = 0;
    public HashMap<Integer, Notification> notifications = new HashMap<>();

    public void PutNotification(Notification notification) {
        lastNotification++;
        notifications.put(lastNotification, notification);
    }

    public void RemoveNotification(Integer id) {
        notifications.remove(id);
    }

    public void RemoveNotifications() {
        notifications.clear();
    }

    public String Count() {
        return notifications.size() + "";
    }

    public String GetNotifications() {

        String returx = "";
        for (Map.Entry<Integer, Notification> entry : notifications.entrySet()) {
            Integer key = entry.getKey();
            Notification value = entry.getValue();

            returx += "<a href=\"#\" onClick=\"deleteNotification('" + key + "'); return false;\" id-data=\"" + key + "\" class=\"list-group-item b-r-0 b-t-0 b-l-0\">\n"
                    + "           <div class=\"media\">\n"
                    + "              <div class=\"media-left\">\n"
                    + "                 <span class=\"fa-stack fa-lg\">\n"
                    + "" + value.hclass + ""
                    + "                 </span>\n"
                    + "              </div>\n"
                    + "              <div class=\"media-body\">\n"
                    + "                 <h5 class=\"m-t-0\">\n"
                    + "                    <span>" + value.content + "</span>\n"
                    + "                 </h5>\n"
                    + "                 <p class=\"text-nowrap small m-b-0\">\n"
                    + "                    <span>" + value.Clock + "</span>\n"
                    + "                 </p>\n"
                    + "              </div>\n"
                    + "           </div>\n"
                    + "        </a>";

        }

        return returx;
    }

    public Message StartDemonByName(String name) {
        Logger.getLogger(ServerManager.class.getName()).log(Level.INFO, "Starting " + name);
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "NOOP";
        Conf conf = Conf.GetInstance("/etc/iweb.conf", null);
        String pw_server_pathx = conf.find("OTHER_CONFIGS", "server_path");
        Long scriptID = ConfigManager.Instance.GetNewTransactionId();
        File f = new File(pw_server_pathx + "demon_independent_" + scriptID + ".sh");
        try {
            if (name.length() < 1) {
                msg.success = false;
                msg.message = "Starting Up Demon Failed! Error: String was undefined";
                ServerManager.Instance.PutNotification(new Notification(msg.message, NotificationType.Error));
                return msg;
            }
            FileWriter fw = new FileWriter(f);
            fw.write("if [ ! -d " + pw_server_pathx + "logs ]; then\n");
            fw.write("mkdir " + pw_server_pathx + "logs\n");
            fw.write("fi\n");

            if (name.equals("logservice")) {
                fw.write("cd " + pw_server_pathx + "logservice; ./logservice logservice.conf >" + pw_server_pathx + "logs/logservice.log &\n");
                fw.write("sleep 1\n");
            } else if (name.equals("uniquenamed")) {
                fw.write("cd " + pw_server_pathx + "uniquenamed; ./uniquenamed gamesys.conf >" + pw_server_pathx + "logs/uniquenamed.log &\n");
                fw.write("sleep 1\n");
            } else if (name.equals("authd") || name.equals("auth")) {
                fw.write("cd " + pw_server_pathx + "authd/; ./authd >" + pw_server_pathx + "logs/auth.log &\n");
                fw.write("sleep 1\n");
            } else if (name.equals("gamedbd")) {
                fw.write("cd " + pw_server_pathx + "gamedbd; ./gamedbd gamesys.conf >" + pw_server_pathx + "logs/gamedbd.log &\n");
                fw.write("sleep 1\n");
            } else if (name.equals("gacd")) {
                fw.write("cd " + pw_server_pathx + "gacd; ./gacd gamesys.conf >" + pw_server_pathx + "logs/gacd.log &\n");
                fw.write("sleep 1\n");
            } else if (name.equals("gfactiond")) {
                fw.write("cd " + pw_server_pathx + "gfactiond; ./gfactiond gamesys.conf >" + pw_server_pathx + "logs/gfactiond.log &\n");
                fw.write("sleep 1\n");
            } else if (name.equals("gdeliveryd")) {
                fw.write("cd " + pw_server_pathx + "gdeliveryd; ./gdeliveryd gamesys.conf >" + pw_server_pathx + "logs/gdeliveryd.log &\n");
                fw.write("sleep 1\n");
            } else if (name.equals("glinkd")) {
                String[] glinks = IwebManager.Instance.glinkd_startCount;
                for (String g : glinks) {
                    fw.write("cd " + pw_server_pathx + "glinkd; ./glinkd gamesys.conf " + g + " >" + pw_server_pathx + "logs/glink1.log &\n");
                }
                fw.write("sleep 1\n");
            }
            fw.close();
            String command = "sh " + pw_server_pathx + "demon_independent_" + scriptID + ".sh";
            File working_directory = new File(pw_server_pathx);
            Process p = Runtime.getRuntime().exec("chmod 777 " + pw_server_pathx + "demon_independent_" + scriptID + ".sh");
            p.waitFor();
            p = Runtime.getRuntime().exec(command, null, working_directory);
            p.waitFor();
            f.delete();
            msg.success = true;
            msg.message = "Demon started up.";
            ServerManager.Instance.PutNotification(new Notification(msg.message));
        } catch (Exception e) {
            f.delete();
            msg.success = false;
            msg.message = "Starting Up Demon Failed! Error:" + e.toString();
            ServerManager.Instance.PutNotification(new Notification(msg.message, NotificationType.Error));
        }
        return msg;
    }

    public Message StartAllDemons() {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        Conf conf = Conf.GetInstance("/etc/iweb.conf", null);
        String pw_server_pathx = conf.find("OTHER_CONFIGS", "server_path");
        File f = new File(pw_server_pathx + "server_starter.sh");
        try {
            FileWriter fw = new FileWriter(f);
            fw.write("if [ ! -d " + pw_server_pathx + "logs ]; then\n");
            fw.write("mkdir " + pw_server_pathx + "logs\n");
            fw.write("fi\n");
            fw.write("cd " + pw_server_pathx + "logservice; ./logservice logservice.conf >" + pw_server_pathx + "logs/logservice.log &\n");
            fw.write("sleep 1\n");
            fw.write("cd " + pw_server_pathx + "uniquenamed; ./uniquenamed gamesys.conf >" + pw_server_pathx + "logs/uniquenamed.log &\n");
            fw.write("sleep 1\n");
            fw.write("cd " + pw_server_pathx + "authd/; ./authd >" + pw_server_pathx + "logs/auth.log &\n");
            fw.write("sleep 1\n");
            fw.write("cd " + pw_server_pathx + "gamedbd; ./gamedbd gamesys.conf >" + pw_server_pathx + "logs/gamedbd.log &\n");
            fw.write("sleep 1\n");
            fw.write("cd " + pw_server_pathx + "gacd; ./gacd gamesys.conf >" + pw_server_pathx + "logs/gacd.log &\n");
            fw.write("sleep 1\n");
            fw.write("cd " + pw_server_pathx + "gfactiond; ./gfactiond gamesys.conf >" + pw_server_pathx + "logs/gfactiond.log &\n");
            fw.write("sleep 1\n");
            fw.write("cd " + pw_server_pathx + "gdeliveryd; ./gdeliveryd gamesys.conf >" + pw_server_pathx + "logs/gdeliveryd.log &\n");
            fw.write("sleep 1\n");
            String[] glinks = IwebManager.Instance.glinkd_startCount;
            for (String g : glinks) {
                fw.write("cd " + pw_server_pathx + "glinkd; ./glinkd gamesys.conf " + g + " >" + pw_server_pathx + "logs/glink1.log &\n");
            }
            fw.write("sleep 1\n");
            fw.close();
            String command = "sh " + pw_server_pathx + "server_starter.sh";
            File working_directory = new File(pw_server_pathx);
            Process p = Runtime.getRuntime().exec("chmod 777 " + pw_server_pathx + "server_starter.sh");
            p.waitFor();
            p = Runtime.getRuntime().exec(command, null, working_directory);
            p.waitFor();
            f.delete();
            msg.success = true;
            msg.message = "Server is Starting Up... it could take some minutes till server is fully up and running.";
            ServerManager.Instance.PutNotification(new Notification(msg.message));
        } catch (Exception e) {
            f.delete();
            msg.success = false;
            msg.message = "Starting Up Server Failed! Error:" + e.toString();
            ServerManager.Instance.PutNotification(new Notification(msg.message, NotificationType.Error));
        }
        return msg;
    }

    public Message StopAllDemons() {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "NOOP";
        try {
            Process p = Runtime.getRuntime().exec("pkill -9 gs");
            p.waitFor();
            p = Runtime.getRuntime().exec("pkill -9 glinkd");
            p.waitFor();
            p = Runtime.getRuntime().exec("pkill -9 gdeliveryd");
            p.waitFor();
            p = Runtime.getRuntime().exec("pkill -9 gfactiond");
            p.waitFor();
            p = Runtime.getRuntime().exec("pkill -9 gacd");
            p.waitFor();
            p = Runtime.getRuntime().exec("pkill -9 gamedbd");
            p.waitFor();
            p = Runtime.getRuntime().exec("pkill -9 uniquenamed");
            p.waitFor();
            p = Runtime.getRuntime().exec("ps -A w");
            BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line;
            while ((line = input.readLine()) != null) {
                if (line.indexOf("auth") != -1) {
                    Runtime.getRuntime().exec("kill " + line.substring(0, 5).replace(" ", ""));
                }
            }
            p = Runtime.getRuntime().exec("pkill -9 logservice");
            p.waitFor();
            p = Runtime.getRuntime().exec("sync");
            p.waitFor();
            FileWriter fw = new FileWriter(new File("/proc/sys/vm/drop_caches"));
            fw.write("3");
            fw.close();
            msg.success = true;
            msg.message = "Server Turned Off.";
            ServerManager.Instance.PutNotification(new Notification(msg.message, NotificationType.Danger));
        } catch (Exception e) {
            msg.success = false;
            msg.message = "Turning Off Server Failed! Error:" + e.toString();
            ServerManager.Instance.PutNotification(new Notification(msg.message, NotificationType.Error));
        }
        return msg;
    }

    public Message KillProcessById(String id) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "NOOP";
        try {
            if (id.length() > 0) {
                Process p2 = Runtime.getRuntime().exec("kill " + id);
                p2.waitFor();
            }
            msg.success = true;
            msg.message = "Killing Success.";
        } catch (Exception e) {
            msg.success = false;
            msg.message = "Killing proces Failed! Error:" + e.toString();
        }
        return msg;
    }

    public boolean KillProcessById(int id) {
        try {
            if (id > 0) {
                Process p2 = Runtime.getRuntime().exec("kill " + id);
                p2.waitFor();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean KillProcessByName(String name) {
        try {
            if (name.length() > 1) {
                Process p = Runtime.getRuntime().exec("/usr/bin/killall -SIGUSR1 " + name);
                p.waitFor();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ArrayList<ChatMessage> GetChatMessages(int show_lines) {
        return ServerLogManager.Instance.GetChats(show_lines);
    }

}
