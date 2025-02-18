package com.ideas.springboot.webflux.app;

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

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	@Autowired
	private ProductRepository dao;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private Logger logger = LoggerFactory.getLogger(this.getClass());

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("products").subscribe();

		Flux.just(
				new Product("Notebook", 255.99),
				new Product("Backpack", 85.50),
				new Product("Mouse Logitech Superlight", 120.0),
				new Product("HyperX Cloudflight", 145.0),
				new Product("JBL Charge 4", 132.0),
				new Product("Sony Vaio i3 1.4 8gb 500gb SSD", 285.0),
				new Product("Samsung S23 FE 8gb 128GB", 515.0),
				new Product("Keyboard Logitech p200", 38.0),
				new Product("Smart TV Samsung s7000 50", 432.0)
		).flatMap(product -> {
			product.setCreatedAt(new Date());
			return dao.save(product);
				})
		.subscribe(product -> logger.info("Insert of Producto: " + product.getId()));
	}
}
