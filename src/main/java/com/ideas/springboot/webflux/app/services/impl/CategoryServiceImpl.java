package com.ideas.springboot.webflux.app.services.impl;

import com.ideas.springboot.webflux.app.documents.Category;
import com.ideas.springboot.webflux.app.repositories.CategoryRepository;
import com.ideas.springboot.webflux.app.services.CategoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@Service
public class CategoryServiceImpl implements CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Autowired
    private CategoryRepository repository;

    @Override
    public Flux<Category> findAll() {
        return repository.findAll();
    }

    @Override
    public Mono<Category> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Category> save(Category category) {
        category.setCreatedAt(new Date());
        return repository.save(category);
    }

    @Override
    public Mono<Category> findByName(String name) {
        return repository.findByName(name);
    }
}
