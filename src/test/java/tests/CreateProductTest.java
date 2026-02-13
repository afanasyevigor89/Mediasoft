package tests;

import clients.UserAPI;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.github.javafaker.Faker;
import dto.CreatedProduct;
import dto.NewProduct;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.Category;
import settings.DatabaseConnectionFactory;
import settings.StatusCode;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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

        step("Проверяем запись в БД", () -> {
            String sql = "SELECT * FROM product WHERE id = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                pstmt.setObject(1, createdProduct.getId(), java.sql.Types.OTHER);

                try (ResultSet rs = pstmt.executeQuery()) {

                    assertTrue(rs.next(), "Продукт не найден в БД");
                    assertEquals(newProduct.getName(), rs.getString("name"));
                    assertEquals(newProduct.getArticle().toString(), rs.getString("article"));
                    assertEquals(newProduct.getCategory(), rs.getString("category"));
                    assertEquals(newProduct.getPrice(), rs.getBigDecimal("price"));
                    assertEquals(newProduct.getQty(), rs.getBigDecimal("qty"));
                    assertEquals(createdProduct.getInsertedAt(), rs.getObject("inserted_at", OffsetDateTime.class)
                            .toLocalDateTime(), "Время создания продукта не совпадает");
                }
            }
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
