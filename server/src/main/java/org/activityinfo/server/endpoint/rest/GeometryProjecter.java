package org.activityinfo.server.endpoint.rest;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.CoordinateSequence;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import com.vividsolutions.jts.geom.util.GeometryTransformer;
import org.activityinfo.analysis.server.generator.map.TiledMap;
import org.activityinfo.analysis.shared.content.AiLatLng;
import org.activityinfo.analysis.shared.content.Point;

import java.util.Arrays;

public class GeometryProjecter extends GeometryTransformer {

    private TiledMap map;

    public GeometryProjecter(TiledMap map) {
        super();
        this.map = map;
    }

    @Override
    protected CoordinateSequence transformCoordinates(CoordinateSequence coords, Geometry parent) {

        int n = coords.size();
        Coordinate outCoords[] = new Coordinate[n];
        int outIndex = 0;

        for (int i = 0; i != n; ++i) {
            Point px = map.fromLatLngToPixel(new AiLatLng(coords.getY(i), coords.getX(i)));
            outCoords[outIndex] = new Coordinate(px.getDoubleX(), px.getDoubleY());
            outIndex++;
        }
        return new CoordinateArraySequence(Arrays.copyOf(outCoords, outIndex));

    }

}
