package org.activityinfo.core.shared.type;

import com.bedatadriven.rebar.time.calendar.LocalDate;
import org.activityinfo.core.shared.serialization.SerArray;
import org.activityinfo.core.shared.serialization.SerReal;
import org.activityinfo.core.shared.serialization.SerValue;

import java.util.Date;

/**
 * ISO8601 Calendar Date, without timezone
 */
public class LocalDateValue implements FieldValue {

    public static final String TYPE_CLASS_ID = "localDate";

    private final int month;
    private final int year;
    private final int day;

    public LocalDateValue(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public int getYear() {
        return year;
    }

    public int getDay() {
        return day;
    }


    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }

    @Override
    public SerValue serialize() {
        SerArray array = new SerArray();
        array.add(new SerReal(year));
        array.add(new SerReal(month));
        array.add(new SerReal(day));
        return array;
    }

    public static LocalDateValue valueOf(LocalDate localDate) {
        return new LocalDateValue(localDate.getYear(), localDate.getMonthOfYear(), localDate.getDayOfMonth());
    }

    @SuppressWarnings("deprecation")
    public static LocalDateValue valueOf(Date date) {
        return new LocalDateValue(date.getYear(), date.getMonth()+1, date.getDay());
    }


    public static class Parser implements FieldValueParser {

        @Override
        public FieldValue parse(SerValue value) {
            SerArray array = value.asArray();
            return new LocalDateValue(
                    array.get(0).asInteger(),
                    array.get(1).asInteger(),
                    array.get(2).asInteger());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalDateValue that = (LocalDateValue) o;

        if (day != that.day) return false;
        if (month != that.month) return false;
        if (year != that.year) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = month;
        result = 31 * result + year;
        result = 31 * result + day;
        return result;
    }
}
