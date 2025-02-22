package com.ideas.springboot.webflux.app.services.impl;

import com.ideas.springboot.webflux.app.controllers.ProductController;
import com.ideas.springboot.webflux.app.documents.Product;
import com.ideas.springboot.webflux.app.repositories.ProductRepository;
import com.ideas.springboot.webflux.app.services.CategoryService;
import com.ideas.springboot.webflux.app.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Date;

@Service
public class ProductServiceImpl implements ProductService {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductRepository repository;

    @Override
    public Flux<Product> findAll() {
        Flux<Product> products = repository
                .findAll()
                .map(product -> {
            product.setName(product.getName().toUpperCase());
            return product;
        });

        products.subscribe(product -> logger.info("Name of product: " + product.getName() + " - created at: " + product.getCreatedAt()));

        return products;
    }

    @Override
    public Flux<Product> findAllDataDriver() {
        Flux<Product> products = repository
                .findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }).delayElements(Duration.ofSeconds(1));

        products.subscribe(product -> logger.info("Name of product: " + product.getName() + " - created at: " + product.getCreatedAt()));

        return products;
    }

    @Override
    public Flux<Product> findAllWithFull() {
        return repository
                .findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                }).repeat(5000);
    }

    // REST API Response
    @Override
    public Flux<Product> findAllWithFullRest() {
        return repository.findAll()
                .map(product -> {
                    product.setName(product.getName().toUpperCase());
                    return product;
                });
    }

    @Override
    public Mono<Product> findById(String id) {
        return repository.findById(id);
    }

    @Override
    public Mono<Product> save(Product product) {
        product.setCreatedAt(new Date());
        return repository.save(product);
    }

    @Override
    public Mono<Void> delete(Product product) {
        logger.info("Deleting Product: " + product.getId() + " " + product.getName());

        return repository.delete(product);
    }
}
