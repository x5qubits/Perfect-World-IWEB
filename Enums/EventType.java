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
public enum EventType {
    RUN_SCRIPT(0),
    RUN_BROADCAST(1),
    RUN_INGAME_EVENT(2);//Activate monster creator, 2x, 

    private int value;
    private static Map map = new HashMap<>();

    private EventType(int value) {
        this.value = value;
    }

    static {
        for (EventType pageType : EventType.values()) {
            map.put(pageType.value, pageType);
        }
    }

    public static EventType valueOf(int pageType) {
        return (EventType) map.get(pageType);
    }

    public int getValue() {
        return value;
    }

}
