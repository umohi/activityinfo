package org.activityinfo.rdf;

import com.hp.hpl.jena.ontology.*;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sdb.SDBException;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.StoreDesc;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatabaseType;
import com.hp.hpl.jena.sdb.store.DatasetStore;
import com.hp.hpl.jena.sdb.store.LayoutType;
import com.hp.hpl.jena.sdb.store.StoreFactory;
import org.activityinfo.graph.shared.ActivityInfoNamespace;
import org.activityinfo.graph.shared.Sparql;
import org.activityinfo.server.database.hibernate.AINamingStrategy;
import org.activityinfo.server.database.hibernate.HibernateModule;
import org.activityinfo.server.database.hibernate.entity.*;
import org.activityinfo.server.database.migration.GraphLoader;
import org.hibernate.ejb.Ejb3Configuration;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.activityinfo.graph.shared.ActivityInfoNamespace.uri;


public class RdfExperiment {

    // some definitions
    static String personURI    = "http://somewhere/JohnSmith";
    static String fullName     = "John Smith";
    private final Dataset dataset;
    private final Store store;


    public static void main(String...argv) throws IOException, ClassNotFoundException {
        // Setup - make the JDBC connection and read the tree description once.
        //StoreDesc storeDesc = StoreDesc.read("sdb-tree.ttl") ;

        // Make a tree description without any connection information.

        RdfExperiment experiment = new RdfExperiment();
        experiment.loadTables();

        //experiment.querySiteTable();

        experiment.query(Sparql.select("?p", "?v")
                .where(Sparql.uri(uri(LocationType.class, 1)), "?p", "?v")
                .toString());


                // experiment.query(String.format("SELECT ?d ?l WHERE { ?d <%s> <%s> ." +
                //                                     " ?d <%s> ?l  }",
//                ActivityInfoNamespace.SUB_DIVISION_OF, ActivityInfoNamespace.uri(Country.class, 1),
//                ActivityInfoNamespace.RDFS_LABEL));
                experiment.close();

        // Make some calls to the tree, using the same JDBC connection and tree description.
//        System.out.println("Subjects: ") ;
//        query("SELECT DISTINCT ?s { ?s ?p ?o }", storeDesc, jdbc) ;
//        System.out.println("Predicates: ") ;
//        query("SELECT DISTINCT ?p { ?s ?p ?o }", storeDesc, jdbc) ;
//        System.out.println("Objects: ") ;
//        query("SELECT DISTINCT ?o { ?s ?p ?o }", storeDesc, jdbc) ;
    }

    public RdfExperiment() {
        Connection jdbc = makeConnection() ;

        SDBConnection conn = new SDBConnection(jdbc) ;

        StoreDesc storeDesc = new StoreDesc(LayoutType.LayoutTripleNodesHash,
                DatabaseType.MySQL) ;

        store = StoreFactory.create(storeDesc, conn);
       // tree.getTableFormatter().truncate();

        dataset = DatasetStore.create(store);
    }


    public void close() {
        store.close() ;
    }


    public void query(String queryString)
    {
        OntModel model1 = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());

        Query query = QueryFactory.create(queryString) ;

        QueryExecution qe = QueryExecutionFactory.create(query, model1) ;
        try {
            ResultSet rs = qe.execSelect() ;
            ResultSetFormatter.out(rs) ;
        } finally { qe.close() ; }
        // Does not close the JDBC connection.
        // Do not call : tree.getConnection().close() , which does close the underlying connection.
        store.close() ;
    }

    public void querySiteTable() {

        query(String.format("SELECT ?rp ?p ?v WHERE { ?rp <%s> <%s> ." +
                " ?rp  ?p  ?v   }",  ActivityInfoNamespace.REPORTS_ON, ActivityInfoNamespace.uri(Site.class, 2000488932)));

    }

    public static Connection makeConnection()
    {
        try {
            return DriverManager.getConnection("jdbc:mysql://localhost/activityinfo?useUnicode=true&characterEncoding=utf8",
                    "root", "root") ;
        } catch (SQLException ex)
        {
            throw new SDBException("SQL Exception while connecting to database: "+ex.getMessage()) ;
        }
    }

    public void loadTables() throws FileNotFoundException, IOException, ClassNotFoundException {

        //
        //		  dataset.begin(ReadWrite.WRITE) ;

        // open our sql database
        EntityManagerFactory emf = createEmf();
        EntityManager em = emf.createEntityManager();


        OntModel om = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, dataset.getDefaultModel());

        om.read(getClass().getResource("/org/activityinfo/legacy.owl").toString());

//		Activity activity = em.find(Activity.class, 33);
//
//        loadActivity(em, om, activity);

        GraphLoader loader = new GraphLoader(em, store, om);
       // loader.loadLocations();
        //loader.loadLocations();
//
//        loader.loadUsersTable();
//        loader.loadDatabases();
//        loader.loadPartnerTable();
//        loader.loadActivityTable();
//        loader.loadSites();
        loader.loadReportingPeriods();
        loader.loadIndicatorValues();

        om.close();
    }

    private OntModel createOntModel() {
        OntModelSpec spec = OntModelSpec.OWL_MEM;
        return ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_MICRO_RULE_INF, dataset.getDefaultModel());
    }

    public static EntityManagerFactory createEmf() {
        Ejb3Configuration config = new Ejb3Configuration();
        config.setProperty("hibernate.dialect",
                "org.hibernate.spatial.dialect.mysql.MySQLSpatialInnoDBDialect");
        config.setProperty("hibernate.connection.driver_class",
                "com.mysql.jdbc.Driver");
        config.setProperty("hibernate.connection.url",
                "jdbc:mysql://localhost/activityinfo?useUnicode=true&characterEncoding=utf8");
        config.setProperty("hibernate.connection.username",
                "root");
        config.setProperty("hibernate.connection.password",
                "root");
        config.setProperty("hibernate.hbm2ddl.auto", "none");
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.connection.pool_size", "0");
        config.setNamingStrategy(new AINamingStrategy());
        for (Class clazz : HibernateModule
                .getPersistentClasses()) {
            config.addAnnotatedClass(clazz);
        }
        return config.buildEntityManagerFactory();
    }
}
