package jhs.java.manager;

import com.goldhuman.Common.Conf;
import com.goldhuman.Common.Octets;
import com.goldhuman.Common.ThreadPool;
import com.goldhuman.IO.ActiveIO;
import com.goldhuman.IO.PollIO;
import com.goldhuman.IO.Protocol.Protocol;
import com.goldhuman.service.GMServiceImpl;
import com.goldhuman.util.ConfigUtil;
import java.io.File;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import protocol.ClientManager;
import protocol.DeliveryClientManager;

public class IWebStarter implements ServletContextListener {

    private static final Log log = LogFactory.getLog(IWebStarter.class);
    public static ActiveIO x1 = null;
    public static ActiveIO x2 = null;
    public static ClientManager cm = null;
    public static GMServiceImpl gmi = null;
    public static DeliveryClientManager dcm = null;

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        (new HelloThread()).start();
    }

    public class HelloThread extends Thread {

        @Override
        public void run() {
                 Conf.GetInstance("/etc/iweb.conf", null);
                Octets.setDefaultCharset("UTF-16LE");
                IWebStarter.gmi = new GMServiceImpl();
                try
                {
                  Class.forName("protocol.DeliveryDB");
                  String dir = ConfigUtil.defaultHomePath();
                  File f1 = new File(dir + "/tmp/moverole/incoming");
                  File f2 = new File(dir + "/tmp/moverole/outcoming");
                  File f3 = new File(dir + "/tmp/rolexml/incoming");
                  File f4 = new File(dir + "/tmp/rolexml/outcoming");
                  if (!f1.exists()) {
                    f1.mkdirs();
                  }
                  if (!f2.exists()) {
                    f2.mkdirs();
                  }
                  if (!f3.exists()) {
                    f3.mkdirs();
                  }
                  if (!f4.exists()) {
                    f4.mkdirs();
                  }
                }
                catch (Exception ex) {}
                try
                {
                  IWebStarter.cm = ClientManager.GetInstance();
                  IWebStarter.x1 = Protocol.Client(IWebStarter.cm);
                }
                catch (Exception e)
                {
                  JHSLogingSystem.LogInfo(IWebStarter.class.getName(), "ClientManager Connect To Game Error!");
                }
                try
                {
                  IWebStarter.dcm = DeliveryClientManager.GetInstance();
                  IWebStarter.x2 = Protocol.Client(IWebStarter.dcm);
                }
                catch (Exception e)
                {
                  JHSLogingSystem.LogInfo(IWebStarter.class.getName(), "DeliveryClientManager Connect To Game Error!");
                }
                ThreadPool.AddTask(PollIO.GetTask());
                JHSLogingSystem.LogInfo(IWebStarter.class.getName(), "JHS Iweb Started!");
              }

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

        try {
            x1.Close();
        } catch (Exception ex) {
        }
        try {
            x2.Close();
        } catch (Exception ex) {
        }
    }
}
