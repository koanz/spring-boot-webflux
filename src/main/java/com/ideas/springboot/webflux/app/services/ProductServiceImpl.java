package com.ideas.springboot.webflux.app.services;

import com.ideas.springboot.webflux.app.controllers.ProductController;
import com.ideas.springboot.webflux.app.documents.Product;
import com.ideas.springboot.webflux.app.repositories.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

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
}
