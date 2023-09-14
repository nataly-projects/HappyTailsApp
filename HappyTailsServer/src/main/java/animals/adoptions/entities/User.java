package animals.adoptions.entities;

import animals.adoptions.users.CreateUserDto;
import animals.adoptions.users.UpdateUserDto;
import animals.adoptions.users.UserDetailsDto;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Blob;

@Entity
@Table(name="users", schema="animals_adoptions")
@XmlRootElement
public class User implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "full_name", nullable=false, length=300)
    private String fullName;

    @Column(name= "phone", nullable=false, length=200)
    private String phone;

    @Column(name= "email",unique = true, nullable=false, length=200)
    private String email;

    @Column(name= "password", nullable=false)
    private String password;

    @Column(name= "image")
    private Blob image;


    public User() {
    }

    public User(int id, String fullName, String phone, String email, String password, Blob image) {
        this.id = id;
        this.fullName = fullName;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.image = image;
    }


    public User(int id, String email) {
        this.id = id;
        this.email = email;
    }

    public User(CreateUserDto createUserDto) {
        this.fullName = createUserDto.getFullName();
        this.phone = createUserDto.getPhone();
        this.email = createUserDto.getEmail();
        this.password = createUserDto.getPassword();
    }

    public User(UpdateUserDto userUpdateDto) {
        this.fullName = userUpdateDto.getFullName();
        this.phone = userUpdateDto.getPhone();
        this.email = userUpdateDto.getEmail();
    }

    public User(UserDetailsDto userDetailsDto) {
        this.fullName = userDetailsDto.getFullName();
        this.id = userDetailsDto.getId();
        this.email = userDetailsDto.getEmail();
        this.phone = userDetailsDto.getPhone();
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

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }



    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", fullName='" + fullName + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", image=" + image + '\'' +
                '}';
    }
}
