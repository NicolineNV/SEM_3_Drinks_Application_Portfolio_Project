package app;

import app.config.ApplicationConfig;
import app.config.HibernateConfig;
import app.routes.Routes;
import app.security.AccessHandler;
import app.security.TokenSecurity;
import app.utils.Utils;
import jakarta.persistence.EntityManagerFactory;

public class RunServer {

    public static void runServer () {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactory();
        TokenSecurity tokenSecurity = new TokenSecurity(
                Utils.setting("SECRET_KEY"),
                Utils.setting("ISSUER"),
                Long.parseLong(Utils.setting("TOKEN_EXPIRE_TIME")));

        new ApplicationConfig(new Routes(emf, tokenSecurity), new AccessHandler(tokenSecurity))
                .startServer(7070);
    }
}
