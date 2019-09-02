/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import Enums.NotificationType;
import customProtocol.model.MyFormatter;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhs.java.classes.Notification;

/**
 *
 * @author IcyTeck
 */
public class JHSLogingSystem {

    private static Logger LOGGER = null;
    public static final JHSLogingSystem Instance = new JHSLogingSystem();

    public JHSLogingSystem() {
        LOGGER = Logger.getLogger(JHSLogingSystem.class.getName());
        LOGGER.setUseParentHandlers(false);

        MyFormatter formatter = new MyFormatter();
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(formatter);

        LOGGER.addHandler(handler);
    }

    public static void LogError(String CLSName, String msg, Throwable thrown) {
        LOGGER.log(Level.SEVERE, "[" + CLSName.replace("jhs.java.classes.", "").replace("jhs.java.manager.", "") + "]:" + msg, thrown);
        ServerManager.Instance.PutNotification(new Notification("An error was thrown in class:"+CLSName+" please check the logs.", NotificationType.Error));
    }

    public static void LogException(String CLSName, String msg, Throwable thrown) {
        LOGGER.log(Level.WARNING, "[" + CLSName.replace("jhs.java.classes.", "").replace("jhs.java.manager.", "") + "]:" + msg, thrown);
        ServerManager.Instance.PutNotification(new Notification("An exception was thrown in class:"+CLSName+" please check the logs.", NotificationType.Danger));
    }

    public static void LogInfo(String CLSName, String msg) {
        LOGGER.log(Level.INFO, "[{0}]:{1}", new Object[]{CLSName.replace("jhs.java.classes.", "").replace("jhs.java.manager.", ""), msg});
    }
}
