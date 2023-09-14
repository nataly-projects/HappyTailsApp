package api;

import java.io.Serializable;

public class ContactUsDto implements Serializable {

    private int userId;
    private String name;
    private String email;
    private String message;

    public ContactUsDto() {
    }

    public ContactUsDto(int userId, String name, String email, String message) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
