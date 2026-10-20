package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {

        /*EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        System.out.println("Hibernate forbandt og byggede skemaet uden fejl!"); // Test if Hibernate works
        emf.close();*/

        ApplicationConfig.startServer(7070);
    }
}
