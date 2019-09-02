/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import jhs.java.classes.DefaultItems;
import jhs.java.classes.Player;
import jhs.java.classes.PwItem;
import jhs.java.classes.QuestionData;
import jhs.java.classes.Rewardqs;
import jhs.java.classes.Setting;
import jhs.java.mysql.JHSMySQL;
import jhs.java.mysql.JHSMySQLResult;
import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.codehaus.jackson.map.ObjectMapper;

/**
 *
 * @author IcyTeck
 */
public class ItemsManager {

    public Rewardqs rewardqs = null;
    public static List<Integer> askedQuestions = new ArrayList<>();
    public static final ItemsManager Instance = new ItemsManager();
    private final int reward_items_id = 13;
    private ConfigManager configManager = null;
    private Setting ritems;
    private ObjectMapper mapper;
    private DataBaseManager DB;

    public void Init() {
        configManager = ConfigManager.Instance;
        ritems = configManager.settings.get(reward_items_id);
        mapper = new ObjectMapper();
        DB = DataBaseManager.Instance;
        try {
            rewardqs = mapper.readValue(new String(Base64.decode(ritems.value)), Rewardqs.class);
            rewardqs.questionsData = new ArrayList<>();
            JHSMySQL mysql = JHSMySQL.getInstance();
            ConfigManager configsx = ConfigManager.Instance;
            mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.DBNAME);
            JHSMySQLResult rst = mysql.Query("SELECT * FROM `TriviaQuests`WHERE 1");
            while (rst.next()) {
                QuestionData qd = new QuestionData();
                qd.Question = rst.getString("quest");
                qd.Answer = rst.getString("answer");
                qd.Id = Integer.parseInt(rst.getString("id"));
                rewardqs.questionsData.add(qd);  
            }
            mysql.close();  
        } catch (Exception ex) {
            JHSLogingSystem.LogException(ItemsManager.class.getName(), ExceptionUtils.getStackTrace(ex), ex);
            rewardqs = new Rewardqs();
            ritems.Save();
        }
    }

    public Rewardqs Get() {
        return rewardqs;
    }

    public void Set(Rewardqs data) {
        rewardqs = data;
        try {
            JHSMySQL mysql = JHSMySQL.getInstance();
            ConfigManager configsx = ConfigManager.Instance;
            mysql.connect(configsx.DBSTRING, configsx.DBUSER, configsx.DBPASS, configsx.DBNAME);
            Boolean saved = false;
            for (QuestionData questionsData : data.questionsData) {
                String query = "INSERT INTO `TriviaQuests` (`id`, `answer`, `quest`) VALUES"
                        + "(?, ?, ?)"
                        + " ON DUPLICATE KEY UPDATE answer=?, quest=?;";
                saved = mysql.Query(query, questionsData.Id+"", questionsData.Answer, questionsData.Question, questionsData.Answer, questionsData.Question).SUCCESS;
                if(saved) {
                }
            }           
            mysql.close();  
            rewardqs.questionsData = null;
            ritems.value = Base64.encode(mapper.writeValueAsString(rewardqs).getBytes());
            ritems.Save();
            rewardqs.questionsData = data.questionsData;
            
            
            
        } catch (IOException ex) {
            JHSLogingSystem.LogException(ItemsManager.class.getName(), ExceptionUtils.getStackTrace(ex), ex);
        }
    }

    public PwItem GetRandomItem() {
        List<String> valuesList = new ArrayList<>(rewardqs.itemsData.keySet());
        int randomIndex = new Random().nextInt(valuesList.size());
        String randomValue = valuesList.get(randomIndex);
        return rewardqs.itemsData.get(randomValue);
    }

    public QuestionData GetRandomQuestion() {
        int rewardType = new Random().nextInt(rewardqs.questionsData.size());
        return rewardqs.questionsData.get(rewardType);
    }

    public void parse(String RoleId, String itemID, String itemCount) {
        try {
            if (configManager.GetSettings(false).get(14).value.equals("true")) {
                if (rewardqs.rewardsData.containsKey(itemID)) {
                    Player player = DB.GetPlayer(RoleId);
                    if (player != null) {
                        int count = Integer.parseInt(itemCount);
                        DefaultItems item = rewardqs.rewardsData.get(itemID);
                        int price = item.itemMultiplier * count;
                        player.dqpoints_que += price;
                        long timeNow = System.currentTimeMillis() / 1000;
                        player.loginTime = timeNow;                        
                        player.IsOnline = 1;                        
                        player.Save();
                        IwebManager.Instance.SendChat("Total DQ points registred:" + price + " Total Points:" + player.dqpoints_que, (byte) 4, player.RoleId);
                    } else {
                        int rr = Integer.parseInt(RoleId);
                        if (rr > 1000) {
                            IwebManager.Instance.SendChat("I can't register this points there is somthing wrong. Please relog and try again.", (byte) 4, rr);
                        }
                    }

                }
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(ItemsManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
    }
}
