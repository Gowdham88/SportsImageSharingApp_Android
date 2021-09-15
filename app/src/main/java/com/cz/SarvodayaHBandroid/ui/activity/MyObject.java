package com.cz.SarvodayaHBandroid.ui.activity;

/**
 * Created by czsm4 on 01/02/18.
 */

import java.util.Date;

public class MyObject implements Comparable<MyObject> {

    private Date dateTime;

    public Date getDateTime() {
        return dateTime;
    }

    public void setDateTime(Date datetime) {
        this.dateTime = datetime;
    }

    @Override
    public int compareTo(MyObject o) {
        if (getDateTime() == null || o.getDateTime() == null)
            return 0;
        return getDateTime().compareTo(o.getDateTime());
    }
}