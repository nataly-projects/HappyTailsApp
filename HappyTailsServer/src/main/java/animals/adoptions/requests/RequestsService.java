package animals.adoptions.requests;

import animals.adoptions.EntityManagerFactoryProvider;
import animals.adoptions.GMailer;
import animals.adoptions.animals.AnimalStatus;
import animals.adoptions.animals.AnimalsService;
import animals.adoptions.entities.Request;
import animals.adoptions.entities.User;
import animals.adoptions.users.UserDetailsDto;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;
import java.util.ArrayList;
import java.util.List;

public class RequestsService {
    private static final EntityManagerFactory entityManagerFactory = EntityManagerFactoryProvider.getEntityManagerFactory();
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();

    public static void addRequest(CreateRequestDto createRequestDto)  {
        Request request = new Request(createRequestDto);
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(request);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
            throw ex;
        }

        // change the animal status to 'hidden'
        AnimalStatus hiddenStatus = AnimalStatus.HIDDEN;
        try {
            AnimalsService.updateAnimalStatus(request.getAnimalId(), hiddenStatus.getDisplayName());
        } catch (Exception ex) {
            System.err.println("Error changing animal status: " + ex.getMessage());
            ex.printStackTrace();
        }

        //send email to the owner
        User user = entityManager.find(User.class, request.getUserReqId().getId());
        if(user != null) {
            String message = "Dear " + request.getOwnerId().getFullName()  + "\n\n You have a new requests for adopt " + request.getAnimalName()
                    + " you can get more info and approve or deny it the the app";
            try {
                new GMailer().sendAdoptMail(message, request.getOwnerId().getEmail());
            } catch (Exception ex) {
                System.err.println("Error sending email: " + ex.getMessage());
                ex.printStackTrace();
            }
        }

    }

    public static List<CreateRequestDto> getUserRequests(int userId) {
        User user = entityManager.find(User.class, userId);

        if (user != null) {
            TypedQuery<Request> query = entityManager.createQuery("SELECT r FROM Request r WHERE r.ownerId.id = :ownerId ", Request.class);
            query.setParameter("ownerId", userId);
            List<Request> requests = query.getResultList();
            List<CreateRequestDto> resRequests = new ArrayList<> ();
            for (Request request : requests) {
                CreateRequestDto createRequestDto = createRequestDto(request);
                resRequests.add(createRequestDto);
            }
            return resRequests;
        }
        return null;
    }

    private static CreateRequestDto createRequestDto(Request request) {
        CreateRequestDto createRequestDto = new CreateRequestDto(request);
        User userRequest = request.getUserReqId();

        if (userRequest != null) {
            UserDetailsDto userRequestDto = new UserDetailsDto();
            userRequestDto.setFullName(userRequest.getFullName());
            userRequestDto.setId(userRequest.getId());
            userRequestDto.setPhone(userRequest.getPhone());
            userRequestDto.setEmail(userRequest.getEmail());
            createRequestDto.setUserReqId(userRequestDto);
        }
        User owner = request.getOwnerId();
        if (owner != null) {
            UserDetailsDto ownerDto = new UserDetailsDto();
            ownerDto.setFullName(owner.getFullName());
            ownerDto.setId(owner.getId());
            ownerDto.setPhone(owner.getPhone());
            ownerDto.setEmail(owner.getEmail());
            createRequestDto.setOwnerId(ownerDto);
        }
        return createRequestDto;
    }



    public static void acceptRequest(int reqId) {
        try {
            Request request = entityManager.find(Request.class, reqId);

            // remove animal
            AnimalsService.deleteAnimal(request.getAnimalId());

            // change the status of the request to accept
            RequestStatus acceptStatus = RequestStatus.ACCEPT;
            updateRequestStatus(reqId, acceptStatus.getDisplayName());
        } catch (Exception e) {
            System.err.println("Error accepting request: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void denyRequest(int reqId) {
        Request request = entityManager.find(Request.class, reqId);

        // change the animal status back to 'active'
        AnimalStatus activeStatus = AnimalStatus.ACTIVE;
        try {
            AnimalsService.updateAnimalStatus(request.getAnimalId(), activeStatus.getDisplayName());
        } catch (Exception ex) {
            System.err.println("Error changing animal status: " + ex.getMessage());
            ex.printStackTrace();
        }

        // change the status of the request to deny
        RequestStatus denyStatus = RequestStatus.DENY;
        try {
            updateRequestStatus(reqId, denyStatus.getDisplayName());
        } catch (Exception ex) {
            System.err.println("Error updating request status: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private static void updateRequestStatus(int reqId, String status) {
        try {
            Request request = entityManager.find(Request.class, reqId);
            if (request != null) {
                request.setStatus(status);
                entityManager.getTransaction().begin();
                entityManager.merge(request);
                entityManager.getTransaction().commit();
            }
        } catch (Exception ex) {
            System.err.println("Error updating request status: " + ex.getMessage());
            ex.printStackTrace();
        }
    }


}
