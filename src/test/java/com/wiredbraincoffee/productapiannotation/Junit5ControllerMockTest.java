package com.wiredbraincoffee.productapiannotation;

import com.wiredbraincoffee.productapiannotation.controller.ProductController;
import com.wiredbraincoffee.productapiannotation.controller.ProductEvent;
import com.wiredbraincoffee.productapiannotation.model.Product;
import com.wiredbraincoffee.productapiannotation.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

/**
 * @author bessam on 27/12/2020
 */

/**
 * in this class we mock the repository it can be used in the unit tests
 */
@ExtendWith(SpringExtension.class)
public class Junit5ControllerMockTest {

    private WebTestClient client;
    private List<Product> expectedProducts;

    @MockBean
    private ProductRepository productRepository;

    @BeforeEach
    void beforeEach(){

        this.expectedProducts = Arrays.asList(
                new Product("1","Big Latte", 2.99),
                new Product("2", "Black coffee", 1.99)
        );

        //Bind to the controller method
        this.client = WebTestClient
                // u can autowire the product repository to the product Controller directly
                .bindToController(new ProductController(productRepository))
                .configureClient()
                .baseUrl("/products")
                .build();

    }

    @Test
    void testGetAllProducts(){

        when(productRepository.findAll()).thenReturn(Flux.fromIterable(expectedProducts));

        client
                .get()
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .isEqualTo(expectedProducts);
    }

    @Test
    void testProductInvalidIdNotFound(){

        String id = "aaa";
        when(productRepository.findById(id)).thenReturn(Mono.empty());

        client
                .get()
                .uri("/{id}",id)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testProductIdFound(){
        when(productRepository.findById(expectedProducts.get(0).getId())).thenReturn(Mono.just(expectedProducts.get(0)));
        client
                .get()
                .uri("/"+expectedProducts.get(0).getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .isEqualTo(expectedProducts.get(0));
    }

    @Test
    void testProductEvents(){
        ProductEvent expectedEvent = new ProductEvent(0L, "Product Event");

        FluxExchangeResult<ProductEvent> result = client
                .get()
                .uri("/events")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .returnResult(ProductEvent.class);
        StepVerifier
                .create(result.getResponseBody())
                .expectNextCount(2)
                .consumeNextWith(productEvent ->
                        assertEquals(Long.valueOf(2), productEvent.getEventId()))
                .thenCancel()
                .verify();
    }

}
