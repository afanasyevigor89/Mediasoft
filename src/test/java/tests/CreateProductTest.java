package tests;


import clients.HibernateConfig;
import clients.UserAPI;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import net.datafaker.Faker;
import dto.CreatedProduct;
import dto.NewProduct;
import entity.ProductsEntity;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import repository.ProductRepository;
import settings.Category;
import settings.DatabaseConnectionFactory;
import settings.StatusCode;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;


class CreateProductTest {



    private final UserAPI userAPI = new UserAPI();
    JsonMapper objectMapper = JsonMapper.builder()
            .addModule(new JavaTimeModule())
            .build();
    private final Faker faker = new Faker(Locale.ENGLISH);
    private Connection dbConnection;
    UUID article = UUID.randomUUID();
    private CreatedProduct createdProduct;

    @BeforeEach
    void setUp() throws SQLException {

        dbConnection = DatabaseConnectionFactory.getConnectionWithTransaction();
    }

    @Test
    void testCreateNewFruits() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        NewProduct newProduct = createValidProduct(Category.FRUITS.getName(), article);

        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);

        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);

        step("Проверяем поля в ответе", () -> {
            assertAll(() -> {
                assertEquals(newProduct.getName(), createdProduct.getName());
                assertEquals(newProduct.getArticle(), createdProduct.getArticle());
                assertEquals(newProduct.getCategory(), createdProduct.getCategory());
                assertEquals(newProduct.getPrice(), createdProduct.getPrice());
                assertEquals(newProduct.getQty(), createdProduct.getQty());
            });
        });


    }

    @Test
    void testCreateNewVegetables() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {

        NewProduct newProduct = createValidProduct(Category.VEGETABLES.getName(), article);

        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);

        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);


        step("Проверяем поля в ответе", () -> {
            assertAll(() -> {
                assertEquals(newProduct.getName(), createdProduct.getName());
                assertEquals(newProduct.getArticle(), createdProduct.getArticle());
                assertEquals(newProduct.getCategory(), createdProduct.getCategory());
                assertEquals(newProduct.getPrice(), createdProduct.getPrice());
                assertEquals(newProduct.getQty(), createdProduct.getQty());
            });
        });
    }

    @Test
    void testCreateProductWithDuplicateArticle() throws JsonProcessingException, com.fasterxml.jackson.core.JsonProcessingException {
        UUID duplicateArticle = UUID.randomUUID();
        NewProduct newProduct = createValidProduct("FRUITS", duplicateArticle);
        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);
        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);

        NewProduct duplicateProduct = createValidProduct("FRUITS", duplicateArticle);
        String duplicateRequestBody = objectMapper.writeValueAsString(duplicateProduct);
        Response response2 = userAPI.createProduct(duplicateRequestBody);


        step("Проверяем статус код ответа", () -> {
            assertEquals(StatusCode.BAD_REQUEST.getCode(), response2.getStatusCode());
        });
    }

    @AfterEach
    void tearDown() throws SQLException {
        dbConnection.close();
        userAPI.deleteProduct(createdProduct.getId());
    }

    private NewProduct createValidProduct(String category, UUID article) {
        return NewProduct.builder()
                .article(article)
                .name(faker.food().fruit())
                .category(category)
                .dictionary("test dict")
                .price(new BigDecimal(faker.commerce().price(1,1000).replace(",", ".")))
                .qty(new BigDecimal(faker.number().randomDouble(2, 1, 50) + ""))
                .build();
    }
}
