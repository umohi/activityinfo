package org.activityinfo.geo.rtree;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;


public class Data {

    private final String uri;

    public Data(String uri) {
        this.uri = uri;
    }

    public void write(DataOutputStream out) throws IOException {
        out.writeUTF(uri);
    }

    public static Data read(DataInputStream in) throws IOException {
        return new Data(in.readUTF());
    }

    @Override
    public String toString() {
        return uri;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data data = (Data) o;

        if (uri != null ? !uri.equals(data.uri) : data.uri != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return uri != null ? uri.hashCode() : 0;
    }
}
