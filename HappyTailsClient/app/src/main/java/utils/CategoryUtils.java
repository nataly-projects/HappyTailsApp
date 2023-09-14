package utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import models.Category;

public class CategoryUtils {

    public static Map<String, Category> createCategoryMap(List<Category> categories) {
        Map<String, Category> categoryMap = new HashMap<>();
        for (Category category : categories) {
            categoryMap.put(category.getName(), category);
        }
        return categoryMap;
    }
}
