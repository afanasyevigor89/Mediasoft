package tests;

import clients.UserAPI;
import com.github.javafaker.Faker;
import dot.CreatedProduct;
import dot.NewProduct;
import dot.ProductData;
import dot.UpdateProduct;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import settings.DatabaseConnectionFactory;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.UUID;

import static io.qameta.allure.Allure.step;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UpdateProductTest {

    private final UserAPI userAPI = new UserAPI();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Faker faker = new Faker(Locale.ENGLISH);
    private CreatedProduct createdProduct;
    private Connection dbConnection;

    public UpdateProductTest() {
    }

    @BeforeEach
    void setUp() throws JsonProcessingException, SQLException {
        dbConnection = DatabaseConnectionFactory.getConnectionWithTransaction();

        NewProduct newProduct = new NewProduct.Builder()
                .name(faker.food().vegetable())
                .article(UUID.randomUUID())
                .category("VEGETABLES")
                .dictionary("vegetable")
                .price(faker.number().numberBetween(0, 1000))
                .qty(faker.number().numberBetween(0, 50))
                .build();

        String requestBody = objectMapper.writeValueAsString(newProduct);
        Response response = userAPI.createProduct(requestBody);
        createdProduct = objectMapper.readValue(response.getBody().asString(), CreatedProduct.class);
    }

    @Test
    void testUpdateProduct() throws JsonProcessingException {
        UpdateProduct updateProduct = new UpdateProduct.Builder()
                .id(createdProduct.getId())
                .name("Carrot")
                .price(120.12)
                .qty(12.12)
                .build();
        String requestBody = objectMapper.writeValueAsString(updateProduct);
        Response response = userAPI.updateProduct(requestBody);
        ProductData productData = objectMapper.readValue(response.getBody().asString(), ProductData.class);
        assertNotNull(productData.getLast_qty_changed(), "Дата изменения кол-ва должна быть заполнена");

        step("Проверяем запись в БД", () -> {
            String sql = "SELECT * FROM product WHERE id = ?";
            try (PreparedStatement pstmt = dbConnection.prepareStatement(sql)) {
                pstmt.setObject(1, createdProduct.getId(), java.sql.Types.OTHER);

                try (ResultSet rs = pstmt.executeQuery()) {

                    assertAll(() -> {
                        assertTrue(rs.next(), "Продукт не найден в БД");
                        assertEquals(updateProduct.getName(), rs.getString("name"));
                        assertEquals(updateProduct.getPrice(), rs.getDouble("price"));
                        assertEquals(updateProduct.getQty(), rs.getDouble("qty"));
                        assertEquals(updateProduct.getInsertedAt(), rs.getTimestamp("inserted_at"));
                        assertEquals(updateProduct.getLast_qty_changed(), rs.getTimestamp("last_qty_changed"));
                    });
                }
            }
        });
    }

    @Test
    void testUpdateProductWithoutID() throws JsonProcessingException {
        UpdateProduct updateProduct = new UpdateProduct.Builder()
                .name("Carrot")
                .price(120.12)
                .qty(12.12)
                .build();
        String requestBody = objectMapper.writeValueAsString(updateProduct);
        Response response = userAPI.updateProduct(requestBody);

        assertEquals(400, response.getStatusCode());
    }

    @AfterEach
    void tearDown() throws SQLException {
        dbConnection.close();
        userAPI.deleteProduct(createdProduct.getId());
    }
}
