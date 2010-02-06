package it.polimi.data.hibernate;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Environment;

public class HibernateUtil {

    private static SessionFactory sessionFactory = null;
    private static boolean resetDatabase = false;

    private static void buildSessionFactory() {
        try {
            // Create the SessionFactory from hibernate.cfg.xml
        	AnnotationConfiguration configuration = new AnnotationConfiguration().configure();
			if(resetDatabase){
        		configuration.setProperty(Environment.HBM2DDL_AUTO, "create");
        	}
        	sessionFactory =  configuration.buildSessionFactory();
        }
        catch (Throwable ex) {
            // Make sure you log the exception, as it might be swallowed
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
    	if(sessionFactory == null){
    		buildSessionFactory();
    	}
        return sessionFactory;
    }
    
    public static void resetDatabase() throws Exception{
    	if(sessionFactory != null){
    		throw new Exception("A session factory has already been created");
    	}
    	resetDatabase = true;
    }

}