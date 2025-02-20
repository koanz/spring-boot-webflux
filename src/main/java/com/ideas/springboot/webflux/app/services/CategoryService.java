package com.ideas.springboot.webflux.app.services;

import com.ideas.springboot.webflux.app.documents.Category;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CategoryService {
    public Flux<Category> findAll();
    public Mono<Category> findById(String id);

    public Mono<Category> save(Category category);

    public Mono<Category> findByName(String name);
}
