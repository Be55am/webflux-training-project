package com.wiredbraincoffee.productapiannotation;

import com.wiredbraincoffee.productapiannotation.controller.ProductEvent;
import com.wiredbraincoffee.productapiannotation.model.Product;
import com.wiredbraincoffee.productapiannotation.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * @author bessam on 27/12/2020
 */

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class Junit5applicationContextTest {
    private WebTestClient client;
    private List<Product> expectedProducts;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void beforeEach() {

        //Bind to the controller method
        this.client = WebTestClient
                //this is the difference between this and the Junit5ControllerTest class
                .bindToApplicationContext(applicationContext)
                .configureClient()
                .baseUrl("/products")
                .build();

        this.expectedProducts = productRepository
                .findAll()
                .log()
                .collectList()
                .block();
    }

    @Test
    void testGetAllProducts() {
        client
                .get()
                .exchange()
                .expectStatus()
                .isOk()
                .expectBodyList(Product.class)
                .isEqualTo(expectedProducts);
    }

    @Test
    void testProductInvalidIdNotFound() {
        client
                .get()
                .uri("/aaa")
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    void testProductIdFound() {
        client
                .get()
                .uri("/" + expectedProducts.get(0).getId())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Product.class)
                .isEqualTo(expectedProducts.get(0));
    }

    @Test
    void testProductEvents() {
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
