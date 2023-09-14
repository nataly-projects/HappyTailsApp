package animals.adoptions;

import animals.adoptions.animals.AnimalsController;
import animals.adoptions.requests.RequestsController;
import animals.adoptions.categories.CategoriesController;
import animals.adoptions.users.UserController;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.io.IOException;
import java.net.URI;


@ApplicationPath("/api")
public class HappyTailsServer extends Application {

    public static final String BASE_URI = "http://localhost:8080/api";

    public HappyTailsServer() {
    }

    public static HttpServer startServer() {

        final ResourceConfig rc = new ResourceConfig().register(UserController.class).register(AnimalsController.class)
                .register(CategoriesController.class).register(RequestsController.class);

        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI),rc);
    }


    public static void main(String[] args) throws IOException {

        final HttpServer server = startServer();
        server.start();
        System.out.println(String.format("Server start... available at "
                + "%s enter to stop it...", BASE_URI));
        System.in.read();
        server.stop();

    }

}

