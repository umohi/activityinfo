package org.activityinfo.legacy.shared.command.result;

/*
 * #%L
 * ActivityInfo Server
 * %%
 * Copyright (C) 2009 - 2013 UNICEF
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 3 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-3.0.html>.
 * #L%
 */

import org.activityinfo.core.shared.model.AiLatLng;
import org.activityinfo.legacy.shared.model.IndicatorDTO;
import org.activityinfo.legacy.shared.reports.content.DimensionCategory;
import org.activityinfo.legacy.shared.reports.model.Dimension;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Contains the aggregate value for an intersection of dimension categories.
 */
public class Bucket implements Serializable {
    private double sum;
    private int count;
    private int aggregationMethod;
    private AiLatLng point;

    private Map<Dimension, DimensionCategory> categories = new HashMap<Dimension, DimensionCategory>();

    public Bucket() {
    }

    public Bucket(double sum) {
        this.sum = sum;
        this.count = 1;
        this.aggregationMethod = IndicatorDTO.AGGREGATE_SUM;
    }

    public Bucket(double sum, int count, int aggregationMethod) {
        this.sum = sum;
        this.count = count;
        this.aggregationMethod = aggregationMethod;
    }

    public Collection<Dimension> dimensions() {
        return categories.keySet();
    }

    public void setCategory(Dimension dimension, DimensionCategory category) {
        this.categories.put(dimension, category);
    }

    public DimensionCategory getCategory(Dimension dimension) {
        return categories.get(dimension);
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public AiLatLng getPoint() {
        return point;
    }

    public void setPoint(AiLatLng point) {
        this.point = point;
    }

    public int getAggregationMethod() {
        return aggregationMethod;
    }

    public void setAggregationMethod(int aggregationMethod) {
        this.aggregationMethod = aggregationMethod;
    }

    public double doubleValue() {
        switch (aggregationMethod) {
            case IndicatorDTO.AGGREGATE_AVG:
                return sum / count;
            case IndicatorDTO.AGGREGATE_SUM:
                return sum;
            case IndicatorDTO.AGGREGATE_SITE_COUNT:
                return count;
        }
        throw new UnsupportedOperationException("aggregationMethod: "
                + aggregationMethod);
    }

    public Object getKey() {
        return categories;
    }

    public void add(Bucket bucket) {
        this.sum += bucket.getSum();
        this.count += bucket.getCount();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  { Value: ").append(doubleValue());
        for (Dimension dim : dimensions()) {
            DimensionCategory cat = getCategory(dim);
            sb.append("\n    ").append(dim.toString()).append(": ");
            sb.append(cat.toString());
        }
        sb.append("\n  }");
        return sb.toString();
    }
}