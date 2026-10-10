package app.seed;

public class RunImport {
    public static void main(String[] args) {
        var emf = app.config.HibernateConfig.getEntityManagerFactory();
        new CocktailImporter(emf).run(Integer.MAX_VALUE);
        emf.close();
    }
}
