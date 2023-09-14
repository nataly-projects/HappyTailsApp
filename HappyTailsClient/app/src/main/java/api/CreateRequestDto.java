package api;

import java.io.Serializable;

import models.User;

public class CreateRequestDto implements Serializable {

    private User userReqId;

    private User ownerId;

    private String status;

    private String animalName;

    private int animalId;

    private String message;



    public CreateRequestDto() {
    }

    public CreateRequestDto(User userReqId, User ownerId, String status, String animal_name, int animalId, String message) {
        this.userReqId = userReqId;
        this.ownerId = ownerId;
        this.status = status;
        this.animalName = animal_name;
        this.animalId = animalId;
        this.message = message;
    }

    public User getUserReqId() {
        return userReqId;
    }

    public void setUserReqId(User userReqId) {
        this.userReqId = userReqId;
    }

    public User getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(User ownerId) {
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
                "userReqId=" + userReqId +
                ", ownerId=" + ownerId +
                ", status='" + status + '\'' +
                ", animal_name='" + animalName + '\'' +
                ", animalId=" + animalId +
                ", message='" + message + '\'' +
                '}';
    }
}
