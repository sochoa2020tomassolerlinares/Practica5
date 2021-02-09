package net.iessochoa.tomassolerlinares.practica5.model;

import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

/**
 * Clase encargada de convertir una fecha al formato de nuestro sistema
 */
public class TransformaFechaSQLite {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }
    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        //le quitamos la hora, pero si la necesitárais, no llaméis a removeTime
        return date == null ? null : removeTime(date).getTime();
    }
    public static Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }
}