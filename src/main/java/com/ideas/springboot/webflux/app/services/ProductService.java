package com.ideas.springboot.webflux.app.services;

import com.ideas.springboot.webflux.app.documents.Product;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface ProductService {
    public Flux<Product> findAll();
    public Flux<Product> findAllDataDriver();
    public Flux<Product> findAllWithFull();
    public Flux<Product> findAllWithFullRest();
    public Mono<Product> findById(String id);
    public Mono<Product> save(Product product);
    public Mono<Void> delete(Product product);
}
