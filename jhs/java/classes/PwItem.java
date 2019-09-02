/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import com.goldhuman.Common.Octets;
import protocol.GRoleInventory;

/**
 *
 * @author IcyTeck
 */
public class PwItem {

    public int id;
    public int pos;
    public String name;
    public int count;
    public int max_count;
    public String data;
    public int proctype;
    public int expire_date;
    public int guid1;
    public int guid2;
    public int mask;

    public PwItem() {

    }

    public PwItem(GRoleInventory gri) {
        id = gri.id;
        pos = gri.pos;
        count = gri.count;
        max_count = gri.max_count;
        data = toHexString(gri.data.getBytes());
        proctype = gri.proctype;
        expire_date = gri.expire_date;
        guid1 = gri.guid1;
        guid2 = gri.guid2;
        mask = gri.mask;
    }

    public GRoleInventory Convert() {
        GRoleInventory gri = new GRoleInventory();
        gri.id = id;
        gri.pos = pos;
        gri.count = count;
        gri.max_count = max_count;
        gri.data = GetOctets();
        gri.proctype = proctype;
        gri.expire_date = expire_date;
        gri.guid1 = guid1;
        gri.guid2 = guid2;
        gri.mask = mask;
        return gri;
    }

    public Octets GetOctets() {
        return new Octets(hextoByteArray(data));
    }

    private byte[] hextoByteArray(String x) {
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

    private String toHexString(byte[] x) {
        StringBuffer sb = new StringBuffer(x.length * 2);
        for (int i = 0; i < x.length; ++i) {
            byte n = x[i];
            int nibble = n >> 4 & 0xF;
            sb.append((char) ((nibble >= 10) ? 97 + nibble - 10 : 48 + nibble));
            nibble = n & 0xF;
            sb.append((char) ((nibble >= 10) ? 97 + nibble - 10 : 48 + nibble));
        }
        return sb.toString();
    }
}
