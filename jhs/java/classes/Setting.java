/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import jhs.java.manager.ConfigManager;
import jhs.java.mysql.JHSMySQL;

/**
 *
 * @author IcyTeck
 */
public class Setting {

    public String name = "";
    public String value = "";
    public int id = 0;

    public Boolean Save() {
        JHSMySQL mysql = JHSMySQL.getInstance();
        ConfigManager configsx = ConfigManager.Instance;
        mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.DBNAME);
        String query = "INSERT INTO `settings` (`id`, `name`, `value`) VALUES"
                + "(?, ?, ?)"
                + " ON DUPLICATE KEY UPDATE name=?, value=?;";
        boolean saved = mysql.Query(query, id + "", name + "", value, name, value).SUCCESS;
        mysql.close();
        return saved;
    }
}
