package com.ideas.springboot.webflux.app;

import com.ideas.springboot.webflux.app.documents.Category;
import com.ideas.springboot.webflux.app.repositories.CategoryRepository;
import com.ideas.springboot.webflux.app.repositories.ProductRepository;
import com.ideas.springboot.webflux.app.documents.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	@Autowired
	private ProductRepository productRepository;

	@Autowired
	private CategoryRepository categoryRepository;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("products").subscribe();
		mongoTemplate.dropCollection("categories").subscribe();

		Category techonology = new Category("Technology");
		Category travel = new Category("Travel");
		Category accesories = new Category("Accesories");

		Flux.just(techonology, travel, accesories)
				.flatMap(categoryRepository::save)
				.doOnNext(c -> logger.info("Category created: " + c.getName()))
				.thenMany(
						Flux.just(
								new Product("Notebook", 255.99, techonology),
								new Product("Backpack", 85.50, travel),
								new Product("Mouse Logitech Superlight", 120.0, techonology),
								new Product("HyperX Cloudflight", 145.0, techonology),
								new Product("JBL Charge 4", 132.0, techonology),
								new Product("Sony Vaio i3 1.4 8gb 500gb SSD", 285.0, techonology),
								new Product("Samsung S23 FE 8gb 128GB", 515.0, techonology),
								new Product("Keyboard Logitech p200", 38.0, techonology),
								new Product("Smart TV Samsung s7000 50", 432.0, techonology),
								new Product("Keychain Independent 50 Anniversary", 18.99, accesories)
						).flatMap(product -> {
							product.setCreatedAt(new Date());
							return productRepository.save(product);
						})
				)
		.subscribe(product -> logger.info("Insert of Producto: " + product.getId()));
	}
}
