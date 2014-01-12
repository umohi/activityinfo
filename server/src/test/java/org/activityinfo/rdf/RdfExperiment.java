package org.activityinfo.rdf;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.activityinfo.server.DeploymentEnvironment;
import org.activityinfo.server.database.TestConnectionProvider;
import org.activityinfo.server.database.hibernate.AINamingStrategy;
import org.activityinfo.server.database.hibernate.SchemaServlet;
import org.activityinfo.server.database.hibernate.entity.Activity;
import org.activityinfo.server.database.hibernate.entity.Indicator;
import org.activityinfo.test.MockHibernateModule;
import org.apache.jena.atlas.lib.NotImplemented;
import org.hibernate.cfg.Environment;
import org.hibernate.ejb.Ejb3Configuration;
import org.hibernate.ejb.HibernateEntityManager;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.ontology.Ontology;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ModelGetter;
import com.hp.hpl.jena.rdf.model.ModelReader;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.tdb.TDB;
import com.hp.hpl.jena.tdb.TDBFactory;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.mysql.jdbc.Driver;


public class RdfExperiment {

	// some definitions
	static String personURI    = "http://somewhere/JohnSmith";
	static String fullName     = "John Smith";

	public static void main(String[] args) throws FileNotFoundException, IOException, ClassNotFoundException {
		//	

		Class.forName(Driver.class.getName());
		
		TestConnectionProvider.URL = "http://localhost/activityinfo";
		//		 
		//		  dataset.begin(ReadWrite.WRITE) ;

		// open our sql database
		EntityManagerFactory emf = createEmf();
		EntityManager em = emf.createEntityManager();
		
		// Make a TDB-backed dataset
		Dataset dataset = initialiseTDB();
		

        OntModelSpec spec = new OntModelSpec( OntModelSpec.OWL_MEM );
        spec.setImportModelGetter( new LocalTDBModelGetter( dataset ) );
 
        OntModel om = ModelFactory.createOntologyModel( spec, dataset.getDefaultModel() );
 
        
		Activity activity = em.find(Activity.class, 33);

		String databaseUri = "http://www.activityinfo.org/rdf/db/" + activity.getDatabase().getId();
		
		String activityUri = databaseUri + "/activity/" + activity.getId();
		
		
		OntClass classForActivity = om.createClass(activityUri);
		
		classForActivity.setLabel(activity.getName(), "en");
		classForActivity.setSuperClass( om.getOntClass( Hxl.NS + "LocatedBase" ) );

		for(Indicator indicator : activity.getIndicators()) {
			
			DatatypeProperty propertyForIndicator = om.createDatatypeProperty(activityUri + "/" + indicator);
			propertyForIndicator.setDomain(classForActivity);
			propertyForIndicator.setLabel(indicator.getCategory(), "en");
		}
		
		dataset.end();
	}

	public static EntityManagerFactory createEmf() throws FileNotFoundException, IOException {

		
		
		MockHibernateModule module = new MockHibernateModule();
		return module.createEmf();
	}
	

    /**
     * Initialise the local TDB image if necessary.
     */
    private static Dataset initialiseTDB() {
        String tdbPath = "/home/alex/tdb";
        new File( tdbPath ).mkdirs();
        return TDBFactory.createDataset( tdbPath );
    }

    static void loadTDBContent( Dataset ds ) {
        if (!ds.containsNamedModel( Hxl.NS )) {
            loadExampleGraph( Hxl.NS, ds, "The Dread Pirate Roberts" );
        }
    }
    /**
     * Create a graph with the given name in the dataset, and initialise it with
     * some fake content (namely an owl:Ontology resource). This is a proxy for
     * loading a real ontology into the model.
     *
     * @param graphName
     * @param ds
     * @param creator
     */
    static void loadExampleGraph( String graphName, Dataset ds, String creator ) {
        Model m = ModelFactory.createDefaultModel();
 
        m.createResource( graphName )
         .addProperty( RDF.type, OWL.Ontology)
         .addProperty( DCTerms.creator, creator );
 
        ds.addNamedModel( graphName, m );
        TDB.sync( m );
    }
    
    
    /**
     * <p>A type of model getter that loads models from a local TDB instance,
     * if they exist as named graphs using the model URI as the graph name.</p>
     */
    static class LocalTDBModelGetter implements ModelGetter {
 
        private Dataset ds;
 
        public LocalTDBModelGetter( Dataset dataset ) {
            ds = dataset;
        }
 
        @Override
        public Model getModel( String uri ) {
            throw new NotImplemented( "getModel( String  ) is not implemented" );
        }
 
        @Override
        public Model getModel( String uri, ModelReader loadIfAbsent ) {
            Model m = ds.getNamedModel( uri );
 
            // create the model if necessary. In actual fact, this example code
            // will not exercise this code path, since we pre-define the models
            // we want to see in TDB
            if (m == null) {
                m = ModelFactory.createDefaultModel();
                loadIfAbsent.readModel( m, uri );
                ds.addNamedModel( uri, m );
            }
 
            return m;
        }
    } // LocalTDBModelGetter
}
