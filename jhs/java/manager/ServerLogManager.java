/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import customProtocol.model.Quotes;
import com.goldhuman.Common.Conf;
import com.goldhuman.Common.Octets;
import customProtocol.model.ChatBroadCast;
import customProtocol.model.GameSocketConnect;
import customProtocol.model.SysSendMail;
import events.PVPEvent;
import events.Trivia;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhs.java.classes.ChatMessage;
import jhs.java.classes.LimitedQueue;
import jhs.java.classes.Player;
import jhs.java.mysql.JHSMySQL;
import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import protocol.GRoleInventory;

/**
 *
 * @author IcyTeck
 */
public class ServerLogManager {

    public static final ServerLogManager Instance = new ServerLogManager();
    public String fd_log;
    public String fd_formatlog;
    public String fd_chat;
    public String fd_trace;
    public LimitedQueue<String> ChatMessages = new LimitedQueue<>(1000);
    private boolean parse_fd_chat;
    private boolean parse_fd_formatlog;
    private boolean parse_fd_trace;
    private boolean parse_fd_log;
    private ConfigManager configManager = null;
    private DataBaseManager DB = null;
    private ItemsManager itemsManager = null;
    private IwebManager iwebManager = null;
    private StatisticManager statisticManager = null;
    private String exchangepoints;
    private String checkpoints;
    private String areyouthere;
    private boolean Inited = false;
    public static boolean TRIVIA = false;
    public static boolean DQSYSTEM = false;
    
    public ServerLogManager() {
    }

    public void Init() {
        fd_log = Conf.GetInstance().find("LOGSERVICE", "fd_log");
        fd_formatlog = Conf.GetInstance().find("LOGSERVICE", "fd_formatlog");
        fd_chat = Conf.GetInstance().find("LOGSERVICE", "fd_chat");
        fd_trace = Conf.GetInstance().find("LOGSERVICE", "fd_trace");
        Boolean clearLogs = "true".equals(Conf.GetInstance().find("OTHER_CONFIGS", "clear_logs_startup"));


        parse_fd_chat = "true".equals(Conf.GetInstance().find("OTHER_CONFIGS", "parse_fd_chat").toLowerCase());
        parse_fd_formatlog = "true".equals(Conf.GetInstance().find("OTHER_CONFIGS", "parse_fd_formatlog").toLowerCase());
        parse_fd_trace = "true".equals(Conf.GetInstance().find("OTHER_CONFIGS", "parse_fd_trace").toLowerCase());
        parse_fd_log = "true".equals(Conf.GetInstance().find("OTHER_CONFIGS", "parse_fd_log").toLowerCase());

        configManager = ConfigManager.Instance;
        DB = DataBaseManager.Instance;

        try {
            areyouthere = Base64.encode(configManager.GetSettings(false).get(17).value.getBytes("UTF-16LE"));
            checkpoints = Base64.encode(configManager.GetSettings(false).get(18).value.getBytes("UTF-16LE"));
            exchangepoints = Base64.encode(configManager.GetSettings(false).get(19).value.getBytes("UTF-16LE"));
        } catch (Exception e) {
            JHSLogingSystem.LogException(ServerLogManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        itemsManager = ItemsManager.Instance;
        iwebManager = IwebManager.Instance;
        statisticManager = StatisticManager.Instance;
        if (clearLogs) {
            clear();
        }        
        
        
        if (parse_fd_chat) {
            startReadingThread(fd_chat, false, "", "worldChat", true, 1);
        }

        if (parse_fd_formatlog) {
            startReadingThread(fd_formatlog, false, "", "formatLog", false, 2);
        }

        if (parse_fd_trace) {
            startReadingThread(fd_trace, false, "", "tranceLog", false, 3);
        }

        if (parse_fd_log) {
            startReadingThread(fd_log, true, "GB18030", "worldLog", false, 4);
        }

        JHSLogingSystem.LogInfo(ServerLogManager.class.getName(), "ServerLogManager - Started with: parse_fd_chat[" + parse_fd_chat + "], parse_fd_formatlog[" + parse_fd_formatlog + "], parse_fd_trace[" + parse_fd_trace + "], parse_fd_log[" + parse_fd_log + "].");
        Inited = true;
    }

    private void startReadingThread(final String Path, final boolean hasEncoding, final String encoding, String threadName, final boolean hasEventFile, final int logId) {
        Thread universalFileLog_thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    universalFileLog(Path, hasEncoding, encoding, hasEventFile, logId);
                } catch (Exception e) {
                    Logger.getLogger(ServerLogManager.class.getName()).log(Level.SEVERE, null, e);
                }
            }
        });
        universalFileLog_thread.setName(threadName);
        universalFileLog_thread.setDaemon(true);
        universalFileLog_thread.start();
    }

    private void universalFileLog(final String Path, final boolean hasEncoding, final String encoding, final boolean hasEventFile, final int logId) {
        try (FileWriter fwOb = new FileWriter(Path, false);
                PrintWriter pwOb = new PrintWriter(fwOb, false)) {
            pwOb.flush();
        } catch (Exception e) {
        }
        final ServerLogManager serverLogManager = ServerLogManager.Instance;
        long _filePointer = 0l;
        File _file = new File(Path);
        try {
            while (true) {
                Thread.sleep(100);
                long len = _file.length();
                if (len < _filePointer) {
                    // Log must have been jibbled or deleted.
                    _filePointer = len;
                } else if (len > _filePointer) {
                    // File must have had something added to it!
                    RandomAccessFile raf = new RandomAccessFile(_file, "r");
                    raf.seek(_filePointer);
                    String line = null;
                    while ((line = raf.readLine()) != null) {
                        if (line != null) {
                            if (hasEventFile) {
                                serverLogManager.AddChat(line);

                            } else {
                                if (hasEncoding) {
                                    line = new String(line.getBytes("ISO-8859-1"), encoding);
                                }
                                serverLogManager.ProcessLine(line, logId);
                            }
                        }
                    }
                    _filePointer = raf.getFilePointer();
                    raf.close();
                }
            }
        } catch (Exception e) {
            JHSLogingSystem.LogInfo(ServerLogManager.class.getName(), "Fatal error reading log file, log tailing has stopped.");
        }
    }

    public void clear() {
        if (parse_fd_trace) {
            try (FileWriter fwOb = new FileWriter(fd_trace, false);
                    PrintWriter pwOb = new PrintWriter(fwOb, false)) {
                pwOb.flush();
            } catch (Exception e) {

            }
        }
        if (parse_fd_chat) {
            try (FileWriter fwOb = new FileWriter(fd_chat, false);
                    PrintWriter pwOb = new PrintWriter(fwOb, false)) {
                pwOb.flush();
            } catch (Exception e) {

            }
        }
        if (parse_fd_formatlog) {
            try (FileWriter fwOb = new FileWriter(fd_formatlog, false);
                    PrintWriter pwOb = new PrintWriter(fwOb, false)) {
                pwOb.flush();
            } catch (Exception e) {

            }
        }
        if (parse_fd_log) {
            try (FileWriter fwOb = new FileWriter(fd_log, false);
                    PrintWriter pwOb = new PrintWriter(fwOb, false)) {
                pwOb.flush();
            } catch (Exception e) {

            }
        }
    }

    public void AddChat(String line) {
        ChatMessages.add(line);
        Trivia.parse(line);
        try {
            if (line.contains(areyouthere)) {
                ChatMessage cm = new ChatMessage(line, false);
                Player userRequesting = DB.GetPlayer(cm.SRC);
                if (userRequesting != null) {
                    String quote = Quotes.getRQuote();
                    if(quote.contains("@")){
                       quote = quote.replace("@", userRequesting.getName());
                    }
                    if(quote.contains("#")){
                        quote = quote.replace("#", new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date())); 
                     }
                    long timeNow = System.currentTimeMillis() / 1000;
                    userRequesting.loginTime = timeNow;
                    userRequesting.IsOnline = 1;      
                    userRequesting.Save();
                    iwebManager.SendChat(quote, (byte) 4, Integer.parseInt(cm.SRC));
                }
            }
            if(PVPEvent.isStarted || PVPEvent.canRegister)
            {
                ChatMessage cm = new ChatMessage(line, false);
                if (cm.GetMessage().toLowerCase().contains("@ready")) {
                    Player userRequesting = DB.GetPlayer(cm.SRC);
                    if (userRequesting != null) {
                       PVPEvent.register(userRequesting);
                    }
                } 
            }
            if (ServerLogManager.DQSYSTEM && line.contains(checkpoints)) {
                ChatMessage cm = new ChatMessage(line, false);
                Player player = DB.GetPlayer(cm.SRC);
                if (player != null) {
                    int roleIdI = Integer.parseInt(cm.SRC);
                    iwebManager.SendChat("You have: " + player.dqpoints_que + " DQ points.", (byte) 4, roleIdI);
                }
            }
            if (ServerLogManager.DQSYSTEM && line.contains(exchangepoints)) {
                ChatMessage cm = new ChatMessage(line, false);
                Player userRequesting = DB.GetPlayer(cm.SRC);
                if (userRequesting != null) {
                    int minPoints = Integer.parseInt(configManager.settings.get(16).value);
                    if (userRequesting.dqpoints_que >= minPoints) {
                        JHSMySQL mysql = JHSMySQL.getInstance();
                        ConfigManager configsx = ConfigManager.Instance;
                        int multiplier = Integer.parseInt(configManager.settings.get(20).value);
                        mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.PWDBNAME);
                        int poins_to_add = userRequesting.dqpoints_que / multiplier;
                        String query = configManager.settings.get(15).value;
                        if(query.startsWith("UPDATE"))
                        {
                            mysql.Query(query, poins_to_add + "", userRequesting.UserId +"");
                        }
                        else
                        {
                            mysql.Query(query, userRequesting.UserId + "", poins_to_add + "");
                        }
                        mysql.close();
                        userRequesting.dqpoints += userRequesting.dqpoints_que;
                        userRequesting.dqpoints_que = 0;
                        userRequesting.Save();
                        iwebManager.SendChat("We sent:" + poins_to_add + " boutique silver. Check your boutique after a few minutes.", (byte) 4, Integer.parseInt(cm.SRC));
                    } else {
                        iwebManager.SendChat("You need to have minimum " + minPoints + " DQ points.", (byte) 4, Integer.parseInt(cm.SRC));
                    }
                }
            }
            
            if (configManager.pvp_player_Hunting_function && line.contains(configManager.pvp_player_Hunting_function_checkPoints)) {
                ChatMessage cm = new ChatMessage(line, false);
                Player player = DB.GetPlayer(cm.SRC);
                if (player != null) {
                    int roleIdI = Integer.parseInt(cm.SRC);
                    long timeNow = System.currentTimeMillis() / 1000;
                    player.loginTime = timeNow;
                    player.IsOnline = 1;
                    player.Save();
                    iwebManager.SendChat("You have: " + player.PVPPoints + " PVP points.", (byte) 4, roleIdI);
                }
            }            
            if (configManager.pvp_player_Hunting_function && line.contains(configManager.pvp_player_Hunting_function_getReward)) {
                ChatMessage cm = new ChatMessage(line, false);
                Player userRequesting = DB.GetPlayer(cm.SRC);
                if (userRequesting != null) {
                    int minPoints = configManager.pvp_player_Hunting_function_minimum_Kills;
                    if (userRequesting.PVPPoints >= minPoints) {
                        Boolean mailSend = false;
                        try{
                            SysSendMail ssm = new SysSendMail();
                            ssm.receiver = userRequesting.RoleId;
                            ssm.setTitle("PvP Reward");
                            ssm.setContext("Congratulations, " + userRequesting.getName() + "!");
                            ssm.attach_money = 0;                            
                            GRoleInventory gri = new GRoleInventory();
                            gri.id = Integer.parseInt(configManager.pvp_player_Hunting_function_item[0]);
                            gri.guid1 = Integer.parseInt(configManager.pvp_player_Hunting_function_item[6]);
                            gri.guid2 = Integer.parseInt(configManager.pvp_player_Hunting_function_item[7]);
                            gri.mask = Integer.parseInt(configManager.pvp_player_Hunting_function_item[3]);
                            gri.proctype = Integer.parseInt(configManager.pvp_player_Hunting_function_item[8]);
                            gri.pos = 0;
                            gri.count = Integer.parseInt(configManager.pvp_player_Hunting_function_item[4]);
                            gri.max_count = Integer.parseInt(configManager.pvp_player_Hunting_function_item[5]);
                            gri.expire_date = Integer.parseInt(configManager.pvp_player_Hunting_function_item[9]);
                            gri.data = new Octets(hextoByteArray(configManager.pvp_player_Hunting_function_item[2]));
                            ssm.attach_obj = gri;
                            GameSocketConnect.Send(ssm);
                            mailSend = true;
                        }catch(Exception asdasd)
                        {
                            mailSend = false;
                        }
                        if(mailSend) {
                            userRequesting.PVPPoints = 0;
                            userRequesting.Save();
                            iwebManager.SendChat("You got mail:" +  configManager.pvp_player_Hunting_function_item[4] + "x "+configManager.pvp_player_Hunting_function_item[1]+". Happy Hunting!", (byte) 4, Integer.parseInt(cm.SRC));
                        }else{
                           iwebManager.SendChat("System is buzzy atm please try again later!", (byte) 4, Integer.parseInt(cm.SRC)); 
                        }
                    } else {
                        iwebManager.SendChat("You need to have minimum " + minPoints + " PVP points.", (byte) 4, Integer.parseInt(cm.SRC));
                    }
                }
            }            
        } catch (Exception e) {
            JHSLogingSystem.LogException(ServerLogManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }

    }
    
    private byte[] hextoByteArray(String x) {
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
    
    public ArrayList<ChatMessage> GetChats(int show_lines) {
        ArrayList<ChatMessage> all_datas = new ArrayList<>();
        if(!Inited)
        {
            return all_datas;
        }
        if (parse_fd_chat) {
            int i = ChatMessages.size() - 1;
            while (i > ChatMessages.size() - show_lines && i >= 0) {
                all_datas.add(new ChatMessage(ChatMessages.get(i), true));
                i--;
            }
        } else {
            BufferedReader br;
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(fd_chat)));
                String line;
                while ((line = br.readLine()) != null) {
                    all_datas.add(new ChatMessage(line, true));
                }
                int i = ChatMessages.size() - 1;
                while (i > ChatMessages.size() - show_lines && i >= 0) {
                    all_datas.add(new ChatMessage(ChatMessages.get(i), true));
                    i--;
                }
            } catch (Exception e) {
                JHSLogingSystem.LogException(ServerLogManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
            }
        }
        return all_datas;
    }

    public void CheckLogin(String line) {
        if (!configManager.record_analytics) {
            return;
        }
        try {
            if (line.contains("formatlog") && line.contains("rolelogin") && line.contains("userid")) {
                String userId = StringUtils.substringBetween(line, "userid=", ":");
                if (userId != null) {
                    // String IP = line.substring(line.indexOf("peer=") + 5, line.length());
                    statisticManager.UpdateStatistic(userId, 0, true);
                }
                if(configManager.record_player_level)
                {
                    String roleId = StringUtils.substringBetween(line, "roleid=", ":");
                    if(roleId != null)
                    {
                        Player userRequesting = DB.GetPlayer(roleId);
                        if(userRequesting != null)
                        {
                            long timeNow = System.currentTimeMillis() / 1000;
                            userRequesting.loginTime = timeNow;
                            userRequesting.IsOnline = 1;
                            userRequesting.Save();
                        }
                    }                    
                }
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(ServerLogManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }

    public void CheckLogOut(String line) {
        if (!configManager.record_analytics) {
            return;
        }
        if (!configManager.record_analytics) {
            return;
        }
        try {
            if (line.contains("formatlog") && line.contains("rolelogout") && line.contains("userid")) {
                String userid = StringUtils.substringBetween(line, ":userid=", ":");
                if (userid != null) {
                    String stime = StringUtils.substringBetween(line, ":time=", ":");
                    if (stime != null) {
                        int PlayTime = Integer.parseInt(stime);
                        statisticManager.UpdateStatistic(userid, PlayTime, false);
                    }
                }          
                  if(configManager.record_player_level)
                  {
                      String player1 = StringUtils.substringBetween(line, ":roleid=", ":");
                      if(player1 != null)
                      {                        
                          Player userRequesting = DB.GetPlayer(player1);
                          if(userRequesting != null)
                          {
                              if(userRequesting.loginTime > 0)
                              {
                                long timenow =  System.currentTimeMillis() / 1000;
                                long total = timenow - userRequesting.loginTime;
                                userRequesting.PlayTime += total;
                              }
                              userRequesting.IsOnline = 0;
                              userRequesting.Save();
                          }
                      }                    
                  }   
            }
            
        } catch (Exception e) {
            JHSLogingSystem.LogException(ServerLogManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }

    public void CheckSoldItem(String line) {
        if (!configManager.record_player_sell) {
            return;
        }
        try {
            if (line.contains("用户") && line.contains("卖店") && line.contains("个")) {
                String prepare = line.substring(line.indexOf("用户"), line.length());
                String finalstring = prepare.replace("用户", "").replace("卖店", ",").replace("个", ",");
                String[] finalData = finalstring.split(",");
                String RoleId = finalData[0];
                String itemCount = finalData[1];
                String itemID = finalData[2];
                itemsManager.parse(RoleId, itemID, itemCount);
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(ServerLogManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }

    public void CheckMailItem(String line) {
        if (!configManager.record_player_mail) {
            return;
        }
        try {
            if (line.contains("formatlog") && line.contains("sendmail") && line.contains("src") && line.contains("dst")) {
                String source = StringUtils.substringBetween(line, ":src=", ":");
                String destination = StringUtils.substringBetween(line, ":dst=", ":");
                String item = StringUtils.substringBetween(line, ":item=", ":");
                String itemcount = StringUtils.substringBetween(line, ":count=", ":");
                String money = StringUtils.substringBetween(line, ":money=", ":");
                JHSLogingSystem.LogInfo(ServerLogManager.class.getName(), source + "UNIMPLEMENTED FUNCTION -- sends x" + itemcount + " " + item + " to " + destination + " money:" + money);
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(ServerLogManager.class.getName(),ExceptionUtils.getStackTrace(e), e);
        }
    }

    public void CheckPvP(String line) {
        if (!configManager.record_player_pvp) {
            return;
        }
        try {
            // 0=1=2
            // 1024=258=4625
            if (line.contains("formatlog:die") && line.contains("roleid") && line.contains("attacker")) {
                String prepare = line.substring(line.indexOf("formatlog:die:"));
                String finalstring = prepare.replace("roleid=", "").replace(":type", "").replace(":attacker", "").replace("formatlog:die:", "");
                String[] finalData = finalstring.split("=");

                int type = Integer.parseInt(finalData[1]);
                if (type == 2) {
                    String roleid = finalData[0];
                    String attacker = finalData[2];
                    Player killed = DB.GetPlayer(roleid);//.(roleid, false);
                    Player attaker = DB.GetPlayer(attacker);
                    
                    if (killed != null && attaker != null) {
                        double kd = 0.00d;
                        try {
                            kd = attaker.kills / attaker.killed;
                        } catch (Exception EX) {
                        }
                        PVPEvent.parse(attaker, killed);
                        attaker.kills++;
                        killed.killed++;
                        if(configManager.pvp_player_Hunting_function && configManager.pvp_player_Hunting_function_item != null)
                        {
                            Boolean canContinue = true;                            
                            for(int i = 0; i < configManager.pvp_player_Hunting_function_restrict_worldIds.length; i++) {                                 
                                String worldId = configManager.pvp_player_Hunting_function_restrict_worldIds[i];
                                String worldId2 = attaker.worldtag.toString();
                                if(worldId.equals(worldId2))
                                {
                                    canContinue = false;
                                    break;
                                }
                            }      
                            if(canContinue){
                                Boolean ALTKILLER = false; 
                                for(int i = 0; i < attaker.LastKilledPlayers.length; i++) {
                                    if(attaker.LastKilledPlayers[i] != null && attaker.LastKilledPlayers[i] == killed.RoleId) {
                                        ALTKILLER = true; 
                                        break;
                                    }
                                }
                                if(!ALTKILLER) {
                                    ArrayUtils.reverse(attaker.LastKilledPlayers);
                                    attaker.LastKilledPlayers[0] = killed.RoleId;
                                    attaker.PVPPoints++;
                                }
                            }
                        }
                        String debug = Quotes.getQuote().replace("@", killed.getName()).replace("#", attaker.getName());
                        DecimalFormat df = new DecimalFormat("0.00##");
                        String result = df.format(kd);
                        debug += " [" + attaker.getName() + " Kills:" + attaker.kills + ", Deaths:" + attaker.killed + " | K/D:" + result + "]";
                        killed.Save();
                        attaker.Save();
                        ChatBroadCast chc = new ChatBroadCast(debug);
                        chc.channel = 6;
                        chc.srcroleid = 0;
                        GameSocketConnect.Send(chc);
                    }
                }
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(ServerLogManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }

    public void ProcessLine(String line, int logId) {
//        /OtherLogs.add(line);
        //System.out.println(logId);
        switch (logId) {
            case 1://worldChat
                break;
            case 2://formatLog
                CheckLogin(line);
                CheckLogOut(line);
                CheckMailItem(line);
                CheckPvP(line);
                break;
            case 3://tranceLog

                break;
            case 4://worldLog
                CheckSoldItem(line);
                break;
        }
    }
}
