package com.its.springjwt.controllers;


import com.its.springjwt.models.Category;
import com.its.springjwt.models.User;
import com.its.springjwt.models.Video;
import com.its.springjwt.payload.response.MessageResponse;
import com.its.springjwt.repository.CategoryRepository;
import com.its.springjwt.security.services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepo;

    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public String createCategory(@RequestBody Category category){
        categoryRepo.save(category);
        return "Category created successfully: " + category.getName();
    }

    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/edit/{id}")
    public Category editCategory(@RequestBody Category category, @PathVariable long id){
        Optional<Category> categoryOptional = categoryRepo.findById(id);

        if (!categoryOptional.isPresent()) {
            throw new UserNotFoundException("id-" + id);
        } else {
            Category newCategory = categoryOptional.get();
            newCategory.setName(category.getName());
            categoryRepo.save(newCategory);
            return newCategory;
        }
    }

    @CrossOrigin
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<HttpStatus> deleteCategory(@PathVariable long id) {
        Optional<Category> categoryOptional = categoryRepo.findById(id);
        if (!categoryOptional.isPresent()) {
            throw new UserNotFoundException("id-" + id);
        }
        categoryRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/all")
    public List<Category> getCategories() {
        {
            return categoryRepo.findAll();
        }
    }

    @GetMapping("/testresponse")
    public ResponseEntity<String> testController()
    {
        {
            return ResponseEntity.status(HttpStatus.OK).body("ok");
        }
    }

    @CrossOrigin
    @GetMapping("/find/{id}")
    public Optional<Category> findCategory(@PathVariable long id) {
        Optional<Category> categoryOptional = categoryRepo.findById(id);

        if (!categoryOptional.isPresent()) {
            throw new UserNotFoundException("id-" + id);
        }

        return categoryRepo.findById(id);
    }

}
