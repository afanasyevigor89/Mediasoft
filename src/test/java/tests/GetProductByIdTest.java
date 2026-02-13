package tests;

import clients.UserAPI;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import dto.CreatedProduct;
import dto.NewProduct;
import dto.ProductData;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.Category;
import settings.StatusCode;

import java.math.BigDecimal;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;

public class GetProductByIdTest {
    private final UserAPI userAPI = new UserAPI();
    JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final Faker faker = new Faker(Locale.ENGLISH);
    private CreatedProduct createdProduct;

    @BeforeEach
    void setUp() throws com.fasterxml.jackson.core.JsonProcessingException {

        NewProduct newProduct = NewProduct.builder()
                .name(faker.food().vegetable())
                .article(UUID.randomUUID())
                .category(Category.VEGETABLES.getName())
                .dictionary("vegetable")
                .price(new BigDecimal(faker.commerce().price(1,1000).replace(",", ".")))
                .qty(new BigDecimal(faker.number().randomDouble(2, 1, 50) + ""))
                .build();
        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);

        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);
    }

    @Test
    void testGetProductById() throws com.fasterxml.jackson.core.JsonProcessingException {
        Response response = userAPI.getProductById(createdProduct.getId());
        ProductData productData = objectMapper.readValue(response.getBody().asString(), ProductData.class);

        step("Проверка тела ответа", () -> {
            assertAll(() -> {
                assertEquals(StatusCode.OK.getCode(), response.getStatusCode());
                assertNotNull(productData.getId());
                assertEquals(productData.getName(), createdProduct.getName(), "Название товара указано неверно, ожидалось: " + createdProduct.getName());
                assertEquals(productData.getCategory(), createdProduct.getCategory(), "Категория товара указана неверно, ожидалось: " + createdProduct.getCategory());
                assertEquals(productData.getArticle(), createdProduct.getArticle().toString(), "Артикул товара указан неверно, ожидалось: " + createdProduct.getArticle());
                assertEquals(productData.getPrice(), createdProduct.getPrice(), "Цена товара указана неверно, ожидалось: " + createdProduct.getPrice());
                assertEquals(productData.getQty(), createdProduct.getQty(), "Кол-во товара указано неверно, ожидалось: " + createdProduct.getQty());
                assertEquals(productData.getInsertedAt(), createdProduct.getInsertedAt(), "Дата создания товара указана неверно, ожидалось: " + createdProduct.getInsertedAt());
                assertEquals("RUB", productData.getCurrency(), "Валюта товара указана неверно, ожидалось: " + createdProduct.getCurrency());
            });
        });
    }

    @Test
    void testGetNotExistProduct() {
        Response response = userAPI.getProductById(UUID.randomUUID());
        assertEquals(StatusCode.BAD_REQUEST.getCode(), response.getStatusCode());
    }

    @AfterEach
    void tearDown() {
        userAPI.deleteProduct(createdProduct.getId());
    }

}
