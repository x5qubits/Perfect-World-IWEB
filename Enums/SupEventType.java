/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Enums;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author IcyTeck
 */
public enum SupEventType {
    Broadcast_Message(50),
    Activate_Monster(1),
    Disable_Monster(2),
    Activate_Double_Money(3),
    Activate_Double_Drop(4),
    Activate_Double_SP(5),
    Activate_Forbid_Trade(6),
    Activate_Forbid_Sale(7),
    Activate_Forbid_Mail(8),
    Activate_Forbid_Faction(9),
    Activate_Forbid_Sell(10),
    Disable_Double_Money(11),
    Disable_Double_Drop(12),
    Disable_Double_SP(13),
    Disable_Forbid_Trade(14),
    Disable_Forbid_Sale(15),
    Disable_Forbid_Mail(16),
    Disable_Forbid_Faction(17),
    Disable_Forbid_Sell(18),
    Set_Lambda(19),
    Set_Max_Online(20),
    Set_Exp_Rate(21);

    private int value;
    private static Map map = new HashMap<>();

    private SupEventType(int value) {
        this.value = value;
    }

    static {
        for (SupEventType pageType : SupEventType.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static SupEventType valueOf(int pageType) {
        return (SupEventType) map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
