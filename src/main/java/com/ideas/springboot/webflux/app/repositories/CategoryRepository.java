package com.ideas.springboot.webflux.app.repositories;

import com.ideas.springboot.webflux.app.documents.Category;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface CategoryRepository extends ReactiveMongoRepository<Category, String> {
        Mono<Category> findByName(String name);
}
