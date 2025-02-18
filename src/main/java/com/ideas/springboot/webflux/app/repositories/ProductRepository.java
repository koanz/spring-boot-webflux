package com.ideas.springboot.webflux.app.repositories;

import com.ideas.springboot.webflux.app.documents.Product;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductRepository extends ReactiveMongoRepository<Product, String> {
}
