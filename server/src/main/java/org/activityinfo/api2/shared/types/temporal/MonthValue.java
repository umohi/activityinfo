package org.activityinfo.api2.shared.types.temporal;

import org.activityinfo.api2.shared.types.FieldValue;

public class MonthValue extends FieldValue {
    private int year;
    private int month;

    public MonthValue(int year, int month) {
        this.year = year;
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    @Override
    public String getTypeClassId() {
        return MonthType.TYPE_CLASS_ID;
    }
}
