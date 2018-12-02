package com.mmjang.ankihelper.data.history;

import android.provider.ContactsContract;

import org.litepal.crud.DataSupport;
import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.LocalTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;
import org.threeten.bp.ZoneOffset;
import org.threeten.bp.temporal.TemporalAdjuster;
import org.threeten.bp.temporal.TemporalField;

import java.util.List;

public class HistoryStat {
    private static long MILLIS_OF_DAY = 3600 * 24 * 1000;
    private int lastDays;
    private long startOfToday;
    private long startOfThisMonth;
    private long startOfLastDays;
    private List<History> dataOfLastDays;

    public HistoryStat(int days){
        lastDays = days;
        startOfToday = LocalDate.now().atStartOfDay().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        startOfThisMonth = LocalDate.now().withDayOfMonth(1).atStartOfDay()
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        startOfLastDays = LocalDate.now().minusDays(days - 1).atStartOfDay()
                .toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
        dataOfLastDays = DataSupport.where("timestamp > ?", Long.toString(startOfLastDays))
                .find(History.class);
    }

    public int getDayCount(int type){
        return DataSupport.where("timestamp > ? and type = ?",
                Long.toString(startOfToday), Integer.toString(type)).count(History.class);
    }

    public int getMonthCount(int type){
        return DataSupport.where("timestamp > ? and type = ?",
                Long.toString(startOfToday), Integer.toString(type)).count(History.class);
    }

    public int[][] getHourStatistics(){
        int[][] result = new int[3][24];
        for(History history : dataOfLastDays){
            long mills = history.getTimeStamp();
            int type = history.getType();
            int hour = LocalDateTime.ofInstant(Instant.ofEpochMilli(mills), ZoneId.systemDefault()).getHour();
            result[type][hour] += 1;
        }
        return result;
    }

    public int[][] getLastDaysStatistics(){
        int[][] result = new int[3][lastDays];
        for(History history : dataOfLastDays){
            int pos =(int) ((history.getTimeStamp() - startOfLastDays) / MILLIS_OF_DAY);
            int type = history.getType();
            result[type][pos] += 1;
        }
        return result;
    }

    public static void main(String[] args){
        HistoryStat historyStat = new HistoryStat(30);
    }
}
