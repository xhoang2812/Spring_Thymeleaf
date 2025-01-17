package com.poly.du_an_tot_nghiep_f6.service.impl;

import com.poly.du_an_tot_nghiep_f6.entity.Category;
import com.poly.du_an_tot_nghiep_f6.repository.CategoryRepo;
import com.poly.du_an_tot_nghiep_f6.service.ICategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements ICategoryService {

    private final CategoryRepo categoryRepo;

    @Override
    public List<Category> findAllByStatusEquals() {
        return categoryRepo.findAllByStatusEquals();
    }

    @Override
    public List<Category> findAll() {
        return categoryRepo.findAll();
    }

    @Override
    public Category findById(int id) {
        return categoryRepo.findById(id).orElseThrow(() -> new EntityNotFoundException("Category Not Found"));
    }

    @Override
    public void updateStatus(int id) {
        Category category = findById(id);
        if (category.getStatus() == 1) {
            category.setStatus(0);
        } else {
            category.setStatus(1);
        }
        category.setDateUpdate(new Date());
        categoryRepo.save(category);
    }


    @Override
    public Category add(Category category) {
        category.setDateCreate(new Date());
        return categoryRepo.save(category);
    }

    @Override
    public Category update(Category category) {
        return categoryRepo.findById(category.getId())
                .map(o -> {
                    o.setName(category.getName());
                    o.setDateUpdate(new Date());
                    o.setStatus(category.getStatus());
                    return categoryRepo.save(o);
                }).orElseThrow(() -> new EntityNotFoundException("Category Not Found"));
    }

    @Override
    public Category findByName(String name) {
        return categoryRepo.findByName(name);
    }
}
