package animals.adoptions;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

public class EntityManagerFactoryProvider {
    public static EntityManagerFactory getEntityManagerFactory() {
        // Load environment variables or set default values
        String dbUsername = System.getenv("DB_USERNAME");
        String dbPassword = System.getenv("DB_PASSWORD");
        String dbUrl = System.getenv("DB_URL");


        // Create properties with resolved values
        Map<String, String> properties = new HashMap<>();
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("javax.persistence.jdbc.url", dbUrl);
        properties.put("javax.persistence.jdbc.user", dbUsername);
        properties.put("javax.persistence.jdbc.password", dbPassword);
        properties.put("javax.persistence.jdbc.driver", "com.mysql.jdbc.Driver");
        properties.put("hibernate.hbm2ddl.auto", "update");


        // Create the EntityManagerFactory with the updated properties
        return Persistence.createEntityManagerFactory("users-persistence", properties);
    }
}

