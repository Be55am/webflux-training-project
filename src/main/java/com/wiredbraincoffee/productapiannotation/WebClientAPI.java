package com.wiredbraincoffee.productapiannotation;

import com.wiredbraincoffee.productapiannotation.controller.ProductEvent;
import com.wiredbraincoffee.productapiannotation.model.Product;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @author bessam on 27/12/2020
 */


/**
 * need to be in a different project
 * https://app.pluralsight.com/course-player?clipId=d60aa181-ca9d-4a18-b1e0-144b0608e1e5
 */

public class WebClientAPI {

    private WebClient webClient;

    WebClientAPI() {
//        this.webClient = WebClient.create("http://localhost:8080/products");
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8080/products")
                .build();
    }

    public static void main(String[] args) {

        WebClientAPI api = new WebClientAPI();
        api.postNewProduct()
                .thenMany(api.getAllProducts())
                .take(1)
                .flatMap(product -> api.updateProduct(product.getId(), "White Tea", product.getPrice()))
                .flatMap(product -> api.deleteProduct(product.getId()))
                .thenMany(api.getAllProducts())
                .subscribe(System.out::println);
    }

    public Mono<ResponseEntity<Product>> postNewProduct() {
        return webClient
                .post()
                .body(Mono.just(new Product(null, "Black Tea", 2.49)), Product.class)
                .retrieve()
                .toEntity(Product.class)
                .doOnSuccess(productResponseEntity -> System.out.println("****** Post " + productResponseEntity));
    }

    public Flux<Product> getAllProducts() {
        return webClient
                .get()
                .retrieve()
                .bodyToFlux(Product.class)
                .doOnNext(productResponseEntity -> System.out.println("****** Get " + productResponseEntity));
    }

    public Mono<Product> updateProduct(String id, String name, double price) {
        return webClient
                .put()
                .uri("/{id}", id)
                .body(Mono.just(new Product(id, name, price)), Product.class)
                .retrieve()
                .bodyToMono(Product.class)
                .doOnSuccess(productResponseEntity -> System.out.println("****** Get " + productResponseEntity));
    }

    public Mono<Void> deleteProduct(String id) {
        return webClient
                .delete()
                .uri("/{id}", id)
                .retrieve()
                .bodyToMono(Void.class)
                .doOnSuccess(o -> System.out.println("******* DELETE " + o));
    }

    public Flux<ProductEvent> getAllEvents() {
        return webClient
                .get()
                .uri("/events")
                .retrieve()
                .bodyToFlux(ProductEvent.class);
    }
}
