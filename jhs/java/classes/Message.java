/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jhs.java.classes;

import java.io.Serializable;

/**
 *
 * @author IcyTeck
 */
public class Message implements Serializable {

    public Integer id;
    public Boolean success;
    public String message;
    public String content;
}
