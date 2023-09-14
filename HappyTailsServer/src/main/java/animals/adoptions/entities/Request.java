package animals.adoptions.entities;


import animals.adoptions.requests.CreateRequestDto;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@Entity
@Table(name="requests", schema="animals_adoptions")
@XmlRootElement
public class Request implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "user_req_id", nullable=false)
    private User userReqId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable=false)
    private User ownerId;

    @Column(name= "status", nullable=false, length=40)
    private String status;

    @Column(name= "animal_name", nullable=false, length=100)
    private String animalName;

    @Column(name= "animal_id", nullable=false)
    private int animalId;

    @Column(name= "message")
    private String message;

    public Request() {
    }

    public Request(int id, User userReqId, User ownerId, String status, String animal_name, int animalId, String message) {
        this.id = id;
        this.userReqId = userReqId;
        this.ownerId = ownerId;
        this.status = status;
        this.animalName = animal_name;
        this.animalId = animalId;
        this.message = message;
    }

    public Request(CreateRequestDto createRequestDto) {
        this.userReqId = new User(createRequestDto.getUserReqId());
        this.ownerId = new User(createRequestDto.getOwnerId());
        this.animalId = createRequestDto.getAnimalId();
        this.status = createRequestDto.getStatus();
        this.animalName = createRequestDto.getAnimalName();
        this.message = createRequestDto.getMessage();
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAnimalName() {
        return animalName;
    }

    public void setAnimalName(String animalName) {
        this.animalName = animalName;
    }

    @Override
    public String toString() {
        return "Request{" +
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
