/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.manager;

import Enums.GameEventsId;
import com.goldhuman.Common.Conf;
import customProtocol.model.ChatBroadCast;
import customProtocol.model.GameSocketConnect;
import events.PVPEvent;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import org.apache.axis.encoding.Base64;
import org.apache.commons.lang.exception.ExceptionUtils;
import protocol.DeliveryDB;
import protocol.GMControlGame_Re;

/**
 *
 * @author IcyTeck
 */
public class IwebManager {

    public static IwebManager Instance = new IwebManager();
    public Integer MainGMID = 1024;
    public String MainName = "System";
    public String[] glinkd_startCount = null;

    public IwebManager() {

    }

    public void Init() {
        MainName = Conf.GetInstance().find("OTHER_CONFIGS", "whisper_gm_name");
        MainGMID = Integer.parseInt(Conf.GetInstance().find("OTHER_CONFIGS", "whisper_gm_id"));
        glinkd_startCount = Conf.GetInstance().find("OTHER_CONFIGS", "glinkd_startCount").split("_");
    }

    public Boolean SendChat(String msg, byte channel, Integer playerId) {
        Boolean success = false;
        try {
            switch (channel) {
                case 4:
                    success = DeliveryDB.replyComplain(playerId, MainGMID, MainName, msg);
                    break;
                default:
                    ChatBroadCast chc = new ChatBroadCast(msg);
                    chc.channel = channel;
                    chc.srcroleid = 0;
                    GameSocketConnect.Send(chc);
                    success = true;
                    try {
                        BufferedWriter bw = new BufferedWriter(new FileWriter(ServerLogManager.Instance.fd_chat, true));
                        bw.write((new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())) + " iweb glinkd-0: chat : Chat: src=" + MainGMID + " chl=" + channel + " msg=" + Base64.encode(msg.getBytes("UTF-16LE")) + "\n");
                        bw.close();
                    } catch (Exception e) {

                    }
                    break;
            }

        } catch (Exception e) {
            JHSLogingSystem.LogException(IwebManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return success;
    }

    public Boolean DisableMonster(Integer World, Integer ctrl) {
        Boolean success = false;
        try {
            String command = "cancel_npc_generator " + ctrl;
            GMControlGame_Re res = DeliveryDB.GMControlGame(World, command);
            if (null != res && 0 == res.retcode) {
                success = true;
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(IwebManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return success;
    }

    public Boolean ActivateMonster(Integer World, Integer ctrl) {
        Boolean success = false;
        try {
            String command = "active_npc_generator " + ctrl;
            GMControlGame_Re res = DeliveryDB.GMControlGame(World, command);
            if (null != res && 0 == res.retcode) {
                success = true;
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(IwebManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return success;
    }

    public Boolean ActivateCloseEvents(Integer eventId, String eventValue) {
        Boolean success = false;
        try {
            if (eventId == GameEventsId.Lambda.ordinal()) {
                int lambda = Integer.parseInt(eventValue);
                success = DeliveryDB.GMSetLambda(lambda);
            }
            if (eventId == GameEventsId.NoTrade.ordinal()) {
                success = DeliveryDB.GMSetNoTrade(eventValue.equals("true"));
            }
            if (eventId == GameEventsId.NoAuction.ordinal()) {
                success = DeliveryDB.GMSetNoAuction(eventValue.equals("true"));
            }
            if (eventId == GameEventsId.NoMail.ordinal()) {
                success = DeliveryDB.GMSetNoMail(eventValue.equals("true"));
            }
            if (eventId == GameEventsId.NoFaction.ordinal()) {
                success = DeliveryDB.GMSetNoFaction(eventValue.equals("true"));
            }
            if (eventId == GameEventsId.DoubleMoney.ordinal()) {
                success = DeliveryDB.GMSetDoubleMoney(eventValue.equals("true"));
            }
            if (eventId == GameEventsId.DoubleObject.ordinal()) {
                success = DeliveryDB.GMSetDoubleObject(eventValue.equals("true"));
            }
            if (eventId == GameEventsId.DoubleSP.ordinal()) {
                success = DeliveryDB.GMSetDoubleSP(eventValue.equals("true"));
            }
            if (eventId == GameEventsId.NoSellPoint.ordinal()) {
                success = DeliveryDB.GMSetNoSellPoint(eventValue.equals("true"));
            }
            if (eventId == GameEventsId.DoubleExp.ordinal()) {
                Double experience = new Double(eventValue);
                com.goldhuman.service.GMServiceImpl gm = new com.goldhuman.service.GMServiceImpl();
                success = gm.setw2iexperience(experience, new com.goldhuman.service.interfaces.LogInfo());
            }
            if (eventId == GameEventsId.MaxOnline.ordinal()) {
                Integer maXonline = Integer.parseInt(eventValue);
                DeliveryDB.SetMaxOnlineNum(maXonline, maXonline);
            }
            if (eventId == GameEventsId.ShutdownGame.ordinal()) {
                Integer waitsecs = Integer.parseInt(eventValue);
                success = DeliveryDB.GMRestartServer(-1, waitsecs);
            }
            if (eventId == GameEventsId.ActivatePVPEvent.ordinal()) {
                if(!PVPEvent.isStartedbyScheduled)
                {
                    PVPEvent.isStartedbyScheduled = true;
                    Thread t1 = new Thread(new Runnable() {
                         @Override
                         public void run() {
                             PVPEvent.start();
                         }
                     });
                    t1.start();
                    success = true;
                }else{
                    success = false;
                }
            }
        } catch (Exception e) {
            JHSLogingSystem.LogException(IwebManager.class.getName(), ExceptionUtils.getStackTrace(e), e);
        }
        return success;
    }
}
