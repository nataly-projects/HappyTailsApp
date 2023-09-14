package animals.adoptions.animals;

import animals.adoptions.EntityManagerFactoryProvider;
import animals.adoptions.entities.Animal;
import animals.adoptions.entities.Category;
import animals.adoptions.entities.User;
import animals.adoptions.users.UserDetailsDto;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import javax.persistence.*;
import javax.sql.rowset.serial.SerialBlob;
import javax.xml.bind.DatatypeConverter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AnimalsService {
    private static final EntityManagerFactory entityManagerFactory = EntityManagerFactoryProvider.getEntityManagerFactory();
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();


    public static List<CreateAnimalDto> getAnimals(String categoryFilter) {
        try{
        TypedQuery<Animal> query;
        if (categoryFilter == null) {
            query = entityManager.createQuery("SELECT a FROM Animal a JOIN FETCH a.owner", Animal.class);

        } else {
            Gson gson = new Gson();
            Category category = gson.fromJson(categoryFilter, Category.class);

            query = entityManager.createQuery("SELECT a FROM Animal a JOIN FETCH a.owner WHERE a.category = :category", Animal.class);
            query.setParameter("category", category);
        }

            List<Animal> animals = query.getResultList();
            List<CreateAnimalDto> resAnimalList = new ArrayList<>();
            for (Animal animal : animals) {
                CreateAnimalDto createAnimalDto = createAnimalDtoFromAnimal(animal);
                resAnimalList.add(createAnimalDto);
            }
            return resAnimalList;
        }catch (JsonSyntaxException e) {
            // Handle Gson deserialization error
            System.err.println("Error during JSON deserialization: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            // Handle SQL-related exceptions
            System.err.println("Error executing SQL query: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public static List<CreateAnimalDto> getAnimalsByUser(int userId) {
        try {
            User user = entityManager.find(User.class, userId);
            if (user != null) {
                TypedQuery<Animal> query = entityManager.createQuery("SELECT a FROM Animal a JOIN FETCH a.owner WHERE a.owner.id = :ownerId", Animal.class);
                query.setParameter("ownerId", userId);

                List<Animal> animals = query.getResultList();
                List<CreateAnimalDto> resAnimalList = new ArrayList<>();
                for (Animal animal : animals) {
                    CreateAnimalDto createAnimalDto = createAnimalDtoFromAnimal(animal);
                    resAnimalList.add(createAnimalDto);
                }
                return resAnimalList;
            }
        }catch (SQLException e) {
            System.err.println("Error executing SQL query: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }



    public static void addAnimal(CreateAnimalDto createAnimalDto)  {
        try {
            Animal animal = new Animal(createAnimalDto);
            animal.getOwner().setId(createAnimalDto.getOwner().getId());
            animal.getOwner().setFullName(createAnimalDto.getOwner().getFullName());
            animal.getOwner().setEmail(createAnimalDto.getOwner().getEmail());

            // set the status of the new animal to active
            AnimalStatus activeStatus = AnimalStatus.ACTIVE;
            animal.setStatus(activeStatus.getDisplayName());

            if (createAnimalDto.getImage() != null) {
                String imageBase64 = createAnimalDto.getImage();

                byte[] imageBytes = DatatypeConverter.parseBase64Binary(imageBase64);
                // Convert the byte array to a Blob object
                Blob imageBlob = new SerialBlob(imageBytes);
                animal.setImage(imageBlob);
                animal.getOwner().setPassword(null);
            }
            try {
                entityManager.getTransaction().begin();
                entityManager.persist(animal);
                entityManager.getTransaction().commit();
            } catch (Exception ex) {
                entityManager.getTransaction().rollback();
                throw ex; // Rethrow the exception to propagate it
            }
        }catch (SQLException e) {
            // Handle SQL exception and SerialBlob exception
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public static void deleteAnimal(int id) {
        try {
            Animal animal = entityManager.find(Animal.class, id);
            entityManager.getTransaction().begin();
            entityManager.remove(animal);
            entityManager.flush();
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static CreateAnimalDto updateAnimal(int id, UpdateAnimalDto updateAnimalDto)  {
        try {
            entityManager.getTransaction().begin();
            Animal animal = entityManager.find(Animal.class, id);

            if ((animal.getImage() == null && updateAnimalDto.getImage() != null) || (animal.getImage() != null && !Objects.equals(updateAnimalDto.getImage(), animal.getImage().toString()))) {

                if (updateAnimalDto.getImage() == null || updateAnimalDto.getImage().isEmpty()) {
                    animal.setImage(null);
                }
                String imageBase64 = updateAnimalDto.getImage();
                byte[] imageBytes = DatatypeConverter.parseBase64Binary(imageBase64);
                // Convert the byte array to a Blob object
                Blob imageBlob = new SerialBlob(imageBytes);
                animal.setImage(imageBlob);
            }

            if (!updateAnimalDto.getName().isEmpty()) {
                animal.setName(updateAnimalDto.getName());
            }
            if (updateAnimalDto.getAge() > 0) {
                animal.setAge(updateAnimalDto.getAge());
            }
            if (updateAnimalDto.getWeight() > 0) {
                animal.setWeight(updateAnimalDto.getWeight());
            }
            if (!updateAnimalDto.getGender().isEmpty()) {
                animal.setGender(updateAnimalDto.getGender());
            }
            if (!updateAnimalDto.getDescription().isEmpty()) {
                animal.setDescription(updateAnimalDto.getDescription());
            }
            entityManager.persist(animal);
            entityManager.getTransaction().commit();
            CreateAnimalDto createAnimalDto = createAnimalDtoFromAnimal(animal);
            return createAnimalDto;
        }catch (SQLException e) {
            // Handle SQL-related exceptions
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }


    public static void updateAnimalStatus(int animalId, String status) {
        try {
            Animal animal = entityManager.find(Animal.class, animalId);
            if (animal != null) {
                animal.setStatus(status);
                entityManager.getTransaction().begin();
                entityManager.persist(animal);
                entityManager.getTransaction().commit();
            }
        }catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static CreateAnimalDto createAnimalDtoFromAnimal(Animal animal) throws SQLException {
        CreateAnimalDto createAnimalDto = new CreateAnimalDto(animal);
        User owner = animal.getOwner();
        if (owner != null) {
            UserDetailsDto animalUserDto = new UserDetailsDto();
            animalUserDto.setFullName(owner.getFullName());
            animalUserDto.setId(owner.getId());
            animalUserDto.setPhone(owner.getPhone());
            animalUserDto.setEmail(owner.getEmail());
            createAnimalDto.setOwner(animalUserDto);
        }

        if (animal.getImage() != null) {
            try {
                Blob imageBlob = animal.getImage(); // get the Blob object from somewhere
                byte[] imageBytes = imageBlob.getBytes(1L, (int) imageBlob.length());
                String base64String = DatatypeConverter.printBase64Binary(imageBytes);
                createAnimalDto.setImage(base64String);
            } catch (SQLException e) {
                System.err.println("Error while handling Blob: " + e.getMessage());
                e.printStackTrace();
            }
        }
        return createAnimalDto;
    }
}
