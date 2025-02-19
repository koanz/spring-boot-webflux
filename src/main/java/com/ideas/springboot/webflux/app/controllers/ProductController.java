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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Controller
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService service;

    @GetMapping({"/list", "/"})
    public String getAll(Model model) {
        Flux<Product> products = service.findAll();

        model.addAttribute("products", products);
        model.addAttribute("title", "List of Products");
        return "list";
    }

    // Data driver List with buffer size configuration: 2
    @GetMapping("/list-datadriver")
    public String listDataDriver(Model model) {
        Flux<Product> products = service.findAllDataDriver();

        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
        model.addAttribute("title", "List of Products");
        return "list";
    }

    // Chunked List with no config.
    @GetMapping("/list-full")
    public String listFull(Model model) {
        Flux<Product> products = service.findAllWithFull();

        model.addAttribute("products", products);
        model.addAttribute("title", "List of Products");
        return "list";
    }

    @GetMapping("/list-chunked")
    public String listChunked(Model model) {
        Flux<Product> products = service.findAllWithFull();

        model.addAttribute("products", products);
        model.addAttribute("title", "List of Products Chunked");
        return "list";
    }

    @GetMapping("/form")
    public Mono<String> create(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("title", "Form of Product");
        return Mono.just("form");
    }

    @GetMapping("/form/{id}")
    public Mono<String> edit(@PathVariable String id, Model model) {
        model.addAttribute("product", service.findById(id));
        model.addAttribute("title", "Edit Product");
        return Mono.just("form");
    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> edit2(@PathVariable String id, Model model) {
        model.addAttribute("product", service.findById(id));
        model.addAttribute("title", "Edit Product");
        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> save(Product product) {
        return service.save(product).doOnNext(p -> {
            logger.info("Saved Product: " + p.getName());
        }).then(Mono.just("redirect:/list"));
    }

}
