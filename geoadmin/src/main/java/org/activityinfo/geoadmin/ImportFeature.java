package org.activityinfo.geoadmin;

import java.util.List;

import org.opengis.feature.type.PropertyDescriptor;

import com.vividsolutions.jts.geom.Envelope;

public class ImportFeature {
    private ImportSource source;
    private int index;
    private Object[] attributeValues;
    private Envelope envelope;
    private List<PropertyDescriptor> attributes;

    public ImportFeature(ImportSource source, int index) {
        this.source = source;
        this.index = index;
    }

    public ImportFeature(List<PropertyDescriptor> attributes, Object[] attributeValues, Envelope envelope) {
        this.attributes = attributes;
        this.attributeValues = attributeValues;
        this.envelope = envelope;
    }

    public Envelope getEnvelope() {
        return envelope;
    }

    public String getAttributeStringValue(PropertyDescriptor descriptor) {
        return getAttributeStringValue(attributes.indexOf(descriptor));
    }

    public int getIndex() {
        return index;
    }

    public Object[] getAttributeValues() {
        return attributeValues;
    }

    public Object getAttributeValue(int attributeIndex) {
        return attributeValues[attributeIndex];
    }

    public String getAttributeStringValue(int attributeIndex) {
        if (attributeValues[attributeIndex] == null) {
            return null;
        } else {
            return attributeValues[attributeIndex].toString();
        }
    }

    public double similarity(String name) {
        double nameSimilarity = 0;
        for (int attributeIndex = 0; attributeIndex != attributeValues.length; ++attributeIndex) {
            Object value = attributeValues[attributeIndex];
            if (value != null) {
                nameSimilarity = Math.max(nameSimilarity, PlaceNames.similiarity(name, value.toString()));
            }
        }
        return nameSimilarity;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i != attributeValues.length; ++i) {
            if (i > 0) {
                sb.append(" ");
            }
            sb.append(attributeValues[i]);
        }
        return sb.toString();
    }


}
