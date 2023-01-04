package com.leojava.springboot.webflux.app;

import java.util.Date;

import com.leojava.springboot.webflux.app.models.documents.Categoria;
import com.leojava.springboot.webflux.app.models.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;

import com.leojava.springboot.webflux.app.models.documents.Producto;

import reactor.core.publisher.Flux;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner{
	
	@Autowired
	private ProductoService service;
	
	@Autowired
	private ReactiveMongoTemplate mongoTemplate;
	
	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();

		Categoria electronico = new Categoria("Electronico");
		Categoria deportes = new Categoria("Deportes");
		Categoria computo = new Categoria("Computo");
		Categoria mobiliario = new Categoria("Mobiliario");

		Flux.just(electronico, deportes,computo,mobiliario)
						.flatMap(service::saveCategoria)
								.doOnNext(c ->{
									log.info("Categoria creada: " + c.getNombre() + "id: " + c.getId());
								}).thenMany(Flux.just(new Producto("Smart TV Samsung LCD", 552.5, electronico),
								new Producto("Smart TV LG LED", 550.0, electronico),
								new Producto("AppleTV LED", 950.2, electronico),
								new Producto("VIVO SmartPhone 6.5", 800.6, electronico),
								new Producto("Sony SmartPods TV LG LED", 350.9, computo),
								new Producto("HP Impresora RecargaContinua", 225.7, computo)
						)
						.flatMap(producto -> {
							producto.setCreateAt(new Date());
							return service.save(producto);
						})
				)
		.subscribe(producto -> log.info("insert: " + producto.getId() + " " + producto.getNombre()));
		
	}

}
