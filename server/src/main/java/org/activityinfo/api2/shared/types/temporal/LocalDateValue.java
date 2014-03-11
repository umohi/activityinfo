package org.activityinfo.api2.shared.types.temporal;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.api2.shared.types.FieldValue;

import java.util.Date;

/**
 * References a single calendar day relative to the gregorian calendar, without
 * reference to a timezone.
 */
public class LocalDateValue extends FieldValue {

    private int year;
    private int monthOfYear;
    private int dayOfMonth;


    public LocalDateValue(int year, int month, int day) {
        this.year = year;
        this.monthOfYear = month;
        this.dayOfMonth = day;
    }

    public int getYear() {
        return year;
    }

    public int getMonthOfYear() {
        return monthOfYear;
    }

    public int getDayOfMonth() {
        return dayOfMonth;
    }

    @Override
    public String getTypeClassId() {
        return LocalDateType.TYPE_CLASS_ID;
    }

    @SuppressWarnings("deprecation")
    public static FieldValue valueOf(Date date) {
        return new LocalDateValue(date.getYear(), date.getMonth()+1, date.getDay());
    }

    public static LocalDateValue valueOf(LocalDate localDate) {
        return new LocalDateValue(
                localDate.getYear(),
                localDate.getMonthOfYear(),
                localDate.getDayOfMonth());
    }
}
