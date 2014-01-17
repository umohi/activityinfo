package org.activityinfo.geo.migrate;


import com.google.appengine.tools.mapreduce.InputReader;
import org.activityinfo.server.DependencyInjection;
import org.activityinfo.server.database.hibernate.HibernateModule;
import org.activityinfo.server.database.hibernate.entity.AdminEntity;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class AdminEntityReader extends InputReader<AdminEntity> {

    private int startId;

    private transient Iterator<AdminEntity> queryIterator;


    public AdminEntityReader(int startId) {
        this.startId = startId;
    }

    @Override
    public void beginSlice() throws IOException {
        EntityManagerFactory emf = DependencyInjection.INJECTOR.getInstance(EntityManagerFactory.class);
        EntityManager em = emf.createEntityManager();
        queryIterator = em.createQuery("select e from AdminEntity e where e.id >= :start and e.level.id=1 and geometry is not null order by e.id")
                .getResultList().iterator();
    }

    @Override
    public AdminEntity next() throws IOException, NoSuchElementException {
        AdminEntity entity = queryIterator.next();
        startId = entity.getId() + 1;
        return entity;
    }
}
