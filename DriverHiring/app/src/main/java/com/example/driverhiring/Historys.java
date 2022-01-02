package com.example.driverhiring;

public class Historys {

    public  String start_place,end_place,date,amount;
    public Historys()
    {

    }
    public Historys(String start_place, String end_place, String date,String amount)
    {
        this.end_place=end_place;
        this.start_place=start_place;
        this.date=date;
        this.amount=amount;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

}
