package com.ideas.springboot.webflux.app.controllers;

import com.ideas.springboot.webflux.app.documents.Category;
import com.ideas.springboot.webflux.app.documents.Product;
import com.ideas.springboot.webflux.app.services.CategoryService;
import com.ideas.springboot.webflux.app.services.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import org.thymeleaf.spring6.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Locale;

@SessionAttributes("product")
@Controller
public class ProductController {
    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private MessageSource messageSource;

    @ModelAttribute("categories")
    public Flux<Category> categories() {
        return categoryService.findAll();
    }

    @GetMapping({"/list", "/"})
    public Mono<String> getAll(Model model, Locale locale) {
        Flux<Product> products = productService.findAll();

        model.addAttribute("products", products);
        model.addAttribute("title", messageSource.getMessage("form.title.list", null, locale));
        products.subscribe(p -> logger.info(p.getName()));

        return Mono.just("product/list");
    }

    // Data driver List with buffer size configuration: 2
    @GetMapping("/list-datadriver")
    public Mono<String> listDataDriver(Model model, Locale locale) {
        Flux<Product> products = productService.findAllDataDriver();

        model.addAttribute("products", new ReactiveDataDriverContextVariable(products, 2));
        model.addAttribute("title", messageSource.getMessage("form.title.list", null, locale));

        return Mono.just("product/list");
    }

    // Chunked List with no config.
    @GetMapping("/list-full")
    public Mono<String> listFull(Model model, Locale locale) {
        Flux<Product> products = productService.findAllWithFull();

        model.addAttribute("products", products);
        model.addAttribute("title", messageSource.getMessage("form.title.list", null, locale));

        return Mono.just("product/list");
    }

    @GetMapping("/list-chunked")
    public Mono<String> listChunked(Model model, Locale locale) {
        logger.info("Chunked List");
        Flux<Product> products = productService.findAllWithFull();

        model.addAttribute("products", products);
        model.addAttribute("title", messageSource.getMessage("form.title.list", null, locale));

        return Mono.just("product/list");
    }

    @GetMapping("/form")
    public Mono<String> create(Model model, Locale locale) {
        logger.info("retrieving form-product template");

        model.addAttribute("product", new Product());
        model.addAttribute("title", messageSource.getMessage("form.title.form", null, locale));
        model.addAttribute("button", "Save");

        return Mono.just("product/form");
    }

    @GetMapping("/form/{id}")
    public Mono<String> edit(@PathVariable String id, Model model, Locale locale) {
        Mono<Product> product = productService.findById(id).doOnNext(p -> logger.info("Producto: " + p.getName()))
                .defaultIfEmpty(new Product());

        model.addAttribute("product", product);
        model.addAttribute("title", messageSource.getMessage("form.title.edit", null, locale));
        model.addAttribute("button", "Save");

        return Mono.just("product/form");
    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> edit2(@PathVariable String id, Model model, Locale locale) {
        return productService.findById(id).doOnNext(p -> {
                    logger.info("Product: " + p.getName());
                    model.addAttribute("button", "Save");
                    model.addAttribute("title", "Edit Product");
                    model.addAttribute("product", p);
                }).defaultIfEmpty(new Product())
                .flatMap(p -> {
                    if(p.getId() == null) {
                        return Mono.error(new InterruptedException("The product doesn't exist"));
                    }

                    return Mono.just(p);
                })
                .then(Mono.just("product/form"))
                .onErrorResume(ex -> Mono.just("redirect:/list?error=the+product+does+not+exist"));
    }

    @GetMapping("/show/{id}")
    public Mono<String> show(@PathVariable String id, Model model, Locale locale) {
        return productService.findById(id).doOnNext(p -> {
            logger.info("Product to display: " + p.getName());
            model.addAttribute("product", p);
            model.addAttribute("title", "Details of Product");
        }).switchIfEmpty(Mono.just(new Product()))
                .flatMap(p -> {
                    if(p.getId() == null) {
                        return Mono.error(new InterruptedException("The product doesn't exist"));
                    }

                    return Mono.just(p);
                })
                .then(Mono.just("product/show"));
    }

    @PostMapping("/form")
    public Mono<String> save(@Valid Product product, BindingResult result, Model model, SessionStatus status) {
        if(result.hasErrors()) {
            logger.error("Errors while trying to save a Product");
            model.addAttribute("button", "Save");
            model.addAttribute("title", "Errors in Form Product");

            return Mono.just("product/form");
        }

        logger.info("Time to save");
        status.setComplete();

        Mono<Category> categoryMono = categoryService.findById(product.getCategory().getId());

        return categoryMono.flatMap(category -> {
            product.setCategory(category);
            return productService.save(product);
        }).doOnNext(p -> {
            logger.info("Assigned Category: " + p.getCategory().getName() + " Id: " + p.getCategory().getId());
            logger.info("Saved Product: " + p.getName() + " Id: " + p.getId());
        }).then(Mono.just("redirect:/list?success=Product+successfully+saved"));
    }

    @GetMapping("/delete/{id}")
    public Mono<String> delete(@PathVariable String id) {
        return productService.findById(id)
                .defaultIfEmpty(new Product())
                .flatMap(p -> {
                    if(p.getId() == null) {
                        logger.error("Cannot delete a Product that doesn't exist (id): " + id);

                        return Mono.error(new InterruptedException("Cannot delete a Product that does not exist"));
                    }

                    return Mono.just(p);
                })
                .flatMap(productService::delete).then(Mono.just("redirect:/list?success=Product+successfully+deleted"))
                .onErrorResume(ex -> Mono.just("redirect:/list?error=the+product+to+be+eliminated+does+not+exist"));
    }

}
