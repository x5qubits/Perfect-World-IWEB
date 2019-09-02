/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import Enums.NotificationType;

/**
 *
 * @author IcyTeck
 */
public class Notification {

    public String title = "Information";
    public String content = "";

    public String hclass = " <i class=\"fa fa-circle-thin fa-stack-2x text-success\"></i> <i class=\"fa fa-check fa-stack-1x text-success\"></i>";
    public String Clock = "";

    private String xerror = "<i class=\"fa fa-circle-thin fa-stack-2x text-warning\"></i><i class=\"fa fa-exclamation fa-stack-1x fa-fw text-warning\"></i>";
    private String xsuccess = "<i class=\"fa fa-circle-thin fa-stack-2x text-success\"></i><i class=\"fa fa-check fa-stack-1x text-success\"></i>";
    private String xprimary = "<i class=\"fa fa-circle-thin fa-stack-2x text-primary\"></i><i class=\"fa fa-info fa-stack-1x text-primary\"></i></i>";
    private String xdanger = " <i class=\"fa fa-circle-thin fa-stack-2x text-danger\"></i><i class=\"fa fa-close fa-stack-1x fa-fw text-danger\"></i>";

    public Notification(String title, String content, String hclass) {
        this.title = title;
        this.content = content;
        this.hclass = hclass;
        Clock = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss a").format(Calendar.getInstance().getTime());
    }

    public Notification(String content) {
        this.content = content;
        Clock = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss a").format(Calendar.getInstance().getTime());
    }

    public Notification(String content, NotificationType Type) {
        this.content = content;
        switch (Type) {
            case Error:
                this.hclass = xerror;
                break;
            case Success:
                this.hclass = xsuccess;
                break;
            case Primary:
                this.hclass = xprimary;
                break;
            case Danger:
                this.hclass = xdanger;
                break;
            default:
                this.hclass = xprimary;
        }
        Clock = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss a").format(Calendar.getInstance().getTime());
    }
}
