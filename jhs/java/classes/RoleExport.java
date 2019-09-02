/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import jhs.java.manager.JHSLogingSystem;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import protocol.GRoleInventory;
import protocol.GameDB;
import protocol.RoleBean;

/**
 *
 * @author IcyTeck
 */
public class RoleExport {

    public int RoleId = 0;
    public int UserId = 0;
    public String Name = "";
    public String UserName = "";
    public String Password = "-";
    public String[] curency_names;
    public String[] curency_values;
    public int CubiToBeAdded = 0;
    public int Level = 0;
    public int pock_money = 0;
    public int bank_money = 0;
    public int Level2 = 0;
    public int reputation = 0;
    public int health = 0;
    public int mana = 0;
    public int exp = 0;
    public int spirit = 0;
    public int vigor = 0;
    public int race = 0;
    public int occupation = 0;
    public byte gender = 0;
    public int spouse = 0;
    public int faction = 0;
    public int attribute = 0;
    public byte status = 0;
    public String creationtime = "";
    public String deletiontime = "";
    public String lastlogin = "";
    public String online = "";
    public int world = 0;
    public float coordinateX = 0;
    public float coordinateY = 0;
    public float coordinateZ = 0;
    public String cubiamount = "";
    public String cubipurchased = "";
    public String cubibought = "";
    public String cubiused = "";
    public String cubisold = "";
    public int pkmode = 0;
    public int pkinvadertime = 0;
    public int pkpariahtime = 0;
    public ArrayList<PwItemWeb> equipment = new ArrayList<>();
    public ArrayList<PwItemWeb> pocket = new ArrayList<>();
    public ArrayList<PwItemWeb> storehouse = new ArrayList<>();
    public ArrayList<PwItemWeb> task_inventory = new ArrayList<>();
    public ArrayList<PwItemWeb> dress = new ArrayList<>();
    public ArrayList<PwItemWeb> generalcard = new ArrayList<>();
    public ArrayList<PwItemWeb> material = new ArrayList<>();

    public RoleExport(RoleBean character, String iconPath) {
        try {
            RoleId = character.base.id;
            UserId = character.base.userid;
            Name = character.base.name.getString();
            Level = character.status.level;
            Level2 = character.status.level2;
            pock_money = character.pocket.money;
            bank_money = character.storehouse.money;
            reputation = character.status.reputation;
            health = character.ep.max_hp;
            mana = character.ep.max_mp;
            exp = character.status.exp;
            spirit = character.status.sp;
            vigor = character.ep.max_ap;
            race = character.base.race;
            occupation = character.base.cls;
            gender = character.base.gender;
            spouse = character.base.spouse;
            status = character.base.status;
            attribute = character.status.pp;
            creationtime = (character.base.create_time <= 0) ? "-" : (new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss")).format(new java.util.Date(1000 * (long) character.base.create_time));
            deletiontime = (character.base.delete_time <= 0) ? "-" : (new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss")).format(new java.util.Date(1000 * (long) character.base.delete_time));
            lastlogin = (character.base.lastlogin_time <= 0) ? "-" : (new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss")).format(new java.util.Date(1000 * (long) character.base.lastlogin_time));
            world = character.status.worldtag;
            coordinateX = character.status.posx;
            coordinateY = character.status.posy;
            coordinateZ = character.status.posz;
            cubiamount = (new DecimalFormat("#.##")).format((double) character.user.cash / 100);
            cubipurchased = (new DecimalFormat("#.##")).format((double) character.user.cash_add / 100);
            cubibought = (new DecimalFormat("#.##")).format((double) character.user.cash_buy / 100);
            cubiused = (new DecimalFormat("#.##")).format((double) character.user.cash_used / 100);
            cubisold = (new DecimalFormat("#.##")).format((double) character.user.cash_sell / 100);
            pkmode = character.status.invader_state;
            pkinvadertime = character.status.invader_time;
            pkpariahtime = character.status.pariah_time;
            Iterator itr = character.equipment.iterator();
            ObjectMapper mapper = new ObjectMapper();
            while (itr.hasNext()) {
                PwItemWeb item = new PwItemWeb((GRoleInventory) itr.next());
                if (iconPath != null && iconPath.length() > 0) {
                    String pathx = iconPath + "" + item.id + ".data";
                    Path path = Paths.get(pathx);
                    if (Files.exists(path)) {
                        IconItem icon = mapper.readValue(new String(Files.readAllBytes(path)), IconItem.class);
                        item.Description = icon.description;
                        item.name = icon.name;
                        item.Icon = icon.imageb64;
                    } else {
                        IconItem icon = new IconItem();
                        item.Description = icon.description;
                        item.name = icon.name;
                        item.Icon = icon.imageb64;
                    }
                }
                equipment.add(item);
            }
            itr = character.pocket.items.iterator();
            while (itr.hasNext()) {
                PwItemWeb item = new PwItemWeb((GRoleInventory) itr.next());
                if (iconPath != null && iconPath.length() > 0) {
                    String pathx = iconPath + "" + item.id + ".data";
                    Path path = Paths.get(pathx);
                    if (Files.exists(path)) {
                        IconItem icon = mapper.readValue(new String(Files.readAllBytes(path)), IconItem.class);
                        item.Description = icon.description;
                        item.name = icon.name;
                        item.Icon = icon.imageb64;
                    } else {
                        IconItem icon = new IconItem();
                        item.Description = icon.description;
                        item.name = icon.name;
                        item.Icon = icon.imageb64;
                    }
                }
                pocket.add(item);
            }
            itr = character.storehouse.items.iterator();
            while (itr.hasNext()) {
                PwItemWeb item = new PwItemWeb((GRoleInventory) itr.next());
                if (iconPath != null && iconPath.length() > 0) {
                    String pathx = iconPath + "" + item.id + ".data";
                    Path path = Paths.get(pathx);
                    if (Files.exists(path)) {
                        IconItem icon = mapper.readValue(new String(Files.readAllBytes(path)), IconItem.class);
                        item.Description = icon.description;
                        item.name = icon.name;
                        item.Icon = icon.imageb64;
                    } else {
                        IconItem icon = new IconItem();
                        item.Description = icon.description;
                        item.name = icon.name;
                        item.Icon = icon.imageb64;
                    }
                }
                storehouse.add(item);
            }
            itr = character.task.task_inventory.iterator();
            while (itr.hasNext()) {
                PwItemWeb item = new PwItemWeb((GRoleInventory) itr.next());
                if (iconPath != null && iconPath.length() > 0) {
                    String pathx = iconPath + "" + item.id + ".data";
                    Path path = Paths.get(pathx);
                    if (Files.exists(path)) {
                        IconItem icon = mapper.readValue(new String(Files.readAllBytes(path)), IconItem.class);
                        item.Description = icon.description;
                        item.name = icon.name;
                        item.Icon = icon.imageb64;
                    } else {
                        IconItem icon = new IconItem();
                        item.Description = icon.description;
                        item.name = icon.name;
                        item.Icon = icon.imageb64;
                    }
                }
                task_inventory.add(item);
            }
        } catch (Exception e) {
            JHSLogingSystem.LogError(RoleExport.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }

    public RoleExport() {
    }

    public Boolean Save() {
        RoleBean character = null;
        Boolean saved = false;
        try {
            character = GameDB.get(RoleId);
            character.base.id = RoleId;
            character.status.level = Level;// = character.status.level;
            character.status.level2 = Level2;// = character.status.level2;
            character.status.reputation = reputation;
            character.ep.max_hp = health;
            character.ep.max_mp = mana;
            character.status.exp = exp;
            character.status.sp = spirit;// = character.status.sp;
            character.ep.max_ap = vigor;// = character.ep.max_ap;
            character.base.race = race;// = character.base.race;
            character.base.cls = occupation;// = character.base.cls;
            character.base.gender = gender;// = character.base.gender;
            character.base.spouse = spouse;// = character.base.spouse;
            character.base.status = status;// = character.base.status;
            character.status.pp = attribute;// = character.status.pp;            
            character.status.posx = coordinateX;
            character.status.posy = coordinateY;
            character.status.posz = coordinateZ;
            character.status.worldtag = world;
            character.status.invader_state = pkmode;// = character.status.invader_state;
            character.status.invader_time = pkinvadertime;// = character.status.invader_time;
            character.status.pariah_time = pkpariahtime;// character.status.pariah_time;
            character.pocket.money = pock_money;
            character.storehouse.money = bank_money;
            if ("0".equals(deletiontime)) {
                character.base.delete_time = 0;
            }
            character.equipment.clear();
            for (PwItemWeb item : equipment) {
                character.equipment.add(item.Convert());
            }
            character.pocket.items.clear();
            for (PwItemWeb item : pocket) {
                character.pocket.items.add(item.Convert());
            }
            character.storehouse.items.clear();
            for (PwItemWeb item : storehouse) {
                character.storehouse.items.add(item.Convert());
            }
            character.task.task_inventory.clear();
            for (PwItemWeb item : task_inventory) {
                character.task.task_inventory.add(item.Convert());
            }
            saved = true;
        } catch (Exception e) {
            JHSLogingSystem.LogException(RoleExport.class.getName(), ExceptionUtils.getStackTrace(e), e);
            saved = false;
        }

        if (character != null && saved) {
            try {
                saved = GameDB.update(character);
            } catch (Exception e) {
                JHSLogingSystem.LogException(RoleExport.class.getName(), ExceptionUtils.getStackTrace(e), e);
                saved = false;
            }
        }
        return saved;
    }

}
