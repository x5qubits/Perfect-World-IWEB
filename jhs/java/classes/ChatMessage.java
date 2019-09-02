/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import java.io.UnsupportedEncodingException;
import jhs.java.manager.DataBaseManager;
import jhs.java.manager.JHSLogingSystem;
import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

/**
 *
 * @author IcyTeck
 */
public class ChatMessage {

    public String Chat = "World";
    public String SRC = "0";
    public String SRCName = "-1";
    public String DEST = "0";
    public String DESTName = "-1";
    public String TYPE = "Chat";
    public String message = "";
    public String color = "";
    public String time = "";

    public ChatMessage() {
    }

    public ChatMessage(String line, Boolean decode) {
        // Whisper
        try {
            time = line.substring(0, 19);
            line = line.substring(line.indexOf(": chat :") + 9);
            if (line.startsWith("Whisper")) {
                message = line.substring(line.indexOf("msg=") + 4);
                SRC = line.substring(line.indexOf("src=") + 4);
                SRC = SRC.substring(0, SRC.indexOf(" "));
                SRCName = DataBaseManager.Instance.GetPlayer(SRC).getName();
                DEST = line.substring(line.indexOf("dst=") + 4);
                DEST = DEST.substring(0, DEST.indexOf(" "));
                if(DEST != null && DEST.length() > 0)
                    DESTName = DataBaseManager.Instance.GetPlayer(DEST).getName();
                
                TYPE = "Whisper";
                color = "label-minsk";
            }
            // Faction
            if (line.startsWith("Guild")) {
                message = line.substring(line.indexOf("msg=") + 4);
                SRC = line.substring(line.indexOf("src=") + 4);
                SRC = SRC.substring(0, SRC.indexOf(" "));
                SRCName = DataBaseManager.Instance.GetPlayer(SRC).getName();
                DEST = line.substring(line.indexOf("fid=") + 4);
                TYPE = "Faction";
                DEST = "Faction(" + DEST.substring(0, DEST.indexOf(" ")) + ")";
                color = "label-malibu";
            }
            // Chatroom
            if (line.startsWith("Group")) {
                message = line.substring(line.indexOf("msg=") + 4);
                SRC = line.substring(line.indexOf("src=") + 4);
                SRC = SRC.substring(0, SRC.indexOf(" "));
                SRCName = DataBaseManager.Instance.GetPlayer(SRC).getName();
                DEST = line.substring(line.indexOf("room=") + 5);
                DEST = "Room(" + DEST.substring(0, DEST.indexOf(" ")) + ")";
                TYPE = "Room";
                color = "label-ighter";
            }
            if (line.startsWith("Chat")) {
                // Common
                if (line.indexOf("chl=0") != -1) {
                    message = line.substring(line.indexOf("msg=") + 4);
                    SRC = line.substring(line.indexOf("src=") + 4);
                    SRC = SRC.substring(0, SRC.indexOf(" "));
                    SRCName = DataBaseManager.Instance.GetPlayer(SRC).getName();
                    DEST = "Common";
                    TYPE = "Common";
                    color = "label-ighter";
                }
                // World
                if (line.indexOf("chl=1") != -1) {
                    message = line.substring(line.indexOf("msg=") + 4);
                    SRC = line.substring(line.indexOf("src=") + 4);
                    SRC = SRC.substring(0, SRC.indexOf(" "));
                    SRCName = DataBaseManager.Instance.GetPlayer(SRC).getName();
                    DEST = "&nbsp;";
                    TYPE = "World";
                    color = "label-warning";
                }
                // Squad
                if (line.indexOf("chl=2") != -1) {
                    message = line.substring(line.indexOf("msg=") + 4);
                    SRC = line.substring(line.indexOf("src=") + 4);
                    SRC = SRC.substring(0, SRC.indexOf(" "));
                    SRCName = DataBaseManager.Instance.GetPlayer(SRC).getName();
                    DEST = "&nbsp;";
                    TYPE = "Squad";
                    color = "label-success";
                }
                // Trade
                if (line.indexOf("chl=7") != -1) {
                    message = line.substring(line.indexOf("msg=") + 4);
                    SRC = line.substring(line.indexOf("src=") + 4);
                    SRC = SRC.substring(0, SRC.indexOf(" "));
                    SRCName = DataBaseManager.Instance.GetPlayer(SRC).getName();
                    DEST = "&nbsp;";
                    TYPE = "Trade";
                    color = "label-info";
                }
                // Broadcast
                if (line.indexOf("chl=9") != -1) {
                    message = line.substring(line.indexOf("msg=") + 4);
                    SRC = line.substring(line.indexOf("src=") + 4);
                    SRC = SRC.substring(0, SRC.indexOf(" "));
                    SRCName = DataBaseManager.Instance.GetPlayer(SRC).getName();
                    TYPE = "System";
                    DEST = "&nbsp;";
                    color = "label-danger";
                }
                if (decode) {
                    try{   
                        String newmsg = GetMessage();
                        byte[] bytes = newmsg.getBytes("UTF-8");
                        message = Base64.encode(bytes);
                    } catch (Exception e) {
                    
                    }
                }
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(ChatMessage.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }

    public String GetMessage() {
        try {
            return new String(Base64.decode(message), "UTF-16LE");
        } catch (UnsupportedEncodingException e) {
            JHSLogingSystem.LogException(ChatMessage.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return new String(Base64.decode(message));
    }
}
