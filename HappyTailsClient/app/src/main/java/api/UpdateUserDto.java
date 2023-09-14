package api;

import java.io.Serializable;

import models.User;

public class UpdateUserDto implements Serializable {
    private String fullName;
    private String email;
    private String phone;
    private String image;


    public UpdateUserDto() {
    }

    public UpdateUserDto(User user) {
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.image = user.getImage();
    }

    public UpdateUserDto(String fullName, String email, String phone, String image) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.image = image;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "UpdateUserDto{" +
                "fullName='" + fullName + '\'' +
                ", email='" + email + '\'' +
                ", phone='" + phone + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
