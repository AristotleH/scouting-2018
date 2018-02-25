package com.example.aristotle.scouting;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class MatchInfo {

    private int blueTeamNumber;
    private int redTeamNumber;
    private int[] cubeTotals;
    private String dateAndTimeCreated;

    /*
        positions in cubeTotals:
        0 - blue's own switch
        1 - blue's scale
        2 - blue's faraway switch
        3 - red's own switch
        4 - red's scale
        5 - red's faraway switch
         */

    public MatchInfo(int bNumber, int rNumber, int[] cT, String timeAndDate) {
        blueTeamNumber = bNumber;
        redTeamNumber = rNumber;
        cubeTotals = cT;
        if (timeAndDate == null) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss', ' MM/dd");
            sdf.setTimeZone(TimeZone.getDefault());
            dateAndTimeCreated = sdf.format(Calendar.getInstance().getTime());
        }
        else {
            dateAndTimeCreated = timeAndDate;
        }

    }

    public int getBlueTeamNumber() {
        return blueTeamNumber;
    }

    public void setBlueTeamNumber(int blueTeamNumber) {
        this.blueTeamNumber = blueTeamNumber;
    }

    public int getRedTeamNumber() {
        return redTeamNumber;
    }

    public void setRedTeamNumber(int redTeamNumber) {
        this.redTeamNumber = redTeamNumber;
    }

    public int[] getCubeTotals() {
        return cubeTotals;
    }

    public void setCubeTotals(int[] cubeTotals) {
        this.cubeTotals = cubeTotals;
    }

    public String getDateAndTimeCreated() {
        return dateAndTimeCreated;
    }

    public void setDateAndTimeCreated(String dateAndTimeCreated) {
        this.dateAndTimeCreated = dateAndTimeCreated;
    }
}
