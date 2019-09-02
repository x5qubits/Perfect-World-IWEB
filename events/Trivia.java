/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package events;

import jhs.java.manager.ItemsManager;
import customProtocol.model.ChatBroadCast;
import customProtocol.model.GameSocketConnect;
import customProtocol.model.SysSendMail;
import it.sauronsoftware.cron4j.Scheduler;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import jhs.java.classes.Player;
import jhs.java.classes.PwItem;
import jhs.java.classes.QuestionData;
import jhs.java.manager.ConfigManager;
import jhs.java.manager.DataBaseManager;
import jhs.java.manager.JHSLogingSystem;
import jhs.java.manager.IwebManager;
import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import protocol.DeliveryDB;

/**
 *
 * @author IcyTeck
 */
public class Trivia {

    public static volatile boolean isEventTime = false;
    public static volatile boolean questionAnswered = false;

    public static String question = null;
    public static String answer = null;
    public static int coinsReward = 0;
    public static boolean isCoinsRewards = false;
    public static boolean IsRanking = true;
    public static PwItem rewardId = null;
    public static String[] data = null;
    public volatile static ScheduledThreadPoolExecutor exec;
    public static int CoinsMax;
    public static int CoinsMin;
    public static boolean isScramble = true;

    public static int StartMinute;
    public static int WaitTime;
    public static boolean isItemReward;

    private static ConfigManager configs = null;

    public volatile static boolean firstStart = true;
    public static boolean inited = false;
    public static boolean AllowCoinsAsReward = false;
    public static boolean trivia_force_wordscramble = true;
    
    public static void Init() {
        if (inited) {
            return;
        }
        inited = true;
        try {
            configs = ConfigManager.Instance;
            Trivia.data = configs.GetSettings(false).get(6).value.split(",");
            Trivia.CoinsMax = Integer.parseInt(configs.GetSettings(false).get(9).value);
            Trivia.CoinsMin = Integer.parseInt(configs.GetSettings(false).get(8).value);
            Trivia.AllowCoinsAsReward = Trivia.CoinsMax > 0;
            Trivia.StartMinute = Integer.parseInt(configs.GetSettings(false).get(10).value);
            Trivia.WaitTime = Integer.parseInt(configs.GetSettings(false).get(11).value);
            Trivia.isItemReward = configs.GetSettings(false).get(12).value.equals("true");
            Scheduler scheduler = new Scheduler();
            Runnable runable = new Runnable() {
                @Override
                public void run() {
                    Trivia.startEvent();
                }
            };
            scheduler.schedule("*/" + Trivia.StartMinute + " * * * *", runable);
            scheduler.start();
            Timer timertrivia = new Timer("QANDA_EVENTTrivia");
            TimerTask trivia = new TimerTask() {
                @Override
                public void run() {
                    if (Trivia.firstStart) {
                        Trivia.startEvent();
                    }
                }
            };
            timertrivia.schedule(trivia, 0l, Trivia.StartMinute * 60000);
            JHSLogingSystem.LogInfo(Trivia.class.getName(), "Trivia started with " + Trivia.data.length + " words Scheguled every " + Trivia.StartMinute + " minute with answer time " + Trivia.WaitTime + " seconds.");
            Trivia.firstStart = false;

        } catch (Exception e) {
            JHSLogingSystem.LogError(Trivia.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }

    public static void parse(String line) {
        if (!Trivia.isEventTime) {
            return;
        }
        if (line.contains("Chat")) {
            try {
                String roleId = StringUtils.substringBetween(line, "src=", " ");
                String msg = line.substring(line.indexOf("msg=") + 4);
                int roleIdI = Integer.parseInt(roleId);
                String decoded = new String(Base64.decode(msg));
                if (roleIdI > 0 && line.contains("chl=1")) {
                    decoded = decoded.replace("\u0000", "");
                    String word = decoded.toLowerCase().trim();
                    if ((word == null ? Trivia.answer == null : word.equals(Trivia.answer.toLowerCase().trim())) && !Trivia.questionAnswered) {
                        Player userInfo = DataBaseManager.Instance.GetPlayer(roleId);
                        if (userInfo != null) {
                            if (giveRewardAndStop(roleIdI, userInfo)) {

                                Trivia.questionAnswered = true;
                                Trivia.isEventTime = false;
                                if (Trivia.isCoinsRewards) {
                                    askQuestion("[TRIVIA] The Reward, goes to " + userInfo.getName() + ".", (byte) 1);
                                } else {
                                    askQuestion("[TRIVIA] The Reward, goes to " + userInfo.getName() + ".", (byte) 1);
                                }
                                resetAll();
                            }
                        } else {
                            if (userInfo != null) {
                                askQuestion2(userInfo.getName() + " please try again later.", userInfo.RoleId);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                Logger.getLogger(Trivia.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    public static boolean giveRewardAndStop(int roleId, Player player) {
        try {
            if (Trivia.isCoinsRewards) {
                SysSendMail ssm = new SysSendMail();
                ssm.receiver = roleId;
                ssm.setTitle("[TRIVIA] Reward");
                ssm.setContext("Congratulations, " + player.getName() + "!");
                ssm.attach_money = Trivia.coinsReward;
                GameSocketConnect.Send(ssm);
            } else {
                SysSendMail ssm = new SysSendMail();
                ssm.receiver = roleId;
                ssm.setTitle("[TRIVIA] Reward");
                ssm.setContext("Congratulations, " + player.getName() + "!");
                ssm.attach_money = 0;
                //GRoleInventory gri = new GRoleInventory();
                ssm.attach_obj = Trivia.rewardId.Convert();
                GameSocketConnect.Send(ssm);
            }
            if (Trivia.IsRanking) {
                player.eventpoints += 1;
                long timeNow = System.currentTimeMillis() / 1000;
                player.loginTime = timeNow;                        
                player.IsOnline = 1;
                player.Save();
            }
            return true;
        } catch (Exception e) {
            Logger.getLogger(Trivia.class.getName()).log(Level.SEVERE, null, e);
            return false;
        }
    }

    public static void startEvent() {
        if (Trivia.isEventTime) {
            return;
        }
        if (!configs.canRunTrivia) {
            return;
        }
        Trivia.isEventTime = true;
        try {
            resetAll();
            Trivia.isEventTime = true;
            Random r = new Random();
            Trivia.coinsReward = r.nextInt(Trivia.CoinsMax - Trivia.CoinsMin) + Trivia.CoinsMin;
            
            if (Trivia.isScramble || Trivia.trivia_force_wordscramble) {
                Random generator = new Random();
                int num1;
                int rewardTypex = new Random().nextInt(Trivia.data.length);
                String phrase = Trivia.data[rewardTypex].toLowerCase().trim();
                int len = phrase.length();
                char c = ' ';
                while (c == ' ') {
                    num1 = generator.nextInt(len);
                    c = phrase.charAt(num1);
                }
                String mut1 = phrase.replace(c, '*');
                Trivia.question = "[TRIVIA] Guess this word: " + mut1;
                Trivia.isScramble = false;
                Trivia.answer = phrase;
            } else {
                Trivia.rewardId = ItemsManager.Instance.GetRandomItem();
                QuestionData queData = ItemsManager.Instance.GetRandomQuestion();
                Trivia.question = "[TRIVIA] " + queData.Question;
                Trivia.answer = queData.Answer.trim().toLowerCase();
                Trivia.isScramble = true;
            }
            //askQuestion(" ==== Trivia Event Started. ==== ");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Trivia.class.getName()).log(Level.SEVERE, null, ex);
            }
            if (Trivia.WaitTime >= 60) {
                int minute = Trivia.WaitTime / 60;
                if(minute > 1){
                    //askQuestion("You have " + minute + " minutes to answer the question:");
                } else{
                   // askQuestion("You have " + minute + " minute to answer the question:");
                }
            } else {
                //askQuestion("You have " + Trivia.WaitTime + " seconds to answer the question:");
            }

            int[] rewardTypeArry = null; //0 == coins 1 == item

            if (!Trivia.isItemReward) {
                rewardTypeArry = new int[]{0, 0, 0, 0, 0, 0};
            } else {
                rewardTypeArry = new int[]{0, 1, 0, 1, 1, 1};
            }

            int rewardType = new Random().nextInt(rewardTypeArry.length);
            if (rewardTypeArry[rewardType] == 0 && Trivia.AllowCoinsAsReward) {
                Trivia.isCoinsRewards = true;
                askQuestion(question);
            } else {
                Trivia.isCoinsRewards = false;
                Trivia.rewardId = ItemsManager.Instance.GetRandomItem();
                askQuestion(question);
            }

            try {
                Trivia.exec.shutdownNow();
            } catch (Exception exxa) {
            }

            Trivia.exec = new ScheduledThreadPoolExecutor(1);
            Trivia.exec.schedule(new Runnable() {
                @Override
                public void run() {
                    Trivia.stopRoundNoAnswer();
                }
            }, (long) Trivia.WaitTime * 1000, TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            Logger.getLogger(Trivia.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static void askQuestion(String msg) {
        ChatBroadCast chc;
        try {
            chc = new ChatBroadCast(msg);
            chc.channel = 9;
            chc.srcroleid = 0;
            GameSocketConnect.Send(chc);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(Trivia.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Trivia.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void askQuestion2(String msg, int roleIdI) {
        try {
            DeliveryDB.replyComplain(roleIdI, IwebManager.Instance.MainGMID, IwebManager.Instance.MainName, msg);
        } catch (Exception e) {
        }
    }

    private static void askQuestion(String msg, byte chan) {
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

    private static void stopRoundNoAnswer() {
        if (Trivia.isEventTime) {
            askQuestion("[TRIVIA] No answer. The response was:"+Trivia.answer);
            resetAll();
            //MathTrivia.startEvent();
        }
    }

    private static void resetAll() {
        Trivia.isEventTime = false;
        Trivia.questionAnswered = false;
        Trivia.question = null;
        Trivia.answer = null;
        Trivia.coinsReward = 0;
        Trivia.isCoinsRewards = false;
        try {
            Trivia.exec.shutdown();
        } catch (Exception e) {
        }
    }

    private static byte[] hextoByteArray(String x) {
        if (x.length() < 2) {
            return new byte[0];
        }
        if (x.length() % 2 != 0) {

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
