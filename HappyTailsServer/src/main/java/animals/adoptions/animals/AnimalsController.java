package animals.adoptions.animals;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/animals")
public class AnimalsController {

    @GET
    @Path("/animals")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CreateAnimalDto> getAllAnimals(@QueryParam("category") String categoryFilter) {
        return AnimalsService.getAnimals(categoryFilter);
    }

    @GET
    @Path("/user_animals")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CreateAnimalDto> getAllAnimalsByUser(@QueryParam("userId") int userId) {
        return AnimalsService.getAnimalsByUser(userId);
    }


    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addAnimal(CreateAnimalDto createAnimalDto)  {
        AnimalsService.addAnimal(createAnimalDto);
    }

    @DELETE
    @Path("/delete")
    public void deleteAnimal(@QueryParam("animal_id") int id) {
        AnimalsService.deleteAnimal(id);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreateAnimalDto updateAnimal(@PathParam("id") int id, UpdateAnimalDto updateAnimalDto)  {
        return AnimalsService.updateAnimal(id, updateAnimalDto);
    }


}
