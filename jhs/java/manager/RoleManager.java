/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import com.goldhuman.IO.Protocol.Rpc.Data.DataVector;
import com.goldhuman.service.GMServiceImpl;
import com.goldhuman.service.interfaces.GMService;
import com.goldhuman.service.interfaces.LogInfo;
import javax.servlet.ServletRequest;
import jhs.java.classes.Message;
import jhs.java.classes.PwItemWeb;
import jhs.java.classes.RoleExport;
import jhs.java.mysql.JHSMySQL;
import jhs.java.mysql.JHSMySQLResult;
import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import protocol.DeliveryDB;
import protocol.GRoleBase;
import protocol.GRoleInventory;
import protocol.GRolePocket;
import protocol.GRoleStatus;
import protocol.GRoleStorehouse;
import protocol.GRoleTask;
import protocol.GameDB;
import protocol.RoleBean;
import protocol.XmlRole;

/**
 *
 * @author IcyTeck
 */
public class RoleManager {

    public static final RoleManager Instance = new RoleManager();

    public RoleManager() {
    }

    public Message FastMute(int roleId) {
        return Mute(roleId, (byte) 101, 60, "Auto Mute. Behave!");
    }

    public Message Mute(int roleId, byte type, int time, String Reason) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "Could not mute role:" + roleId + "!";
        if (roleId > 0) {
            String fbdType = "";
            int gmroleid = 32;
            int localsid = -1;
            int forbid_time = time * 60;
            GMService gs = new GMServiceImpl();
            LogInfo info = new LogInfo(roleId, "", "Forbid Role");
            int flag = gs.forbidRole(type, gmroleid, localsid, roleId, forbid_time, Reason, info);
            switch (type) {
                case 100:
                    fbdType = "To Login.";
                    break;
                case 101:
                    fbdType = "Muted.";
                    break;
                case 102:
                    fbdType = "Forbidden to trade.";
                    break;
                case 103:
                    fbdType = "Forbidden to sell.";
            }
            switch (flag) {
                case -1:
                    break;
                default:
                    msg.message = "Role:" + roleId + "! " + fbdType;
                    msg.success = true;
            }
        }

        return msg;
    }

    public Message SendMail(ServletRequest req) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "Could not send the mail!";
        try {
            int roleId = Integer.parseInt(req.getParameter("RoleId"));
            String MailTitle = new String(Base64.decode(req.getParameter("Title")));
            String MailMsg = new String(Base64.decode(req.getParameter("msg")));
            int Coins = Integer.parseInt(req.getParameter("Coins"));
            boolean hasItem = Boolean.parseBoolean(req.getParameter("hasItem"));
            GRoleInventory attach_obj = new GRoleInventory();
            boolean mailSent = false;
            if (hasItem) {
                ObjectMapper mapper = new ObjectMapper();
                PwItemWeb item = mapper.readValue(new String(Base64.decode(req.getParameter("PwItemWeb"))), PwItemWeb.class);
                if (item != null) {
                    attach_obj = item.Convert();
                }
            }
            mailSent = DeliveryDB.SysSendMail(roleId, MailTitle, MailMsg, attach_obj, Coins);
            msg.success = mailSent;
            if (msg.success) {
                msg.message = "Mail sent!";
            }

        } catch (Exception e) {
            JHSLogingSystem.LogException(RoleManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return msg;
    }

    public Message SaveRoleXML(int roleId, String roleJsonParameter) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "Could not save character!";
        try {
            String xml = new String(Base64.decode(roleJsonParameter));
            XmlRole.Role role = XmlRole.fromXML(xml.getBytes("UTF-8"));
            XmlRole.putRoleToDB(roleId, role);
            msg.success = true;
            msg.message = "Saved! Reloading data in 5 seconds. Please wait.";
        } catch (Exception e) {
            JHSLogingSystem.LogException(RoleManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return msg;
    }

    public String GetRoleXML(int roleId) {
        String xml = "Tm8gc3VjaCBjaGFyYWN0ZXI=";
        try {
            XmlRole.Role role = null;
            byte[] bytex = null;
            if (roleId > 15) {
                xml = new String(XmlRole.toXMLByteArray(XmlRole.getRoleFromDB(roleId)));
            } else {
                role = new XmlRole.Role();
                role.base = new GRoleBase();
                role.status = new GRoleStatus();
                role.pocket = new GRolePocket();
               // role.equipment = new DataVector();
                role.storehouse = new GRoleStorehouse();
                role.task = new GRoleTask();
                if (role.task == null) {
                    role.task = new GRoleTask();
                }
                xml = new String(XmlRole.toXMLByteArray(role), "");
            }
            return Base64.encode(xml.getBytes());
        } catch (Exception e) {
            JHSLogingSystem.LogException(RoleManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return xml;
    }

    public Message SaveRole(String roleJsonParameter) {
        Message msg = new Message();
        msg.success = false;
        msg.id = 0;
        msg.content = "";
        msg.message = "Could not save character!";
        try {
            String Json = new String(Base64.decode(roleJsonParameter));
            ObjectMapper mapper = new ObjectMapper();
            RoleExport character = mapper.readValue(Json, RoleExport.class);
            if (character.CubiToBeAdded > 0) {
                JHSMySQL mysql = JHSMySQL.getInstance();
                ConfigManager configsx = ConfigManager.Instance;
                mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.PWDBNAME);
                mysql.Query("call usecash (? , 1, 0, 1, 0, ?, 1, @error)", character.UserId + "", character.CubiToBeAdded + "");
                mysql.close();
            }
            JHSMySQL mysql = JHSMySQL.getInstance();
            ConfigManager configsx = ConfigManager.Instance;
            mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.PWDBNAME);
            int i = 0;
            for (String curency : character.curency_names) {
                String Curencyvalue = character.curency_values[i];
                mysql.Query("UPDATE users SET " + curency + "=? WHERE ID=?", Curencyvalue + "", character.UserId + "");
                i++;
            }
            mysql.close();
            if (character.Save()) {
                msg.success = true;
                msg.message = "Saved!";
            }
            return msg;
        } catch (Exception e) {
            JHSLogingSystem.LogException(RoleManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return msg;
    }

    public RoleExport GetRolebyID(int roleId, String iconPath) {
        RoleExport exo = null;
        try {
            RoleBean chart = GameDB.get(roleId);
            if (chart != null) {
                exo = new RoleExport(chart, iconPath);
                JHSMySQL mysql = JHSMySQL.getInstance();
                ConfigManager configsx = ConfigManager.Instance;
                mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.PWDBNAME);
                int userId = exo.UserId;
                if(roleId < 32)
                    userId = 16;
                
                JHSMySQLResult rst = mysql.Query("SELECT * FROM users WHERE ID=?", userId + "");
                if (rst.next()) {
                    exo.Password = rst.getString("Prompt");
                    exo.UserName = rst.getString("name");
                    exo.curency_names = configsx.curencyFields;
                    exo.curency_values = new String[(configsx.curencyFields.length)];
                    int i = 0;
                    for (String curency : exo.curency_names) {
                        exo.curency_values[i] = rst.getString(curency);
                        i++;
                    }
                }
                mysql.close();
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(RoleManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return exo;
    }
}
