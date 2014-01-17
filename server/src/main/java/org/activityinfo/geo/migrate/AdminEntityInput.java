package org.activityinfo.geo.migrate;

import com.google.appengine.tools.mapreduce.Input;
import com.google.appengine.tools.mapreduce.InputReader;
import org.activityinfo.server.database.hibernate.entity.AdminEntity;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


public class AdminEntityInput extends Input<AdminEntity> {

    @Override
    public List<? extends InputReader<AdminEntity>> createReaders() throws IOException {
        return Arrays.asList(new AdminEntityReader(0));
    }

}
