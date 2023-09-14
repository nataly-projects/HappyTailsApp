package models;

import java.io.Serializable;

public class Request implements Serializable {

    private int id;

    private User userReqId;

    private User ownerId;

    private String status;

    private String animalName;

    private int animalId;

    private String message;


    public Request() {
    }

    public Request(int id, User userReqId, User ownerId, String status, String animalName, int animalId, String message) {
        this.id = id;
        this.userReqId = userReqId;
        this.ownerId = ownerId;
        this.status = status;
        this.animalName = animalName;
        this.animalId = animalId;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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
        return "Request{" +
                "id=" + id +
                ", userReqId=" + userReqId +
                ", ownerId=" + ownerId +
                ", status='" + status + '\'' +
                ", animalName='" + animalName + '\'' +
                ", animalId=" + animalId +
                ", message='" + message + '\'' +
                '}';
    }
}
