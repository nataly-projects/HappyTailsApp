package animals.adoptions.requests;

import animals.adoptions.entities.Request;
import animals.adoptions.users.UserDetailsDto;


public class CreateRequestDto {

    private int id;

    private UserDetailsDto userReqId;

    private UserDetailsDto ownerId;

    private String status;

    private String animalName;

    private int animalId;

    private String message;



    public CreateRequestDto() {
    }

    public CreateRequestDto(UserDetailsDto userReqId, UserDetailsDto ownerId, String status, String animal_name, int animalId, String message) {
        this.userReqId = userReqId;
        this.ownerId = ownerId;
        this.status = status;
        this.animalName = animal_name;
        this.animalId = animalId;
        this.message = message;
    }

    public CreateRequestDto(Request request) {
        this.id = request.getId();
        this.status = request.getStatus();
        this.animalName = request.getAnimalName();
        this.animalId = request.getAnimalId();
        this.message = request.getMessage();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDetailsDto getUserReqId() {
        return userReqId;
    }

    public void setUserReqId(UserDetailsDto userReqId) {
        this.userReqId = userReqId;
    }

    public UserDetailsDto getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UserDetailsDto ownerId) {
        this.ownerId = ownerId;
    }

    public int getAnimalId() {
        return animalId;
    }

    public void setAnimalId(int animalId) {
        this.animalId = animalId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "CreateRequestDto{" +
                "id=" + id +
                ", userReqId=" + userReqId +
                ", ownerId=" + ownerId +
                ", status='" + status + '\'' +
                ", animal_name='" + animalName + '\'' +
                ", animalId=" + animalId +
                ", message='" + message + '\'' +
                '}';
    }
}
