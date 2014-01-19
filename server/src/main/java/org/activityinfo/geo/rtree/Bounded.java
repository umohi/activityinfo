package org.activityinfo.geo.rtree;

import com.vividsolutions.jts.geom.Envelope;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;


public class Bounded {

    public static final int X = 0;
    public static final int Y = 1;

    protected final float[] min = new float[2];
    protected final float[] max = new float[2];

    public Bounded() {
        min[X] = +Float.MAX_VALUE;
        min[Y] = +Float.MAX_VALUE;
        max[X] = -Float.MAX_VALUE;
        max[Y] = -Float.MAX_VALUE;
    }

    public Bounded(Bounded other) {
        min[X] = other.min[X];
        min[Y] = other.min[Y];
        max[X] = other.max[X];
        max[Y] = other.max[Y];
    }

    public Bounded(Envelope envelope) {
        min[X] = (float)envelope.getMinX();
        min[Y] = (float)envelope.getMinY();
        max[X] = (float)envelope.getMaxX();
        max[Y] = (float)envelope.getMaxY();
    }

    public final float getCoord(int i) {
        return min[i];
    }

    public final float getDimension(int i) {
        return max[i] - min[i];
    }

    public void expandTo(Bounded bounded) {
        if(bounded.min[X] < min[X]) {
            min[X] = bounded.min[X];
        }
        if(bounded.min[Y] < min[Y]) {
            min[Y] = bounded.min[Y];
        }
        if(bounded.max[X] > max[X]) {
            max[X] = bounded.max[X];
        }
        if(bounded.max[Y] > max[Y]) {
            max[Y] = bounded.max[Y];
        }
    }

    public float getMin(int i) {
        return min[i];
    }

    public float getMax(int i) {
        return max[i];
    }

    public float area() {
        if(isEmpty()) {
            return 0;
        } else {
            return (max[X] - min[X]) * (max[Y] - min[Y]);
        }
    }

    public final float requiredExpansion(Bounded other) {
        Bounded expanded = new Bounded(this);
        expanded.expandTo(other);
        return expanded.area();
    }

    public boolean contains(Bounded rect) {
        return min[X] <= rect.min[X] &&
               min[Y] <= rect.min[Y] &&
               max[X] >= rect.max[X] &&
               max[Y] >= rect.max[Y];
    }

    protected final void writeBounds(DataOutput out) throws IOException {
        out.writeFloat(min[X]);
        out.writeFloat(min[Y]);
        out.writeFloat(max[X]);
        out.writeFloat(max[Y]);
    }

    protected final void readBounds(DataInput in) throws IOException {
        min[X] = in.readFloat();
        min[Y] = in.readFloat();
        max[X] = in.readFloat();
        max[Y] = in.readFloat();
    }

    public boolean isEmpty() {
        return min[X] > max[X];
    }

    public boolean intersects(Bounded other) {
        if (isEmpty() || other.isEmpty()) {
            return false;
        }
        return !(other.min[X] > max[X] ||
                other.max[X] < min[X] ||
                other.min[Y] > max[Y] ||
                other.max[Y] < min[Y]);
    }
}
