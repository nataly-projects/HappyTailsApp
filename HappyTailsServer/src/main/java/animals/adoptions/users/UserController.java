package animals.adoptions.users;

import animals.adoptions.entities.User;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/users")
public class UserController {

    @POST
    @Path("/signing")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CreateUserDto signIn(SigningDto signingDto) {
        CreateUserDto user = UserService.signing(signingDto);
        if(user != null){
            return user;
        } else{
            return null;
        }
    }

    @POST
    @Path("/signup")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public User signUp(CreateUserDto createUserDto) {
        return UserService.signup(createUserDto);
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public UpdateUserDto updateUser(@PathParam("id") int id, UpdateUserDto userUpdateDto) {
        return UserService.updateUser(id, userUpdateDto);
    }

    @DELETE
    @Path("/{id}")
    public void deleteUser(@PathParam("id") int id) {
        UserService.deleteUser(id);
    }


    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUserById(@PathParam("id") int id) {
        return UserService.getUserById(id);
    }

    @POST
    @Path("/contact_us")
    @Consumes(MediaType.APPLICATION_JSON)
    public void contactUs(ContactUsDto contactUsDto) {
        UserService.contactUs(contactUsDto);
    }

    @POST
    @Path("/reset_password_code")
    public String resetPasswordCode(@QueryParam("email") String email) {
        return UserService.resetPasswordCode(email);
    }

    @POST
    @Path("/reset_password")
    @Consumes(MediaType.APPLICATION_JSON)
    public void resetPassword(ResetPasswordDto resetPasswordDto) {
        UserService.resetPassword(resetPasswordDto);
    }

}
