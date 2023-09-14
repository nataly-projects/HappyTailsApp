package animals.adoptions.requests;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Path("/requests")
public class RequestsController {

    @GET
    @Path("/user_requests")
    @Produces(MediaType.APPLICATION_JSON)
    public List<CreateRequestDto> getUserRequests(@QueryParam("userId") int userId) {
        return RequestsService.getUserRequests(userId);
    }


    @POST
    @Path("/add")
    @Consumes(MediaType.APPLICATION_JSON)
    public void addRequest(CreateRequestDto createRequestDto) {
        RequestsService.addRequest(createRequestDto);
    }


    @DELETE
    @Path("/accept")
    public void acceptRequest(@QueryParam("reqId") int reqId) {
        RequestsService.acceptRequest(reqId);
    }

    @PUT
    @Path("/deny")
    @Consumes(MediaType.APPLICATION_JSON)
    public void denyRequest(@QueryParam("reqId") int reqId) {
        RequestsService.denyRequest(reqId);
    }




}
