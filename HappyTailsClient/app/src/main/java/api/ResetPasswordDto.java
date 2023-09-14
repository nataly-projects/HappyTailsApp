package api;

import java.io.Serializable;

public class ResetPasswordDto implements Serializable {

    private String email;
    private String password;


    public ResetPasswordDto() {
    }

    public ResetPasswordDto(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return "ResetPasswordDto{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

}
