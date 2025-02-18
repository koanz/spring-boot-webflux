package com.ideas.springboot.webflux.app.controllers;

import com.ideas.springboot.webflux.app.documents.Product;
import com.ideas.springboot.webflux.app.repositories.ProductRepository;
import com.ideas.springboot.webflux.app.services.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

@Controller
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService service;

    @GetMapping("/")
    public String getAll(Model model) {
        Flux<Product> products = service.findAll();

        model.addAttribute("products", products);
        model.addAttribute("title", "List of Products");
        return "list";
    }

    @GetMapping("/list-datadriver")
    public String listDataDriver(Model model) {
        Flux<Product> products = service.findAllDataDriver();

        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
        model.addAttribute("title", "List of Products");
        return "list";
    }
}
