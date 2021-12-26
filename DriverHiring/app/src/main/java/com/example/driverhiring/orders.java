package com.example.driverhiring;

public class orders {

    public  String fromdate,todate,time,start_place,end_place,uid;
    public orders()
    {

    }
    public orders(String fromdate, String todate, String time, String start_place, String end_place, String uid)
    {
        this.end_place=end_place;
        this.start_place=start_place;
        this.fromdate=fromdate;
        this.todate=todate;
        this.time=time;
        this.uid = uid;
    }

    public String getFromdate() {
        return fromdate;
    }

    public void setFromdate(String fromdate) {
        this.fromdate = fromdate;
    }

    public String getTodate() {
        return todate;
    }

    public void setTodate(String todate) {
        this.todate = todate;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStart_place() {
        return start_place;
    }

    public void setStart_place(String start_place) {
        this.start_place = start_place;
    }

    public String getEnd_place() {
        return end_place;
    }

    public void setEnd_place(String end_place) {
        this.end_place = end_place;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }



}
