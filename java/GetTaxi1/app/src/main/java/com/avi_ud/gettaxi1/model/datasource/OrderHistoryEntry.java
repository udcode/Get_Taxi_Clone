package com.avi_ud.gettaxi1.model.datasource;

import java.util.Objects;

public class OrderHistoryEntry {
    String driver_name;
    String dest;
    String src;
    String date;

    public OrderHistoryEntry() {
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderHistoryEntry that = (OrderHistoryEntry)o;
        if(!driver_name.equals(that.getDriver_name())|| !src.equals(that.getSrc()) || !dest.equals(that.getDest())|| !date.equals(that.getDate())){
            return false;
        }
        return true;

    }

    @Override
    public int hashCode() {
        return Objects.hash(driver_name, dest, src);
    }

    public OrderHistoryEntry(String date,String name, String src, String dest ) {
        this.driver_name=name;
        this.dest=dest;
        this.src=src;
        this.date = date;
    }

    public String getDriver_name() {
        return driver_name;
    }

    public String getDest() {
        return dest;
    }
    public String getDate() {
        return date;
    }

    public String getSrc() {
        return src;
    }


}
