package api;

import java.io.Serializable;

import models.Animal;
import models.Category;

public class UpdateAnimalDto implements Serializable {
    private String name;
    private Category category;
    private int age;
    private int weight;
    private String gender;
    private String description;
    private String image;


    public UpdateAnimalDto() {
    }


    public UpdateAnimalDto(Animal animal) {
        this.name = animal.getName();
        this.category = animal.getCategory();
        this.age = animal.getAge();
        this.weight = animal.getWeight();
        this.gender = animal.getGender();
        this.description = animal.getDescription();
        this.image = animal.getImage();
    }

    public UpdateAnimalDto(String name, Category category, int age, int weight, String gender, String description, String image) {
        this.name = name;
        this.category = category;
        this.age = age;
        this.weight = weight;
        this.gender = gender;
        this.description = description;
        this.image = image;
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

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    @Override
    public String toString() {
        return "UpdateAnimalDto{" +
                "name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", age=" + age +
                ", weight=" + weight +
                ", gender='" + gender + '\'' +
                ", description='" + description + '\'' +
                ", image='" + image + '\'' +
                '}';
    }
}
