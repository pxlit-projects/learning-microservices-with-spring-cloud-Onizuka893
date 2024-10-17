package be.pxl.services.services;

import be.pxl.services.domain.Category;

import java.util.List;

public interface ICategoryService {
    List<Category> getAllCategories();
}
