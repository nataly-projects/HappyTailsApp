package animals.adoptions.animals;

import animals.adoptions.entities.Animal;
import animals.adoptions.entities.Category;
import animals.adoptions.users.UserDetailsDto;


public class CreateAnimalDto {

    private int id;
    private String name;
    private Category category;
    private String gender;
    private int age;
    private int weight;
    private String description;
    private String image;
    private UserDetailsDto owner;
    private String status;


    public CreateAnimalDto() {
    }

    public CreateAnimalDto(int id, String name, Category category, String gender, int age, int weight, String description, String image, String status) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.description = description;
        this.image = image;
        this.status = status;
    }

    public CreateAnimalDto(Animal animal) {
        this.id = animal.getId();
        this.name = animal.getName();
        this.category = animal.getCategory();
        this.age = animal.getAge();
        this.weight = animal.getWeight();
        this.description = animal.getDescription();
        this.gender = animal.getGender();
        this.status = animal.getStatus();
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

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public UserDetailsDto getOwner() {
        return owner;
    }

    public void setOwner(UserDetailsDto owner) {
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
        return "CreateAnimalDto{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", category=" + category +
                ", gender='" + gender + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                ", owner=" + owner +
                ", status='" + status + '\'' +
                '}';
    }
}
