package animals.adoptions.users;

import animals.adoptions.entities.User;

public class CreateUserDto {

    private int id;
    private String fullName;

    private String phone;

    private String email;

    private String password;

    private String image;


    public CreateUserDto() {
    }

    public CreateUserDto(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.phone = user.getPhone();
        this.email = user.getEmail();
        this.password = user.getPassword();
    }


    public CreateUserDto(int id, String fullName, String phone, String email, String password, String image) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "CreateUserDto{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
