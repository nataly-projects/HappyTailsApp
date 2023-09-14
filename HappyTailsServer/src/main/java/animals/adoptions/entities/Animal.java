package animals.adoptions.entities;

import animals.adoptions.animals.CreateAnimalDto;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.sql.Blob;

@Entity
@Table(name="animals", schema="animals_adoptions")
@XmlRootElement
public class Animal implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(name= "name", nullable=false, length=200)
    private String name;

    @Column(name= "age", nullable=false, length=200)
    private int age;

    @Column(name= "gender", nullable=false, length=200)
    private String gender;

    @Column(name= "weight", nullable=false, length=200)
    private int weight;

    @Column(name= "description", length=200)
    private String description;

    @Column(name= "status",nullable=false,  length=200)
    private String status;

    @Column(name= "image")
    private Blob image;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", nullable=false)
    private Category category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "owner_id", nullable=false)
    private User owner;

    public Animal() {
    }

    public Animal(int id, String name, int age, String gender, int weight, String description, String status, Blob image, Category category, User owner) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.gender = gender;
        this.weight = weight;
        this.description = description;
        this.status = status;
        this.image = image;
        this.category = category;
        this.owner = owner;
    }

    public Animal(CreateAnimalDto createAnimalDto) {
        this.id = createAnimalDto.getId();
        this.name = createAnimalDto.getName();
        this.category = createAnimalDto.getCategory();
        this.age = createAnimalDto.getAge();
        this.weight = createAnimalDto.getWeight();
        this.description = createAnimalDto.getDescription();
        this.gender = createAnimalDto.getGender();
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Blob getImage() {
        return image;
    }

    public void setImage(Blob image) {
        this.image = image;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Animal{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", age=" + age +
                ", gender='" + gender + '\'' +
                ", weight=" + weight +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", image=" + image +
                ", category=" + category +
                ", owner=" + owner +
                '}';
    }
}
