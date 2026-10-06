package app.config;

import jakarta.persistence.EntityManagerFactory;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.util.Properties;

public final class TestHibernateConfig {

    private static PostgreSQLContainer container;
    private static EntityManagerFactory emf;

    private TestHibernateConfig () {}

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            startContainer();
            emf = HibernateEmfBuilder.build(buildProps());
        }
        return emf;
    }

    private static void startContainer() {
        container = new PostgreSQLContainer("postgres:16")
                .withDatabaseName("test_db")
                .withUsername("test")
                .withPassword("test");
        container.start();
    }

    private static Properties buildProps() {
        Properties props = HibernateBaseProperties.createBase();
        props.put("hibernate.hbm2ddl.auto", "create-drop");
        props.put("hibernate.connection.url", container.getJdbcUrl());
        props.put("hibernate.connection.username", container.getUsername());
        props.put("hibernate.connection.password", container.getPassword());
        return props;
    }
}