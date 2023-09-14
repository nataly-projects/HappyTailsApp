package animals.adoptions.categories;

import animals.adoptions.EntityManagerFactoryProvider;
import animals.adoptions.entities.Category;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.TypedQuery;

import java.util.Iterator;
import java.util.List;

public class CategoriesService {
    private static final EntityManagerFactory entityManagerFactory = EntityManagerFactoryProvider.getEntityManagerFactory();
    private static final EntityManager entityManager = entityManagerFactory.createEntityManager();

    public static List<Category> getCategories() {
        try {
            TypedQuery<Category> query = entityManager.createQuery("SELECT c FROM Category c", Category.class);
            List<Category> categories = query.getResultList();

            // Iterate over each category and remove it from the database if it has no animals
            Iterator<Category> iterator = categories.iterator();
            while (iterator.hasNext()) {
                Category category = iterator.next();
                TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(a) FROM Animal a WHERE a.category = :category", Long.class);
                countQuery.setParameter("category", category);
                Long count = countQuery.getSingleResult();
                if (count == 0) {
                    entityManager.getTransaction().begin();
                    entityManager.remove(category);
                    entityManager.getTransaction().commit();
                    iterator.remove();
                }
            }
            return categories;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    public static Category addCategory(Category category) {
        try {
            entityManager.getTransaction().begin();
            entityManager.persist(category);
            entityManager.getTransaction().commit();

            TypedQuery<Category> query = entityManager.createQuery("SELECT c FROM Category c WHERE c.name = :name", Category.class);
            query.setParameter("name", category.getName());
            Category newCategory = query.getSingleResult();
            return newCategory;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


}
