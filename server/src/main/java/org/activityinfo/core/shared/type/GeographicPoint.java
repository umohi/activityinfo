package org.activityinfo.core.shared.type;

import org.activityinfo.core.shared.serialization.SerArray;
import org.activityinfo.core.shared.serialization.SerValue;

/**
 * A Geographic Point, referenced to the WGS-84 datum
 */
public class GeographicPoint implements FieldValue {

    private static final String TYPE_CLASS_ID = "geoPoint";

    private final double latitude;
    private final double longitude;

    public GeographicPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String getTypeClassId() {
        return TYPE_CLASS_ID;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    @Override
    public SerValue serialize() {
        SerArray coordinates = new SerArray();
        coordinates.add(latitude);
        coordinates.add(longitude);
        return coordinates;
    }

    public static class Parser implements FieldValueParser {

        @Override
        public FieldValue parse(SerValue value) {
            SerArray coordinates = value.asArray();
            return new GeographicPoint(
                    coordinates.get(0).asReal(),
                    coordinates.get(1).asReal());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GeographicPoint that = (GeographicPoint) o;

        if (Double.compare(that.latitude, latitude) != 0) return false;
        if (Double.compare(that.longitude, longitude) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(latitude);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(longitude);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
