package com.mmjang.duckmemo.util;

import org.threeten.bp.Instant;
import org.threeten.bp.LocalDate;
import org.threeten.bp.LocalDateTime;
import org.threeten.bp.OffsetDateTime;
import org.threeten.bp.ZoneId;

public class DateUtils {
    public static long getStartOfToday(){
         return LocalDate.now().atStartOfDay().toInstant(OffsetDateTime.now().getOffset()).toEpochMilli();
    }

    public static long fromMillisToStartOfDay(long millis){
         return LocalDateTime.ofInstant(Instant.ofEpochMilli(millis), ZoneId.systemDefault()).toLocalDate().atStartOfDay().toInstant(
                OffsetDateTime.now().getOffset()).toEpochMilli();
    }
}
