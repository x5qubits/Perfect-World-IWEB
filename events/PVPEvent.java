/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package events;


import com.goldhuman.Common.Marshal.MarshalException;
import com.goldhuman.Common.Octets;
import customProtocol.model.ChatBroadCast;
import customProtocol.model.GameSocketConnect;
import customProtocol.model.SysSendMail;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhs.java.classes.Player;
import jhs.java.manager.IwebManager;
import jhs.java.manager.StatisticManager;
import protocol.DeliveryDB;
import protocol.GRoleInventory;
import protocol.GameDB;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import jhs.java.manager.DataBaseManager;
import jhs.java.manager.JHSLogingSystem;
import protocol.RoleBean;

/**
 *
 * @author IcyTeck
 */
public class PVPEvent {
    public static boolean isStartedbyScheduled = false;
    public static boolean isStarted = false;
    public static boolean canRegister = false;
    public static HashMap<Integer, Player> players = new HashMap<>();
    public static Integer defaultMapId = 205;
    public static Integer eventDuration = 3;
    public static Integer eventDuration_keep = 3;
    public static String[][] pvpitems;

    public static void parse(Player killer, Player pray) {
        if (PVPEvent.isStarted) {
            if (PVPEvent.players.containsKey(killer.RoleId)) {
                Player p = PVPEvent.players.get(killer.RoleId);
                if(Objects.equals(p.worldtag, PVPEvent.defaultMapId))
                {
                   p.kills++;
                   PVPEvent.players.put(p.RoleId, p);              
                }
            }
            if (PVPEvent.players.containsKey(pray.RoleId)) {
                Player p = PVPEvent.players.get(pray.RoleId);
                if(Objects.equals(p.worldtag, PVPEvent.defaultMapId))
                {
                   p.killed++;
                   PVPEvent.players.put(p.RoleId, p);             
                }
            }
        }
    }
   
    public static void start() {
        PVPEvent.players = new HashMap<>();
        JHSLogingSystem.LogInfo(PVPEvent.class.getName(), "PVP Event PRESTART - ITEMS:"+ pvpitems.length);
        PVPEvent.canRegister = true;  
        PVPEvent.Broacast("Dragon Slayer PVP event will start in 2 minutes, registration is now open. To join enter West Ciry Arena!", (byte) 9);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        PVPEvent.Broacast("To join Dragon Slayer PVP event, once you are in Arena RELOG then write @ready in chat and hit enter!", (byte) 9);
        try {
            Thread.sleep(55000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        PVPEvent.Broacast("The Dragon Slayer PVP event will last " + PVPEvent.eventDuration_keep + " minutes in this " + PVPEvent.eventDuration_keep + " minutes you have to kill whoever you find!", (byte) 9);
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        PVPEvent.Broacast("The Dragon Slayer PVP event is (Free for all) so no rules.", (byte) 9);

        try {
            Thread.sleep(55000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        PVPEvent.Broacast("Dragon Slayer PVP Event event has started!", (byte) 9);
        PVPEvent.isStarted = true;
        try {
            Thread.sleep(5000);
        } catch (InterruptedException ex) {
            Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
        PVPEvent.delay_rewards();
    }

    public static void delay_rewards() {
        PVPEvent.canRegister = false;
        PVPEvent.eventDuration = PVPEvent.eventDuration_keep;
        final Timer autoNotifiyMe = new Timer("pvpTimer");
        final TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
              
                if (PVPEvent.isStarted) {
                    if (PVPEvent.eventDuration <= PVPEvent.eventDuration_keep && PVPEvent.eventDuration > 0) {
                        PVPEvent.Broacast("Dragon Slayer PVP Event will end in " + PVPEvent.eventDuration + " minutes.", (byte) 13);
                    }else{
                        PVPEvent.isStarted = false;
                        PVPEvent.givePrizeAndStop();
                    }
                    JHSLogingSystem.LogInfo(PVPEvent.class.getName(), "PVP Event UPDATE - Total Players:"+players.size());
                    PVPEvent.eventDuration--;
                } else {
                    try{
                        autoNotifiyMe.cancel();
                        autoNotifiyMe.purge();
                    }
                    catch(Exception P)
                    {

                    }                      
                }

            }
        };
        autoNotifiyMe.schedule(task1, 0l, StatisticManager.Instance.ONE_MINUTE_MILISECONDS);
        JHSLogingSystem.LogInfo(PVPEvent.class.getName(), "PVP Event STARTED.");
    }

    public static void register(Player killer) {
        
        if (PVPEvent.isStarted || PVPEvent.canRegister) {
            if (!PVPEvent.players.containsKey(killer.RoleId)) {
                try {
                    Boolean hasbennupdate = false;
                    RoleBean character;
                    character = GameDB.get(killer.RoleId);//e(killer.RoleId);
                    if(character == null)
                    {
                        PVPEvent.sendPM(killer.getName() + " Please RELOG then write @ready in chat and hit enter!", killer.RoleId);
                        return;
                    }
                        Player ui = new Player();
                        ui.RoleId = killer.RoleId;
                        ui.UserId = killer.UserId;
                        ui.Name = killer.Name;
                        ui.Level = character.status.level;
                        ui.cls = killer.cls;
                        ui.kills = 0;
                        ui.killed = 0;
                        long timeNow = System.currentTimeMillis() / 1000;
                        killer.loginTime = timeNow;                        
                        killer.IsOnline = 1;
                        ui.worldtag = character.status.worldtag;
                        killer.Save();
                    if (Objects.equals(ui.worldtag, PVPEvent.defaultMapId)) {
                        hasbennupdate = true;
                    } else {
                        PVPEvent.sendPM(ui.getName() + " Please RELOG then write @ready in chat and hit enter!", ui.RoleId);
                        return;
                    }
                    if (hasbennupdate) {
                        PVPEvent.battleNotice(killer.getName() + " joined the Dragon Slayer PVP Event!");
                        PVPEvent.players.put(killer.RoleId, ui);
                        JHSLogingSystem.LogInfo(PVPEvent.class.getName(), "PVP Event UPDATE - Player["+ui.RoleId+"]["+ui.worldtag+"], Total Players:"+players.size());
                    }
                } catch (MarshalException ex) {
                    Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
                } catch (Exception ex) {
                    Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                PVPEvent.PMUser("You are already registered.", killer.RoleId);
            }
            } else {
                PVPEvent.PMUser("PvP Event NOT Started or register period is over.", killer.RoleId);
            }

    }
    
    private static void sendPM(String msg, Integer roleIdI) {
        try {
            DeliveryDB.replyComplain(roleIdI, IwebManager.Instance.MainGMID, IwebManager.Instance.MainName, msg);
        } catch (Exception e) {
        }
    }
    
    private static List<Entry<Integer, Player>> sortByComparator(HashMap<Integer, Player> unsortMap) {

        List<Entry<Integer, Player>> list = new LinkedList<>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<Integer, Player>>()
        {
            @Override
            public int compare(Entry<Integer, Player> s1, Entry<Integer, Player> s2)
            {
                return Integer.compare(s2.getValue().kills, s1.getValue().kills);
            }
        });

        return list;
    }
    
    public static void givePrizeAndStop() {
            List<Entry<Integer, Player>> vplayers = null;
            try{            
               vplayers = sortByComparator(PVPEvent.players);   
               JHSLogingSystem.LogInfo(PVPEvent.class.getName(), "PVP Event ENDED WITH:"+vplayers.size()+" winners.");
            }
            catch(Exception e)
            {
                JHSLogingSystem.LogInfo(PVPEvent.class.getName(), e.toString());
            }         
            int rewardId = 0;
            int counter = 0;
            if (vplayers != null && vplayers.size() > 0) {
                    try{
                    for (Entry<Integer, Player> entry : vplayers) {
                    Integer roleId = entry.getValue().RoleId;
                    Player player = entry.getValue();
                    if (player != null && player.kills > 0) {
                        try {
                            if(rewardId < 3) {
                                try{
                                RoleBean character = GameDB.get(player.RoleId);
                                if(character.status.worldtag != PVPEvent.defaultMapId)
                                    continue;
                                }catch(Exception e){}                    
                            }
                            Player pl = DataBaseManager.Instance.GetPlayer(player.RoleId.toString());
                            if(pl != null)
                            {
                                pl.pvpEvent += player.kills;
                                pl.Save();
                            }
                            SysSendMail ssm = new SysSendMail();
                            ssm.receiver = roleId;
                            ssm.setTitle("Event Reward");
                            ssm.setContext("Congratulations, " + player.getName() + "!");
                            ssm.attach_money = 0;
                            GRoleInventory gri = new GRoleInventory();
                            gri.id = Integer.parseInt(PVPEvent.pvpitems[rewardId][0]);
                            gri.guid1 = Integer.parseInt(PVPEvent.pvpitems[rewardId][6]);
                            gri.guid2 = Integer.parseInt(PVPEvent.pvpitems[rewardId][7]);
                            gri.mask = Integer.parseInt(PVPEvent.pvpitems[rewardId][3]);
                            gri.proctype = Integer.parseInt(PVPEvent.pvpitems[rewardId][8]);
                            gri.pos = 0;
                            gri.count = Integer.parseInt(PVPEvent.pvpitems[rewardId][4]);
                            gri.max_count = Integer.parseInt(PVPEvent.pvpitems[rewardId][5]);
                            gri.expire_date = Integer.parseInt(PVPEvent.pvpitems[rewardId][9]);
                            gri.data = new Octets(hextoByteArray(PVPEvent.pvpitems[rewardId][2]));
                            ssm.attach_obj = gri;
                            GameSocketConnect.Send(ssm);
                            String msg1 = "";
                            switch (counter) {
                                case 0:
                                    msg1 = "" + player.getName() + " brutally occupies first place in the PvP event with " + player.kills + " kills his reward is: " + PVPEvent.pvpitems[rewardId][4] + "x " + PVPEvent.pvpitems[rewardId][1] + "!";
                                    rewardId++;
                                    break;
                                case 1:
                                    msg1 = "" + player.getName() + " violently takes second place with " + player.kills + " Kills his reward is: " + PVPEvent.pvpitems[rewardId][4] + "x " + PVPEvent.pvpitems[rewardId][1] + "!";
                                    rewardId++;
                                    break;
                                case 2:
                                    msg1 = "" + player.getName() + " earned third place with " + player.kills + " Kills his reward is: " + PVPEvent.pvpitems[rewardId][4] + "x " + PVPEvent.pvpitems[rewardId][1] + "!";
                                    rewardId++;
                                    break;
                                default:
                                    msg1 = "" + player.getName() + " earned to #" + counter + "th place with " + player.kills + " Kills his reward is: " + PVPEvent.pvpitems[rewardId][4] + "x " + PVPEvent.pvpitems[rewardId][1] + "!";
                                    break;
                            }
                            counter++;
                            PVPEvent.Broacast(msg1, (byte) 9);
                            Thread.sleep(2000);
                        } catch (IOException | InterruptedException | NumberFormatException ex) {
                             JHSLogingSystem.LogInfo(PVPEvent.class.getName(), ex.toString());
                        }
                    }
                }
                if (rewardId > 0) {
                    PVPEvent.Broacast("Congratulations to all!", (byte) 9);
                }
                }catch(Exception e)
                {
                    JHSLogingSystem.LogInfo(PVPEvent.class.getName(), e.toString());
                }                
            }else{
                 PVPEvent.Broacast("Dragon Slayer PVP event is now over.", (byte) 9);
                 JHSLogingSystem.LogInfo(PVPEvent.class.getName(), "PVP Event WITHOUT ANY WINNERS.");
            }

        PVPEvent.isStarted = false;
        PVPEvent.isStartedbyScheduled = false;
        PVPEvent.canRegister = false;
        PVPEvent.players = new HashMap<>();
    }

    public static void battleNotice(String msg) {
        ChatBroadCast chc;
        try {
            chc = new ChatBroadCast(msg);
            chc.channel = 13;
            chc.srcroleid = 0;
            GameSocketConnect.Send(chc);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PVPEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private static void PMUser(String msg, int roleIdI) {
        try {
            DeliveryDB.replyComplain(roleIdI, IwebManager.Instance.MainGMID, IwebManager.Instance.MainName, msg);
        } catch (Exception e) {
        }
    }
    
    private static void Broacast(String msg, byte chan) {
        ChatBroadCast chc;
        try {
            chc = new ChatBroadCast(msg);
            chc.channel = chan;
            chc.srcroleid = 0;
            GameSocketConnect.Send(chc);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Trivia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Trivia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static byte[] hextoByteArray(String x) {
        if (x.length() < 2) {
            return new byte[0];
        }
        if (x.length() % 2 != 0) {
            System.err.println("hextoByteArray error! hex size=" + Integer.toString(x.length()));
        }
        byte[] rb = new byte[x.length() / 2];
        for (int i = 0; i < rb.length; ++i) {
            rb[i] = 0;

            int n = x.charAt(i + i);
            if ((n >= 48) && (n <= 57)) {
                n -= 48;
            } else if ((n >= 97) && (n <= 102)) {
                n = n - 97 + 10;
            }
            rb[i] = (byte) (rb[i] | n << 4 & 0xF0);

            n = x.charAt(i + i + 1);
            if ((n >= 48) && (n <= 57)) {
                n -= 48;
            } else if ((n >= 97) && (n <= 102)) {
                n = n - 97 + 10;
            }
            rb[i] = (byte) (rb[i] | n & 0xF);
        }
        return rb;
    }
}
