/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package customProtocol.model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

/**
 *
 * @author IcyTeck
 */
public class MyFormatter extends Formatter {
    // Create a DateFormat to format the logger timestamp.

    private static final DateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss.SSS");

    @Override
    public String format(LogRecord record) {
        StringBuilder builder = new StringBuilder(1000);
        builder.append(df.format(new Date(record.getMillis()))).append(" - ");
        builder.append("[").append(record.getLevel()).append("] - ");
        builder.append(formatMessage(record));
        builder.append("\n");
        return builder.toString();
    }

    @Override
    public String getHead(Handler h) {
        return super.getHead(h);
    }

    @Override
    public String getTail(Handler h) {
        return super.getTail(h);
    }
}
