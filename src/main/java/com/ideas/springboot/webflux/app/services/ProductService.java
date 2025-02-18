package com.ideas.springboot.webflux.app.services;

import com.ideas.springboot.webflux.app.documents.Product;
import reactor.core.publisher.Flux;

public interface ProductService {
    public Flux<Product> findAll();

    public Flux<Product> findAllDataDriver();
}
