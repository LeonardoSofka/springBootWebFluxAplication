package com.leojava.springboot.webflux.app.controllers;

import com.leojava.springboot.webflux.app.models.dao.ProductoDao;
import com.leojava.springboot.webflux.app.models.documents.Producto;
import com.leojava.springboot.webflux.app.models.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/productos")
public class ProductoRestController {

    @Autowired
    private ProductoService service;

    private static final Logger log = LoggerFactory.getLogger(ProductController.class);

    @GetMapping
    public Flux<Producto> index(){

        Flux<Producto> productos = service.findAll().map(producto -> {
            producto.setNombre(producto.getNombre().toUpperCase());
            return producto;
        }).doOnNext(prod -> log.info(prod.getNombre()));
        return productos;
    }

    @GetMapping("/{id}")
    public Mono<Producto> show(@PathVariable String id){
        //Mono<Producto> producto = dao.findById(id);
        Flux<Producto> productos = service.findAll();
        Mono<Producto> producto = productos.filter(p -> p.getId().equals(id))
                .next()
                .doOnNext(prod -> log.info(prod.getNombre()));
        return producto;
    }
}
