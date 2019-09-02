/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import java.util.HashMap;
import jhs.java.classes.StringExtension;
import jhs.java.mysql.JHSMySQL;
import jhs.java.mysql.JHSMySQLResult;

/**
 *
 * @author IcyTeck
 */
public class LoginManager {

    public static LoginManager Instance = new LoginManager();
    public boolean connected = false;

    public Boolean DoLogin(String user, String passwor) {
        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configs = ConfigManager.Instance;
        connected = mysql.connect(configs.DBSTRING, configs.DBUSER, configs.DBPASS, configs.DBNAME);

        JHSMySQLResult result = mysql.Query("SELECT * FROM users WHERE name=? and pass=?", user, passwor);
        boolean loged = result.next();
        mysql.close();
        return loged;
    }

    public JHSMySQLResult DoLoginRe(String user, String passwor) {
        if(StringExtension.NotValidString(user) || StringExtension.NotValidString(passwor))
        {
            connected = true;
            return StringExtension.ReturnError(user);
        }
        
        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configs = ConfigManager.Instance;
        connected = mysql.connect(configs.DBSTRING, configs.DBUSER, configs.DBPASS, configs.DBNAME);        
        return mysql.Query("SELECT * FROM users WHERE name=? and pass=?", user, passwor);
    }
}
