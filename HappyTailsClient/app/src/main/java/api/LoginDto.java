package api;

import java.io.Serializable;

public class LoginDto implements Serializable {

    private int id;
    private String email;

    public LoginDto() {
    }

    public LoginDto(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "LoginResponse{" +
                "id=" + id +
                ", email='" + email + '\'' +
                '}';
    }


}
