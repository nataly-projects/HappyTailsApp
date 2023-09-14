package animals.adoptions.users;

import animals.adoptions.EntityManagerFactoryProvider;
import animals.adoptions.GMailer;
import animals.adoptions.entities.User;

import javax.persistence.*;
import javax.sql.rowset.serial.SerialBlob;
import javax.xml.bind.DatatypeConverter;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.*;

public class UserService {

    private static final EntityManagerFactory entityManagerFactory = EntityManagerFactoryProvider.getEntityManagerFactory();
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();


    public static User getUserById(int id) {
        try {
            User user = entityManager.find(User.class, id);
            return user;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static void deleteUser(int id) {
        try {
            entityManager.getTransaction().begin();
            User user = entityManager.find(User.class, id);
            entityManager.remove(user);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    public static UpdateUserDto updateUser(int id, UpdateUserDto userUpdateDto) {
        try {
            entityManager.getTransaction().begin();

            User user = entityManager.find(User.class, id);
            // if image updated
            if ((user.getImage() == null && userUpdateDto.getImage() != null) || (user.getImage() != null && !Objects.equals(userUpdateDto.getImage(), user.getImage().toString()))) {

                if (userUpdateDto.getImage() == null || userUpdateDto.getImage().isEmpty()) {
                    user.setImage(null);
                }
                String imageBase64 = userUpdateDto.getImage();
                byte[] imageBytes = DatatypeConverter.parseBase64Binary(imageBase64);
                // Convert the byte array to a Blob object
                Blob imageBlob = new SerialBlob(imageBytes);
                user.setImage(imageBlob);
            }
            // if the user update the email - need to check if the update email is already exists
            if (!Objects.equals(user.getEmail(), userUpdateDto.getEmail())) {
                try {
                    User existingUserEmail = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                            .setParameter("email", userUpdateDto.getEmail())
                            .getSingleResult();
                    // the email is already exists
                    return null;

                // NoResultException is thrown by getSingleResult() method when the query executed with getSingleResult()
                // does not return any results. This can happen if there is no record in the database that matches the query criteria.
                } catch ( NoResultException e) {
                    // the updated email is not already exists - update the user.
                    if (!userUpdateDto.getFullName().isEmpty()) {
                        user.setFullName(userUpdateDto.getFullName());
                    }
                    if (!userUpdateDto.getEmail().isEmpty()) {
                        user.setEmail(userUpdateDto.getEmail());
                    }
                    if (!userUpdateDto.getPhone().isEmpty()) {
                        user.setPhone(userUpdateDto.getPhone());
                    }
                    user.setPassword(null);
                }
            }
            if (userUpdateDto.getFullName() != null && !userUpdateDto.getFullName().isEmpty()) {
                user.setFullName(userUpdateDto.getFullName());
            }
            if (userUpdateDto.getPhone() != null && !userUpdateDto.getPhone().isEmpty()) {
                user.setPhone(userUpdateDto.getPhone());
            }
            entityManager.persist(user);
            entityManager.getTransaction().commit();

            UpdateUserDto updatedUser = new UpdateUserDto(user);

            Blob imageBlob = user.getImage(); // get the Blob object from somewhere
            byte[] imageBytes = imageBlob.getBytes(1L, (int) imageBlob.length());
            String base64String = DatatypeConverter.printBase64Binary(imageBytes);
            updatedUser.setImage(base64String);
            return updatedUser;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static User signup(CreateUserDto createUserDto) {
        try {
            User existingUser = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", createUserDto.getEmail())
                    .getSingleResult();
            // the email is already exists
           return null;
        }  catch (NoResultException e) {
            // Handle the case where no user with the given email is found
            User user = new User(createUserDto);
            entityManager.getTransaction().begin();
            try {
                entityManager.persist(user);
                entityManager.flush();
                entityManager.getTransaction().commit();
                User persistedUser = new User();
                persistedUser.setId(user.getId());
                persistedUser.setFullName(user.getFullName());
                persistedUser.setPhone(user.getPhone());
                persistedUser.setEmail(user.getEmail());
                persistedUser.setImage(user.getImage());
                persistedUser.setPassword(null);
                return persistedUser;
            } catch (Exception ex) {
                entityManager.getTransaction().rollback();
            }
        }
        return null;
    }

    public static CreateUserDto signing(SigningDto signingDto) {
        try{
            TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
            query.setParameter("email", signingDto.getEmail());
            User user;
            try{
                user = query.getSingleResult();
                if(user.getEmail().equals(signingDto.getEmail()) && checkPassword(signingDto.getPassword(), user.getPassword())) {
                    CreateUserDto newUser = new CreateUserDto(user);
                    if (user.getImage() != null) {
                        Blob imageBlob = user.getImage(); // get the Blob object from somewhere
                        byte[] imageBytes = imageBlob.getBytes(1L, (int)imageBlob.length());
                        String base64String = DatatypeConverter.printBase64Binary(imageBytes);
                        newUser.setImage(base64String);
                    }
                    newUser.setPassword(null);
                    return newUser;
                }
            }  catch (NoResultException | SQLException e) {
                throw new RuntimeException(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static void contactUs(ContactUsDto contactUsDto) {
        try {
            // create the body of the mail
            String message = "an application was received from the Happy Tails app from:\n "
                    + "name: " + contactUsDto.getName()
                    + "\nemail: " + contactUsDto.getEmail() + "\nthe message:\n" + contactUsDto.getMessage();

            // send mail to the happy tails system email with the message
            new GMailer().sendContactUsMail(message);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static String resetPasswordCode(String email) {
        try {
            User existingUser = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", email)
                    .getSingleResult();
            // Generate a random code.
            String code = generateRandomCode();

            // send to the user reset password email with the code
            String message = "Your password reset code is: " + code + ".";
            new GMailer().sendResetPasswordMail(email, message);
            return code;
        }  catch (NoResultException e) {
            // Handle the case where no user with the given email is found
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void resetPassword(ResetPasswordDto resetPasswordDto) {
        try {
            User existingUser = entityManager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class)
                    .setParameter("email", resetPasswordDto.getEmail())
                    .getSingleResult();

            // save the new password in the user entity
            existingUser.setPassword(resetPasswordDto.getPassword());
            entityManager.getTransaction().begin();
            entityManager.persist(existingUser);
            entityManager.flush();
            entityManager.getTransaction().commit();
        }  catch (NoResultException ex) {
            ex.printStackTrace();
            throw  ex;
        } catch (Exception ex) {
            ex.printStackTrace();
            throw ex;
        }
    }

    //Helpers functions

    private static String generateRandomCode() {
        // Generate a random alphanumeric code.
        String alphanumeric = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder codeBuilder = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            int index = random.nextInt(alphanumeric.length());
            codeBuilder.append(alphanumeric.charAt(index));
        }
        return codeBuilder.toString();
    }


    public static boolean checkPassword(String encryptedPassword, String storedEncryptedPassword) {
        // check if the password that send matches the password that store in the db for the given email
        return encryptedPassword.equals(storedEncryptedPassword);
    }




}
