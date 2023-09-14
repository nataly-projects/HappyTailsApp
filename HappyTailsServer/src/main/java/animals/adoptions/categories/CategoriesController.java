package animals.adoptions.categories;

import animals.adoptions.entities.Category;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/categories")
public class CategoriesController {

    @GET
    @Path("/")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Category> getCategories()  {
        return CategoriesService.getCategories();
    }

    @POST
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Category addCategory(Category category)  {
        return CategoriesService.addCategory(category);
    }



}
