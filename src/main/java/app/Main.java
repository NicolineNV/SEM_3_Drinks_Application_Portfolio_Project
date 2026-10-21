package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.routes.Routes;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    public static void main(String[] args) {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();

        Routes routes = new Routes(emf);
        new ApplicationConfig(routes).startServer(7070);
    }
}
