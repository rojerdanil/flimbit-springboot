package com.riseup.flimbit.service;

import com.riseup.flimbit.entity.ShareCategory;
import com.riseup.flimbit.repository.ShareCategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShareCategoryService {

    @Autowired
    private ShareCategoryRepository shareCategoryRepository;

    public ShareCategory createCategory(ShareCategory category) {
        return shareCategoryRepository.save(category);
    }

    public List<ShareCategory> getAllCategories() {
        return shareCategoryRepository.findAll();
    }

    public ShareCategory getCategoryById(int id) {
        return shareCategoryRepository.findById(id).orElse(null);
    }

    public void deleteCategory(int id) {
        shareCategoryRepository.deleteById(id);
    }
}
