package com.tapiodev.sijoitussovellus;
import androidx.room.TypeConverter;
import java.util.Date;
public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value){
        return value == null ? new Date() : new Date(value);
    }
    @TypeConverter
    public static Long dateToTimestamp(Date date){
        return date == null ? null : date.getTime();
    }
}
